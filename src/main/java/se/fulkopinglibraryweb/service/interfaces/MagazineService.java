package se.fulkopinglibraryweb.service.interfaces;

import se.fulkopinglibraryweb.service.search.SearchCriteria;

import java.util.List;
import java.util.Optional;

public interface MagazineService {
    List<String> getAllMagazines();
    Optional<String> getMagazineById(String id);
    void addMagazine(String magazineDetails);
    void updateMagazine(String id, String magazineDetails);
    void deleteMagazine(String id);
    List<String> search(SearchCriteria criteria);
    List<String> searchMagazines(String query);
    List<String> getLatestIssues(int limit);
    boolean isAvailableForLending(String id);
}
