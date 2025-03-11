package se.fulkopinglibraryweb.repository;

import se.fulkopinglibraryweb.model.Magazine;
import se.fulkopinglibraryweb.service.search.SearchCriteria;

import java.util.List;

/**
 * Repository interface for Magazine entities.
 * Extends FirestoreRepository to inherit Firestore-specific operations.
 */
public interface MagazineRepository extends FirestoreRepository<Magazine, String> {
    
    /**
     * Find magazines by publisher.
     *
     * @param publisher The publisher to search for
     * @return A list of matching magazines
     */
    List<Magazine> findByPublisher(String publisher);
    
    /**
     * Find magazines by ISSN.
     *
     * @param issn The ISSN to search for
     * @return A list of matching magazines
     */
    List<Magazine> findByIssn(String issn);
    
    /**
     * Find magazines by category.
     *
     * @param category The category to search for
     * @return A list of matching magazines
     */
    List<Magazine> findByCategory(String category);
    
    /**
     * Find magazines by publication year.
     *
     * @param year The publication year to search for
     * @return A list of matching magazines
     */
    List<Magazine> findByPublicationYear(int year);
    
    /**
     * Find magazines by frequency.
     *
     * @param frequency The frequency to search for (e.g., "weekly", "monthly")
     * @return A list of matching magazines
     */
    List<Magazine> findByFrequency(String frequency);

    /**
     * Find the latest magazine issues, ordered by publication date.
     *
     * @param limit The maximum number of issues to return
     * @return A list of the latest magazine issues
     */
    List<Magazine> findLatest(int limit);

    /**
     * Search magazines using the provided search criteria.
     *
     * @param criteria The search criteria to use
     * @return A list of matching magazines
     */
    List<Magazine> search(SearchCriteria criteria);
}
