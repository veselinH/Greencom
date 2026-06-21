package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.client.LoyaltyFacade;
import bg.greencom.greencomwebapp.model.entity.AdditionalPackageEntity;
import bg.greencom.greencomwebapp.model.entity.ContractEntity;
import bg.greencom.greencomwebapp.model.entity.PlanEntity;
import bg.greencom.greencomwebapp.model.entity.UserEntity;
import org.hibernate.ObjectNotFoundException;
import bg.greencom.greencomwebapp.model.view.AdditionalPackageViewModel;
import bg.greencom.greencomwebapp.model.view.ContractViewModel;
import bg.greencom.greencomwebapp.repository.ContractRepository;
import bg.greencom.greencomwebapp.service.ContractService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Set;

/**
 * Handles contract lifecycle: creation (with loyalty earn), deactivation (with loyalty revoke),
 * PDF generation, and ownership checks. Loyalty calls are best-effort — a loyalty-service
 * outage never blocks contract sign/unsign.
 */
@Service
public class ContractServiceImpl implements ContractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContractServiceImpl.class);
    private static final String OBJECT_TYPE = "contract";

    private final ContractRepository contractRepository;
    private final ModelMapper modelMapper;
    private final TemplateEngine templateEngine;
    private final LoyaltyFacade loyaltyFacade;

    public ContractServiceImpl(ContractRepository contractRepository, ModelMapper modelMapper,
                               TemplateEngine templateEngine, LoyaltyFacade loyaltyFacade) {
        this.contractRepository = contractRepository;
        this.modelMapper = modelMapper;
        this.templateEngine = templateEngine;
        this.loyaltyFacade = loyaltyFacade;
    }

    @Override
    public void addContract(PlanEntity planEntity, UserEntity userEntity,
                            Set<AdditionalPackageEntity> additionalPackageEntities, byte[] signature) {
        ContractEntity newContract = new ContractEntity();
        newContract
                .setUser(userEntity)
                .setPlan(planEntity)
                .setSignedOn(LocalDate.now())
                .setSignSignature(signature);
        newContract.setActive(true);
        newContract.setAdditionalPackageEntities(additionalPackageEntities);

        contractRepository.saveAndFlush(newContract);
        LOGGER.info("User {} signed new contract with plan name {}", userEntity.getUsername(), planEntity.getName());

        // Award loyalty points (best-effort) for signing the plan.
        loyaltyFacade.earn(userEntity.getUsername(), toPoints(planEntity.getPrice()));
        LOGGER.info("Loyalty points awarded for contract signing of user {} successfully", userEntity.getUsername());
    }

    @Override
    public ContractViewModel findById(Long contractId) {
        ContractEntity contractEntity = contractRepository.findById(contractId)
                .orElseThrow(() -> new ObjectNotFoundException(contractId, OBJECT_TYPE));

        ContractViewModel contractView = modelMapper.map(contractEntity, ContractViewModel.class);

        if (contractEntity.getAdditionalPackageEntities() != null) {
            for (AdditionalPackageEntity pkg : contractEntity.getAdditionalPackageEntities()) {
                contractView.getAdditionalPackageViewModels().add(
                        modelMapper.map(pkg, AdditionalPackageViewModel.class));
            }
        }

        return contractView;
    }

    @Override
    public void deactivateContract(Long contractId, byte[] unsignSignature) {
        ContractEntity contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ObjectNotFoundException(contractId, OBJECT_TYPE));

        contract
                .setUnsignSignature(unsignSignature)
                .setUnsignedOn(LocalDate.now())
                .setActive(false);

        contractRepository.saveAndFlush(contract);
        LOGGER.info("Contract {} deactivated successfully for user {}", contractId, contract.getUser().getUsername());

        // Revoke the loyalty points that were earned for this plan (best-effort).
        loyaltyFacade.revoke(contract.getUser().getUsername(), toPoints(contract.getPlan().getPrice()));
        LOGGER.info("Loyalty points for contract {} revoked successfully", contractId);
    }

    @Override
    public byte[] generateContractPdf(Long contractId) {
        ContractEntity contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ObjectNotFoundException(contractId, OBJECT_TYPE));

        Context context = new Context();
        context.setVariable("contract", contract);
        context.setVariable("downloadDate", LocalDate.now());

        try {
            byte[] logoBytes = new ClassPathResource("static/images/logoz.png").getInputStream().readAllBytes();
            String logoBase64 = java.util.Base64.getEncoder().encodeToString(logoBytes);
            context.setVariable("logoImage", "data:image/png;base64," + logoBase64);
        } catch (Exception e) {
            // Logo is optional — the PDF renders without it if the resource is missing.
            LOGGER.warn("Could not embed logo in contract PDF (contract {}): {}", contractId, e.getMessage());
        }

        if (contract.getSignSignature() != null) {
            String signatureBase64 = java.util.Base64.getEncoder().encodeToString(contract.getSignSignature());
            context.setVariable("signatureImage", "data:image/png;base64," + signatureBase64);
        }

        String htmlContent = templateEngine.process("documents/contract", context);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.getFontResolver().addFont(
                    new ClassPathResource("fonts/Helvetica.ttf").getURL().toString(), true);
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Contract PDF generation failed for contract " + contractId, e);
        }
    }

    @Override
    public String getContractDownloadFileName(Long id) {
        ContractEntity contract = contractRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(id, OBJECT_TYPE));

        String planName = contract.getPlan().getName()
                .replaceAll("[^a-zA-Z0-9-_]", "_");

        return planName + "_" + id + "_" + LocalDate.now() + ".pdf";
    }

    @Override
    public boolean isContractOwner(Long contractId, String username) {
        return contractRepository.findById(contractId)
                .map(contract -> contract.getUser() != null
                        && contract.getUser().getUsername().equals(username))
                .orElse(false);
    }

    /** Monthly price rounded to the nearest integer gives the loyalty points to award/revoke. */
    private int toPoints(BigDecimal price) {
        if (price == null) {
            return 0;
        }
        return price.setScale(0, RoundingMode.HALF_UP).intValue();
    }
}
