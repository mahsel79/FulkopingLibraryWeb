package se.fulkopinglibraryweb.service.interfaces;

import java.util.List;
import se.fulkopinglibraryweb.model.Media;
import se.fulkopinglibraryweb.model.Loan;

public interface MediaService {
    void addMedia(String title);
    void updateMedia(String mediaId, String title);
    List<Media> getAllMedia();
    List<Media> searchMedia(String searchType, String searchQuery);
    Media getMediaById(String mediaId);
    boolean isAvailable(String mediaId);
    Loan borrowMedia(String userId, String mediaId);
    void returnMedia(String userId, String mediaId);
    
    List<Media> getMediaByType(String type);
    Media saveMedia(Media media);
    List<Loan> getLoansForMedia(String mediaId);
    void deleteMediaById(String mediaId);
}
