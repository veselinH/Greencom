package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.model.entity.InternetTypeEntity;
import bg.greencom.greencomwebapp.model.entity.enums.InternetTypeEnum;
import bg.greencom.greencomwebapp.repository.InternetTypeRepository;
import bg.greencom.greencomwebapp.service.InternetTypeService;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Service implementation for managing internet connection technologies and categories.
 */
@Service
public class InternetTypeServiceImpl implements InternetTypeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InternetTypeServiceImpl.class);

    private final InternetTypeRepository internetTypeRepository;

    public InternetTypeServiceImpl(InternetTypeRepository internetTypeRepository) {
        this.internetTypeRepository = internetTypeRepository;
    }

    @Override
    public void initialize() {
        if (internetTypeRepository.count() == 0) {
            InternetTypeEntity type1 = new InternetTypeEntity();
            InternetTypeEntity type2 = new InternetTypeEntity();

            type1.setName(InternetTypeEnum.FIBER_NET);
            type2.setName(InternetTypeEnum.VDSL);

            internetTypeRepository.saveAll(Set.of(type1, type2));
            LOGGER.info("Internet types initialized.");
        }
    }

    @Override
    public InternetTypeEntity findByName(InternetTypeEnum name) {
        return internetTypeRepository.findByName(name);
    }
}
