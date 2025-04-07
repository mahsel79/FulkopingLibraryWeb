package se.fulkopinglibraryweb.service.interfaces;

import se.fulkopinglibraryweb.model.Magazine;
import se.fulkopinglibraryweb.service.search.SearchCriteria;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface AsyncMagazineService extends AsyncCrudOperations<Magazine, String> {
    CompletableFuture<List<Magazine>> getAllMagazines();
    CompletableFuture<Optional<Magazine>> getMagazineById(String id);
    CompletableFuture<Magazine> addMagazine(Magazine magazine);
    CompletableFuture<Magazine> updateMagazine(String id, Magazine magazine);
    CompletableFuture<Void> deleteMagazine(String id);
    CompletableFuture<List<Magazine>> search(SearchCriteria criteria);
    CompletableFuture<List<Magazine>> searchMagazines(String query);
    CompletableFuture<List<Magazine>> getLatestIssues(int limit);
    CompletableFuture<Boolean> isAvailableForLending(String id);
}
