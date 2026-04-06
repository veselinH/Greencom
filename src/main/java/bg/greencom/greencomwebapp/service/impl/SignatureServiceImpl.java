package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.model.entity.PlanEntity;
import bg.greencom.greencomwebapp.model.entity.SignatureEntity;
import bg.greencom.greencomwebapp.model.entity.UserEntity;
import bg.greencom.greencomwebapp.model.user.GreencomUserDetails;
import bg.greencom.greencomwebapp.model.view.VoicePlanViewModel;
import bg.greencom.greencomwebapp.repository.SignatureRepository;
import bg.greencom.greencomwebapp.service.SignatureService;
import org.springframework.stereotype.Service;

@Service
public class SignatureServiceImpl implements SignatureService {
    private final SignatureRepository signatureRepository;

    public SignatureServiceImpl(SignatureRepository signatureRepository) {
        this.signatureRepository = signatureRepository;
    }

    @Override
    public void addSignature(PlanEntity planEntity, UserEntity userEntity, byte[] signature) {
        SignatureEntity signatureToAdd = new SignatureEntity();
        signatureToAdd
                .setUser(userEntity)
                .setPlan(planEntity)
                .setSignature(signature);

        signatureRepository.saveAndFlush(signatureToAdd);
    }
}
