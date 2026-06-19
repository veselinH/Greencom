package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.model.entity.MobileExtraEntity;
import bg.greencom.greencomwebapp.model.entity.enums.MobileExtraEnum;
import bg.greencom.greencomwebapp.repository.MobileExtraRepository;
import bg.greencom.greencomwebapp.service.MobileExtraService;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Service implementation managing available mobile add-on packages and features.
 */
@Service
public class MobileExtraServiceImpl implements MobileExtraService {

    private final MobileExtraRepository mobileExtraRepository;

    public MobileExtraServiceImpl(MobileExtraRepository mobileExtraRepository) {
        this.mobileExtraRepository = mobileExtraRepository;
    }

    /**
     * Seeds the database with default mobile extras on system startup if empty.
     * Inserts MOBILE_TV, MOBILE_MUSIC, and GREENCOM_PREMIUM_CARD configurations.
     */
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

    /**
     * Finds a specific mobile extra configuration by its matching enum identifier.
     * Returns null if the specified type does not exist in the database.
     */
    @Override
    public MobileExtraEntity findByName(MobileExtraEnum mobileExtraEnum) {

        return mobileExtraRepository
                .findByName(mobileExtraEnum)
                .orElse(null);
    }
}
