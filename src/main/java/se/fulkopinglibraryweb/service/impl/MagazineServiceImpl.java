package se.fulkopinglibraryweb.service.impl;

import se.fulkopinglibraryweb.exception.MagazineServiceException;
import se.fulkopinglibraryweb.model.Magazine;
import se.fulkopinglibraryweb.repository.MagazineRepository;
import se.fulkopinglibraryweb.service.interfaces.MagazineService;
import se.fulkopinglibraryweb.service.search.SearchCriteria;
import com.google.gson.Gson;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MagazineServiceImpl implements MagazineService {
    private final MagazineRepository magazineRepository;
    private final Gson gson = new Gson();

    public MagazineServiceImpl(MagazineRepository magazineRepository) {
        this.magazineRepository = magazineRepository;
    }

    @Override
    public List<String> getAllMagazines() {
        List<Magazine> magazines = magazineRepository.findAll();
        return magazines.stream()
            .map(gson::toJson)
            .collect(Collectors.toList());
    }

    private Magazine enrichMagazine(Magazine magazine) {
        // Add any additional enrichment logic here
        return magazine;
    }

    @Override
    public List<String> search(SearchCriteria criteria) {
        List<Magazine> items = magazineRepository.search(criteria);
        return items.stream()
            .map(this::enrichMagazine)
            .map(gson::toJson)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<String> getMagazineById(String id) {
        try {
            Optional<Magazine> magazineOpt = magazineRepository.findById(id);
            return magazineOpt.map(gson::toJson);
        } catch (Exception e) {
            throw new MagazineServiceException(
                "getMagazineById", 
                id, 
                MagazineServiceException.ErrorType.DATABASE_ERROR,
                "Failed to retrieve magazine with id: " + id,
                e
            );
        }
    }

    @Override
    public void addMagazine(String magazineDetails) {
        try {
            Magazine magazine = gson.fromJson(magazineDetails, Magazine.class);
            if (magazine == null) {
                throw new MagazineServiceException(
                    "addMagazine",
                    magazineDetails,
                    MagazineServiceException.ErrorType.INVALID_INPUT,
                    "Invalid magazine details format"
                );
            }
            magazineRepository.save(magazine);
        } catch (Exception e) {
            throw new MagazineServiceException(
                "addMagazine",
                magazineDetails,
                MagazineServiceException.ErrorType.DATABASE_ERROR,
                "Failed to add magazine",
                e
            );
        }
    }

    @Override
    public void updateMagazine(String id, String magazineDetails) {
        try {
            if (!magazineRepository.existsById(id)) {
                throw new MagazineServiceException(
                    "updateMagazine",
                    id,
                    MagazineServiceException.ErrorType.NOT_FOUND,
                    "Magazine not found with id: " + id
                );
            }
            Magazine magazine = gson.fromJson(magazineDetails, Magazine.class);
            if (magazine == null) {
                throw new MagazineServiceException(
                    "updateMagazine",
                    magazineDetails,
                    MagazineServiceException.ErrorType.INVALID_INPUT,
                    "Invalid magazine details format"
                );
            }
            magazineRepository.save(magazine);
        } catch (Exception e) {
            throw new MagazineServiceException(
                "updateMagazine",
                id,
                MagazineServiceException.ErrorType.DATABASE_ERROR,
                "Failed to update magazine with id: " + id,
                e
            );
        }
    }

    @Override
    public List<String> searchMagazines(String query) {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setSearchTerm(query);
        List<Magazine> items = magazineRepository.search(criteria);
        return items.stream()
            .map(gson::toJson)
            .collect(Collectors.toList());
    }

    @Override
    public List<String> getLatestIssues(int limit) {
        List<Magazine> latest = magazineRepository.findLatest(limit);
        return latest.stream()
            .map(gson::toJson)
            .collect(Collectors.toList());
    }

    @Override
    public boolean isAvailableForLending(String id) {
        return magazineRepository.existsById(id);
    }

    @Override
    public void deleteMagazine(String id) {
        try {
            if (!magazineRepository.existsById(id)) {
                throw new MagazineServiceException(
                    "deleteMagazine",
                    id,
                    MagazineServiceException.ErrorType.NOT_FOUND,
                    "Magazine not found with id: " + id
                );
            }
            magazineRepository.deleteById(id);
        } catch (Exception e) {
            throw new MagazineServiceException(
                "deleteMagazine",
                id,
                MagazineServiceException.ErrorType.DATABASE_ERROR,
                "Failed to delete magazine with id: " + id,
                e
            );
        }
    }
}
