package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.model.entity.TelevisionTypeEntity;
import bg.greencom.greencomwebapp.model.entity.enums.TelevisionTypeEnum;
import bg.greencom.greencomwebapp.repository.TelevisionTypeRepository;
import bg.greencom.greencomwebapp.service.TelevisionTypeService;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Service implementation for managing television types.
 * <p>
 * This service handles business logic related to {@link TelevisionTypeEntity},
 * including system startup database initialization.
 * </p>
 */
@Service
public class TelevisionTypeServiceImpl implements TelevisionTypeService {

    private final TelevisionTypeRepository televisionTypeRepository;

    public TelevisionTypeServiceImpl(TelevisionTypeRepository televisionTypeRepository) {
        this.televisionTypeRepository = televisionTypeRepository;
    }

    /**
     * Initializes the television types in the database if it is empty.
     * <p>
     * Checks if any television types exist. If the repository count is zero,
     * it seeds the default values (INTERACTIVE and SATELLITE) into the database.
     * </p>
     */
    @Override
    public void initialize() {
        if (televisionTypeRepository.count() == 0) {
            TelevisionTypeEntity type1 = new TelevisionTypeEntity();
            TelevisionTypeEntity type2 = new TelevisionTypeEntity();

            type1.setName(TelevisionTypeEnum.INTERACTIVE);
            type2.setName(TelevisionTypeEnum.SATELLITE);

            televisionTypeRepository.saveAll(Set.of(type1, type2));
        }
    }
}
