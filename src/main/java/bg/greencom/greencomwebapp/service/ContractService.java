package bg.greencom.greencomwebapp.service;

import bg.greencom.greencomwebapp.model.entity.PlanEntity;
import bg.greencom.greencomwebapp.model.entity.UserEntity;
import bg.greencom.greencomwebapp.model.view.ContractViewModel;

public interface ContractService {
    void addContract(PlanEntity planEntity, UserEntity userEntity, byte[] signature);

    ContractViewModel findById(Long contractId);

    void deactivateContract(Long contractId, byte[] unsignSignature);
}
