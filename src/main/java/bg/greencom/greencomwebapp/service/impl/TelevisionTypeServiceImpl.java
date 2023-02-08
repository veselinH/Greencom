package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.model.entity.TelevisionTypeEntity;
import bg.greencom.greencomwebapp.model.entity.enums.TelevisionTypeEnum;
import bg.greencom.greencomwebapp.repository.TelevisionTypeRepository;
import bg.greencom.greencomwebapp.service.TelevisionTypeService;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class TelevisionTypeServiceImpl implements TelevisionTypeService {

    private final TelevisionTypeRepository televisionTypeRepository;

    public TelevisionTypeServiceImpl(TelevisionTypeRepository televisionTypeRepository) {
        this.televisionTypeRepository = televisionTypeRepository;
    }

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
