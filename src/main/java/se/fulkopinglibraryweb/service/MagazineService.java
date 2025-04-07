package se.fulkopinglibraryweb.service;

import se.fulkopinglibraryweb.model.Magazine;
import se.fulkopinglibraryweb.repository.MagazineRepository;
import jakarta.inject.Inject;
import se.fulkopinglibraryweb.utils.LoggerUtil;
import se.fulkopinglibraryweb.service.search.SearchCriteria;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;

@Service
public class MagazineService {
    private final Logger logger = LoggerUtil.getLogger(MagazineService.class);
    private final MagazineRepository magazineRepository;

    @Inject
    public MagazineService(MagazineRepository magazineRepository) {
        this.magazineRepository = magazineRepository;
    }

    public Optional<Magazine> findById(String id) {
        logger.info("findById - Finding magazine by id: {}", id);
        try {
            return magazineRepository.findById(id);
        } catch (Exception e) {
            LoggerUtil.logError(MagazineService.class, "Error finding magazine", e);
            return Optional.empty();
        }
    }

    public List<Magazine> findByPublisher(String publisher) {
        logger.info("findByPublisher - Finding magazines by publisher: {}", publisher);
        try {
            return magazineRepository.findByPublisher(publisher);
        } catch (Exception e) {
            LoggerUtil.logError(MagazineService.class, "Error finding magazines by publisher", e);
            return List.of();
        }
    }

    public List<Magazine> findAll() {
        logger.info("findAll - Fetching all magazines");
        try {
            return magazineRepository.findAll();
        } catch (Exception e) {
            LoggerUtil.logError(MagazineService.class, "Error fetching all magazines", e);
            return List.of();
        }
    }

    public List<Magazine> getAll() {
        return findAll();
    }

    public Magazine create(Magazine magazine) {
        logger.info("create - Creating new magazine: {}", magazine.getTitle());
        try {
            return magazineRepository.save(magazine);
        } catch (Exception e) {
            LoggerUtil.logError(MagazineService.class, "Error creating magazine", e);
            throw new RuntimeException("Failed to create magazine", e);
        }
    }

    public Magazine update(Magazine magazine) {
        logger.info("update - Updating magazine: {}", magazine.getId());
        try {
            return magazineRepository.save(magazine);
        } catch (Exception e) {
            LoggerUtil.logError(MagazineService.class, "Error updating magazine", e);
            throw new RuntimeException("Failed to update magazine", e);
        }
    }

    public void delete(String id) {
        logger.info("delete - Deleting magazine: {}", id);
        try {
            magazineRepository.deleteById(id);
        } catch (Exception e) {
            LoggerUtil.logError(MagazineService.class, "Error deleting magazine", e);
            throw new RuntimeException("Failed to delete magazine", e);
        }
    }

    public List<Magazine> search(String field, String value) {
        logger.info("search - Searching magazines by {}: {}", field, value);
        SearchCriteria criteria = new SearchCriteria();
        criteria.setFilterField(field);
        criteria.setFilterValue(value);
        return magazineRepository.search(criteria);
    }
}
