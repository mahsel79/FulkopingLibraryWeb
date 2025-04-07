package se.fulkopinglibraryweb.service;

import se.fulkopinglibraryweb.model.Magazine;
import se.fulkopinglibraryweb.repository.MagazineRepository;
import se.fulkopinglibraryweb.service.interfaces.AsyncMagazineService;
import se.fulkopinglibraryweb.service.interfaces.AsyncCrudOperations;
import se.fulkopinglibraryweb.service.search.SearchCriteria;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class AsyncMagazineServiceImpl implements AsyncMagazineService {
    private final MagazineRepository magazineRepository;

    public AsyncMagazineServiceImpl(MagazineRepository magazineRepository) {
        this.magazineRepository = magazineRepository;
    }

    @Override
    public CompletableFuture<Magazine> create(Magazine magazine) {
        return CompletableFuture.supplyAsync(() -> magazineRepository.save(magazine));
    }

    @Override
    public CompletableFuture<Magazine> getById(String id) {
        return CompletableFuture.supplyAsync(() -> magazineRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Magazine not found")));
    }

    @Override
    public CompletableFuture<Optional<Magazine>> read(String id) {
        return CompletableFuture.supplyAsync(() -> magazineRepository.findById(id));
    }

    @Override
    public CompletableFuture<Magazine> update(Magazine magazine) {
        return CompletableFuture.supplyAsync(() -> magazineRepository.save(magazine));
    }

    @Override
    public CompletableFuture<Void> delete(String id) {
        return CompletableFuture.runAsync(() -> magazineRepository.deleteById(id));
    }

    @Override
    public CompletableFuture<List<Magazine>> getAll() {
        return CompletableFuture.supplyAsync(() -> magazineRepository.findAll());
    }

    @Override
    public CompletableFuture<List<Magazine>> search(SearchCriteria criteria) {
        return CompletableFuture.supplyAsync(() -> magazineRepository.search(criteria));
    }

    @Override
    public CompletableFuture<Boolean> isAvailableForLending(String id) {
        return CompletableFuture.supplyAsync(() -> {
            Magazine magazine = magazineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Magazine not found"));
            return magazine.isAvailable();
        });
    }

    // AsyncMagazineService methods
    @Override
    public CompletableFuture<List<Magazine>> getAllMagazines() {
        return getAll();
    }

    @Override
    public CompletableFuture<Optional<Magazine>> getMagazineById(String id) {
        return read(id);
    }

    @Override
    public CompletableFuture<Magazine> addMagazine(Magazine magazine) {
        return create(magazine);
    }

    @Override
    public CompletableFuture<Magazine> updateMagazine(String id, Magazine magazine) {
        magazine.setId(id);
        return update(magazine);
    }

    @Override
    public CompletableFuture<Void> deleteMagazine(String id) {
        return delete(id);
    }

    @Override
    public CompletableFuture<List<Magazine>> searchMagazines(String query) {
        return CompletableFuture.supplyAsync(() -> {
            SearchCriteria criteria = new SearchCriteria();
            criteria.setSearchTerm(query);
            return magazineRepository.search(criteria);
        });
    }

    @Override
    public CompletableFuture<List<Magazine>> getLatestIssues(int limit) {
        return CompletableFuture.supplyAsync(() -> magazineRepository.findLatest(limit));
    }
}
