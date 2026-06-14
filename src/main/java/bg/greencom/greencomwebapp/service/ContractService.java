package bg.greencom.greencomwebapp.service;

import bg.greencom.greencomwebapp.model.entity.AdditionalPackageEntity;
import bg.greencom.greencomwebapp.model.entity.PlanEntity;
import bg.greencom.greencomwebapp.model.entity.UserEntity;
import bg.greencom.greencomwebapp.model.view.ContractViewModel;

import java.util.Set;

public interface ContractService {
    void addContract(PlanEntity planEntity, UserEntity userEntity, Set<AdditionalPackageEntity> additionalPackageEntities, byte[] signature);

    ContractViewModel findById(Long contractId);

    void deactivateContract(Long contractId, byte[] unsignSignature);

    byte[] generateContractPdf(Long contractId);
}
