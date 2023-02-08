package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.model.entity.AdditionalPackageEntity;
import bg.greencom.greencomwebapp.model.entity.enums.AdditionalPackageEnum;
import bg.greencom.greencomwebapp.repository.AdditionalPackageRepository;
import bg.greencom.greencomwebapp.service.AdditionalPackageService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;

@Service
public class AdditionalPackageServiceImpl implements AdditionalPackageService {

    private static final BigDecimal ADULT_XTRA_PRICE = BigDecimal.valueOf(1.99);
    private static final BigDecimal SPORT_XTRA_PRICE = BigDecimal.valueOf(11.99);
    private static final BigDecimal MOVIE_XTRA_PRICE = BigDecimal.valueOf(7.99);

    private final AdditionalPackageRepository additionalPackageRepository;

    public AdditionalPackageServiceImpl(AdditionalPackageRepository additionalPackageRepository) {
        this.additionalPackageRepository = additionalPackageRepository;
    }

    @Override
    public void initialize() {
       if (additionalPackageRepository.count() == 0){
           AdditionalPackageEntity extra1 = new AdditionalPackageEntity();
           AdditionalPackageEntity extra2 = new AdditionalPackageEntity();
           AdditionalPackageEntity extra3 = new AdditionalPackageEntity();

           extra1.setName(AdditionalPackageEnum.ADULT_XTRA);
           extra1.setPrice(ADULT_XTRA_PRICE);

           extra2.setName(AdditionalPackageEnum.SPORT_XTRA);
           extra2.setPrice(SPORT_XTRA_PRICE);

           extra3.setName(AdditionalPackageEnum.MOVIES_XTRA);
           extra3.setPrice(MOVIE_XTRA_PRICE);

           additionalPackageRepository
                   .saveAll(Set.of(
                           extra1,
                           extra2,
                           extra3));
       }
    }
}
