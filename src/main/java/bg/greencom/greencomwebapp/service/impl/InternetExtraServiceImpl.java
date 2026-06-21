package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.model.entity.InternetExtrasEntity;
import bg.greencom.greencomwebapp.model.entity.enums.InternetExtraEnum;
import bg.greencom.greencomwebapp.repository.InternetExtraRepository;
import bg.greencom.greencomwebapp.service.InternetExtraService;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Service implementation managing available internet add-on packages and features.
 */
@Service
public class InternetExtraServiceImpl implements InternetExtraService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InternetExtraServiceImpl.class);

    private final InternetExtraRepository internetExtraRepository;

    public InternetExtraServiceImpl(InternetExtraRepository internetExtraRepository) {
        this.internetExtraRepository = internetExtraRepository;
    }

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
            LOGGER.info("Internet extras initialized.");
        }
    }

    @Override
    public InternetExtrasEntity findByName(InternetExtraEnum internetExtraEnum) {
        return internetExtraRepository
                .findByName(internetExtraEnum)
                .orElse(null);
    }
}
