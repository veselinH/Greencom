package bg.greencom.greencomwebapp.service;

import bg.greencom.greencomwebapp.model.entity.AdditionalPackageEntity;
import bg.greencom.greencomwebapp.model.view.AdditionalPackageViewModel;

import java.util.List;
import java.util.Set;

/**
 * Manages add-on packages (e.g. extra channels, premium bundles) that can be
 * attached to a television plan contract.
 */
public interface AdditionalPackageService {

    /** Seeds all {@code AdditionalPackageEnum} values into the database if not already present. */
    void initialize();

    /** Returns all additional packages sorted alphabetically by name. */
    List<AdditionalPackageViewModel> findAllOrderedByName();

    /** Returns the {@code AdditionalPackageEntity} records whose IDs are in the given set. */
    Set<AdditionalPackageEntity> findAllByIds(Set<Long> additionalPackageIds);
}
