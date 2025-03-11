package se.fulkopinglibraryweb.repository;

import se.fulkopinglibraryweb.model.Media;

import java.util.List;

/**
 * Repository interface for Media entities.
 * Extends FirestoreRepository to inherit Firestore-specific operations.
 */
public interface MediaRepository extends FirestoreRepository<Media, String> {
    
    /**
     * Find media by type.
     *
     * @param type The media type to search for
     * @return A list of matching media
     */
    List<Media> findByType(String type);
    
    /**
     * Find media by director.
     *
     * @param director The director to search for
     * @return A list of matching media
     */
    List<Media> findByDirector(String director);
    
    /**
     * Find media by release year.
     *
     * @param year The release year to search for
     * @return A list of matching media
     */
    List<Media> findByReleaseYear(int year);
    
    /**
     * Check if a media item is available.
     *
     * @param mediaId The ID of the media to check
     * @return A boolean indicating availability
     */
    boolean isAvailable(String mediaId);
}
