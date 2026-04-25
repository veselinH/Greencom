package bg.greencom.greencomwebapp.service;

import bg.greencom.greencomwebapp.model.entity.AdditionalPackageEntity;
import bg.greencom.greencomwebapp.model.view.AdditionalPackageViewModel;
import bg.greencom.greencomwebapp.repository.AdditionalPackageRepository;

import java.util.List;
import java.util.Set;


public interface AdditionalPackageService {
    void initialize();

    List<AdditionalPackageViewModel> findAllOrderedByName();

    Set<AdditionalPackageEntity> findAllByIds(Set<Long> additionalPackageIds);
}
