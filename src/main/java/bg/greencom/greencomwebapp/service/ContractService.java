package bg.greencom.greencomwebapp.service;

import bg.greencom.greencomwebapp.model.entity.AdditionalPackageEntity;
import bg.greencom.greencomwebapp.model.entity.PlanEntity;
import bg.greencom.greencomwebapp.model.entity.UserEntity;
import bg.greencom.greencomwebapp.model.view.ContractViewModel;

import java.util.Set;

/**
 * Manages the full contract lifecycle: creation, deactivation, PDF generation,
 * download naming, and ownership checks.
 */
public interface ContractService {

    /**
     * Creates and persists a new active contract for the given user and plan, attaching
     * any additional packages and the sign-signature image. Awards loyalty points
     * best-effort — a loyalty-service outage does not prevent the contract from being saved.
     */
    void addContract(PlanEntity planEntity, UserEntity userEntity,
                     Set<AdditionalPackageEntity> additionalPackageEntities, byte[] signature);

    /**
     * Returns a full view of the contract including its additional packages.
     * Throws {@code ObjectNotFoundException} if no contract with the given ID exists.
     */
    ContractViewModel findById(Long contractId);

    /**
     * Marks the contract inactive, records the unsign-signature image, and revokes the
     * loyalty points that were originally earned for the plan (best-effort).
     * Throws {@code ObjectNotFoundException} if no contract with the given ID exists.
     */
    void deactivateContract(Long contractId, byte[] unsignSignature);

    /**
     * Renders the contract as a PDF byte array using the Thymeleaf + Flying Saucer
     * pipeline. Throws {@code RuntimeException} if PDF generation fails.
     */
    byte[] generateContractPdf(Long contractId);

    /**
     * Returns a sanitised download filename in the form {@code PlanName_id_date.pdf}.
     * Special characters in the plan name are replaced with underscores.
     */
    String getContractDownloadFileName(Long id);

    /**
     * Returns {@code true} if the given username is the owner of the contract,
     * {@code false} if the contract does not exist or belongs to another user.
     */
    boolean isContractOwner(Long contractId, String username);
}
