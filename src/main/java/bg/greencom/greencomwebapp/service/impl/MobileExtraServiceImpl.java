package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.model.entity.MobileExtraEntity;
import bg.greencom.greencomwebapp.model.entity.enums.MobileExtraEnum;
import bg.greencom.greencomwebapp.repository.MobileExtraRepository;
import bg.greencom.greencomwebapp.service.MobileExtraService;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class MobileExtraServiceImpl implements MobileExtraService {

    private final MobileExtraRepository mobileExtraRepository;

    public MobileExtraServiceImpl(MobileExtraRepository mobileExtraRepository) {
        this.mobileExtraRepository = mobileExtraRepository;
    }


    @Override
    public void initialize() {
        if (mobileExtraRepository.count() == 0) {
            MobileExtraEntity mobileExtraEntity1 = new MobileExtraEntity();
            MobileExtraEntity mobileExtraEntity2 = new MobileExtraEntity();
            MobileExtraEntity mobileExtraEntity3 = new MobileExtraEntity();

            mobileExtraEntity1.setName(MobileExtraEnum.MOBILE_TV);
            mobileExtraEntity2.setName(MobileExtraEnum.MOBILE_MUSIC);
            mobileExtraEntity3.setName(MobileExtraEnum.GREENCOM_PREMIUM_CARD);


            mobileExtraRepository
                    .saveAll(Set.of(
                            mobileExtraEntity1,
                            mobileExtraEntity2,
                            mobileExtraEntity3));
        }
    }
}
