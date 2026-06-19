package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.model.entity.AdditionalPackageEntity;
import bg.greencom.greencomwebapp.model.entity.enums.AdditionalPackageEnum;
import bg.greencom.greencomwebapp.model.view.AdditionalPackageViewModel;
import bg.greencom.greencomwebapp.repository.AdditionalPackageRepository;
import bg.greencom.greencomwebapp.service.AdditionalPackageService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation managing commercial add-on options like movie, adult, and sports channels.
 */
@Service
public class AdditionalPackageServiceImpl implements AdditionalPackageService {

    private static final BigDecimal ADULT_XTRA_PRICE = BigDecimal.valueOf(1.99);
    private static final BigDecimal SPORT_XTRA_PRICE = BigDecimal.valueOf(11.99);
    private static final BigDecimal MOVIE_XTRA_PRICE = BigDecimal.valueOf(7.99);

    private final AdditionalPackageRepository additionalPackageRepository;

    public AdditionalPackageServiceImpl(AdditionalPackageRepository additionalPackageRepository) {
        this.additionalPackageRepository = additionalPackageRepository;
    }

    /**
     * Seeds base supplementary television options on system startup if empty.
     * Establishes initial commercial rates for ADULT_XTRA, SPORT_XTRA, and MOVIES_XTRA.
     */
    @Override
    public void initialize() {
        if (additionalPackageRepository.count() == 0) {
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

    /**
     * Gathers all available supplementary option configurations from persistence layers,
     * sorting results alphabetically by their descriptive names.
     */
    @Override
    public List<AdditionalPackageViewModel> findAllOrderedByName() {
        return additionalPackageRepository
                .findAllByOrderByNameAsc()
                .stream()
                .map(additionalPackageEntity -> {
                    AdditionalPackageViewModel additionalPackageViewModel = new AdditionalPackageViewModel();
                    additionalPackageViewModel.setName(additionalPackageEntity.getName().getValue());
                    additionalPackageViewModel.setPrice(additionalPackageEntity.getPrice());
                    additionalPackageViewModel.setId(additionalPackageEntity.getId());
                    return additionalPackageViewModel;
                })
                .collect(Collectors.toList());
    }

    /**
     * Fetches multiple entity configurations matching an explicit set of primary reference keys.
     */
    @Override
    public Set<AdditionalPackageEntity> findAllByIds(Set<Long> additionalPackageIds) {
        return new HashSet<>(additionalPackageRepository.findAllById(additionalPackageIds));
    }
}
