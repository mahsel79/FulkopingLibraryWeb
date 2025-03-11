package se.fulkopinglibraryweb.repository.impl;

import com.google.cloud.firestore.DocumentSnapshot;
import org.springframework.stereotype.Repository;
import se.fulkopinglibraryweb.model.Media;
import se.fulkopinglibraryweb.repository.AbstractFirestoreRepository;
import se.fulkopinglibraryweb.repository.MediaRepository;
import se.fulkopinglibraryweb.service.search.SearchCriteria;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class MediaFirestoreRepository extends AbstractFirestoreRepository<Media, String> implements MediaRepository {

    private static final String COLLECTION_NAME = "media";
    
    public MediaFirestoreRepository() {
        super(COLLECTION_NAME);
    }

    @Override
    public List<Media> search(SearchCriteria criteria) {
        return super.search(criteria);
    }

    @Override
    public List<String> getSearchableFields() {
        return Arrays.asList("title", "director", "catalog");
    }

    @Override
    public Media convertToEntity(DocumentSnapshot document) {
        if (document == null || !document.exists()) {
            return null;
        }
        
        Media media = new Media();
        media.setId(document.getId());
        media.setTitle(document.getString("title"));
        media.setCatalog(document.getString("catalog"));
        media.setDirector(document.getString("director"));
        
        Long releaseYear = document.getLong("ReleaseYear");
        if (releaseYear != null) {
            media.setReleaseYear(releaseYear.intValue());
        }
        
        Boolean available = document.getBoolean("available");
        media.setAvailable(available != null ? available : true);
        
        return media;
    }

    @Override
    public Map<String, Object> convertToMap(Media media) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", media.getTitle());
        map.put("catalog", media.getCatalog());
        map.put("director", media.getDirector());
        map.put("releaseYear", media.getReleaseYear());
        map.put("available", media.isAvailable());
        map.put("type", media.getType());
        return map;
    }

    @Override
    public List<Media> findByType(String type) {
        return super.findByFieldSync("type", type);
    }

    @Override
    public List<Media> findByDirector(String director) {
        return super.findByFieldSync("director", director);
    }

    @Override
    public List<Media> findByReleaseYear(int year) {
        return super.findByFieldSync("releaseYear", year);
    }

    @Override
    public boolean isAvailable(String mediaId) {
        Optional<Media> media = super.findByIdSync(mediaId);
        return media.map(Media::isAvailable).orElse(false);
    }

}
