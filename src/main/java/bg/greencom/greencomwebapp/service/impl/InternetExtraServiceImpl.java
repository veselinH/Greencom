package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.model.entity.InternetExtrasEntity;
import bg.greencom.greencomwebapp.model.entity.enums.InternetExtraEnum;
import bg.greencom.greencomwebapp.repository.InternetExtraRepository;
import bg.greencom.greencomwebapp.service.InternetExtraService;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Service implementation managing available internet add-on packages and features.
 */
@Service
public class InternetExtraServiceImpl implements InternetExtraService {

    private final InternetExtraRepository internetExtraRepository;

    public InternetExtraServiceImpl(InternetExtraRepository internetExtraRepository) {
        this.internetExtraRepository = internetExtraRepository;
    }

    /**
     * Seeds the database with default internet extras on system startup if empty.
     * Inserts STATIC_IP, ANTI_VIRUS_PROGRAM, and WIFI_ROUTER configurations.
     */
    @Override
    public void initialize() {
        if (internetExtraRepository.count() == 0) {
            InternetExtrasEntity extra1 = new InternetExtrasEntity();
            InternetExtrasEntity extra2 = new InternetExtrasEntity();
            InternetExtrasEntity extra3 = new InternetExtrasEntity();

            extra1.setName(InternetExtraEnum.STATIC_IP);
            extra2.setName(InternetExtraEnum.ANTI_VIRUS_PROGRAM);
            extra3.setName(InternetExtraEnum.WIFI_ROUTER);

            internetExtraRepository
                    .saveAll(Set.of(
                            extra1,
                            extra2,
                            extra3
                    ));
        }
    }

    /**
     * Finds a specific internet extra configuration by its matching enum identifier.
     * Returns null if the specified type does not exist in the database.
     */
    @Override
    public InternetExtrasEntity findByName(InternetExtraEnum internetExtraEnum) {
        return internetExtraRepository
                .findByName(internetExtraEnum)
                .orElse(null);
    }
}
