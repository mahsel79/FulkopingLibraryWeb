package se.fulkopinglibraryweb.service.impl;

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
        Optional<Magazine> magazineOpt = magazineRepository.findById(id);
        return magazineOpt.map(gson::toJson);
    }

    @Override
    public void addMagazine(String magazineDetails) {
        Magazine magazine = gson.fromJson(magazineDetails, Magazine.class);
        magazineRepository.save(magazine);
    }

    @Override
    public void updateMagazine(String id, String magazineDetails) {
        if (magazineRepository.existsById(id)) {
            Magazine magazine = gson.fromJson(magazineDetails, Magazine.class);
            magazineRepository.save(magazine);
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
        magazineRepository.deleteById(id);
    }
}
