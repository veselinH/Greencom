package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.client.LoyaltyFacade;
import bg.greencom.greencomwebapp.model.entity.AdditionalPackageEntity;
import bg.greencom.greencomwebapp.model.entity.ContractEntity;
import bg.greencom.greencomwebapp.model.entity.PlanEntity;
import bg.greencom.greencomwebapp.model.entity.UserEntity;
import bg.greencom.greencomwebapp.model.view.AdditionalPackageViewModel;
import bg.greencom.greencomwebapp.model.view.ContractViewModel;
import bg.greencom.greencomwebapp.repository.ContractRepository;
import bg.greencom.greencomwebapp.service.ContractService;
import org.hibernate.ObjectNotFoundException;
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

    public ContractServiceImpl(ContractRepository contractRepository, ModelMapper modelMapper, TemplateEngine templateEngine, LoyaltyFacade loyaltyFacade) {
        this.contractRepository = contractRepository;
        this.modelMapper = modelMapper;
        this.templateEngine = templateEngine;
        this.loyaltyFacade = loyaltyFacade;
    }

    /**
     * Provisions a new customer contract, registers the signature byte stream,
     * and sends a best-effort call to credit standard profile loyalty points.
     */
    @Override
    public void addContract(PlanEntity planEntity, UserEntity userEntity, Set<AdditionalPackageEntity> additionalPackageEntities, byte[] signature) {
        ContractEntity newContract = new ContractEntity();
        newContract
                .setUser(userEntity)
                .setPlan(planEntity)
                .setSignedOn(LocalDate.now())
                .setSignSignature(signature);
        newContract.setActive(true);
        newContract.setAdditionalPackageEntities(additionalPackageEntities);

        contractRepository.saveAndFlush(newContract);

//      Award loyalty points (best-effort) for signing the plan.
        loyaltyFacade.earn(userEntity.getUsername(), toPoints(planEntity.getPrice()));
    }

    /**
     * Resolves a complete contract summary projection by identifier,
     * including mapped collections of all nested packages.
     */
    @Override
    public ContractViewModel findById(Long contractId) {
        ContractEntity contractEntity = contractRepository.findById(contractId)
                .orElseThrow(() -> new ObjectNotFoundException(contractId, OBJECT_TYPE));

        ContractViewModel contractView = modelMapper.map(contractEntity, ContractViewModel.class);

        if (contractEntity.getAdditionalPackageEntities() != null) {
            for (AdditionalPackageEntity additionalPackageEntity : contractEntity.getAdditionalPackageEntities()) {
                AdditionalPackageViewModel additionalPackageViewModel = modelMapper.map(additionalPackageEntity, AdditionalPackageViewModel.class);
                contractView.getAdditionalPackageViewModels().add(additionalPackageViewModel);
            }
        }

        return contractView;
    }

    /**
     * Terminates an active contract record using a cancellation signature,
     * and triggers a best-effort transaction request to revoke accumulated points.
     */
    @Override
    public void deactivateContract(Long contractId, byte[] unsignSignature) {
        ContractEntity contract = contractRepository
                .findById(contractId)
                .orElseThrow(
                        () -> new ObjectNotFoundException(contractId, OBJECT_TYPE));

        contract
                .setUnsignSignature(unsignSignature)
                .setUnsignedOn(LocalDate.now())
                .setActive(false);

        contractRepository.saveAndFlush(contract);

//      Revoke the loyalty points that were earned for this plan (best-effort).
        loyaltyFacade.revoke(contract.getUser().getUsername(), toPoints(contract.getPlan().getPrice()));
    }

    /** Loyalty points awarded/revoked for a plan: its monthly price rounded to the nearest whole point. */
    private int toPoints(BigDecimal price) {
        if (price == null) {
            return 0;
        }
        return price.setScale(0, RoundingMode.HALF_UP).intValue();
    }

    /**
     * Compiles contract data and signature streams into an HTML context,
     * parsing it into an isolated PDF byte stream using Flying Saucer IText.
     */
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

            renderer.getFontResolver().addFont(new ClassPathResource("fonts/Helvetica.ttf").getURL().toString(), true);
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Contract PDF generation failed for contract " + contractId, e);
        }
    }

    /**
     * Generates a sanitized file download name using safe characters,
     * specific contract indicators, and the request timeline stamp.
     */
    @Override
    public String getContractDownloadFileName(Long id) {
        ContractEntity contract = contractRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(id, OBJECT_TYPE));

        String planName = contract.getPlan().getName()
                .replaceAll("[^a-zA-Z0-9-_]", "_");

        return planName + "_" + id + "_" + LocalDate.now() + ".pdf";
    }

    /**
     * Validates if the currently authenticated username matches the assigned owner
     * of the requested contract record.
     */
    @Override
    public boolean isContractOwner(Long contractId, String username) {
        return contractRepository.findById(contractId)
                .map(contract -> contract.getUser() != null
                        && contract.getUser().getUsername().equals(username))
                .orElse(false);
    }
}
