package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.model.entity.ContractEntity;
import bg.greencom.greencomwebapp.model.entity.PlanEntity;
import bg.greencom.greencomwebapp.model.entity.UserEntity;
import bg.greencom.greencomwebapp.model.exception.ObjectNotFoundException;
import bg.greencom.greencomwebapp.model.view.ContractViewModel;
import bg.greencom.greencomwebapp.repository.ContractRepository;
import bg.greencom.greencomwebapp.service.ContractService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ContractServiceImpl implements ContractService {

    private static final String OBJECT_TYPE = "contract";

    private final ContractRepository contractRepository;
    private final ModelMapper modelMapper;

    public ContractServiceImpl(ContractRepository contractRepository, ModelMapper modelMapper) {
        this.contractRepository = contractRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public void addContract(PlanEntity planEntity, UserEntity userEntity, byte[] signature) {
        ContractEntity newContract = new ContractEntity();
        newContract
                .setUser(userEntity)
                .setPlan(planEntity)
                .setSignedOn(LocalDate.now())
                .setSignSignature(signature);
        newContract.setActive(true);

        contractRepository.saveAndFlush(newContract);
    }

    @Override
    public ContractViewModel findById(Long contractId) {
        return modelMapper.map(contractRepository.findById(contractId).orElse(null), ContractViewModel.class);
    }

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
    }
}
