package se.fulkopinglibraryweb.service;

import se.fulkopinglibraryweb.model.ItemType;
import se.fulkopinglibraryweb.model.Media;
import se.fulkopinglibraryweb.model.Loan;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;
import se.fulkopinglibraryweb.repository.MediaRepository;
import se.fulkopinglibraryweb.service.interfaces.LoanService;
import se.fulkopinglibraryweb.service.interfaces.MediaService;
import se.fulkopinglibraryweb.utils.LoggingUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class MediaServiceImpl implements MediaService {
    private final MediaRepository mediaRepository;
    private final LoanService loanService;

    public MediaServiceImpl(MediaRepository mediaRepository, LoanService loanService) {
        this.mediaRepository = mediaRepository;
        this.loanService = loanService;
    }

    @Override
    public Media getMediaById(String id) {
        LoggingUtils.logServiceOperation("MediaService", "getMediaById", "Finding media by id: " + id);
        Optional<Media> mediaOpt = mediaRepository.findById(id);
        return mediaOpt.orElseThrow(() -> new RuntimeException("Media not found with id: " + id));
    }

    @Override
    public List<Loan> getLoansForMedia(String mediaId) {
        LoggingUtils.logServiceOperation("MediaService", "getLoansForMedia", 
            "Getting loans for media id: " + mediaId);
        return loanService.getLoansByMediaId(mediaId);
    }

    @Override
    public List<Media> getMediaByType(String type) {
        LoggingUtils.logServiceOperation("MediaService", "getMediaByType", "Finding media by type: " + type);
        return mediaRepository.findByType(type);
    }

    @Override
    public Media saveMedia(Media media) {
        LoggingUtils.logServiceOperation("MediaService", "saveMedia", "Saving media: " + media.getTitle());
        return mediaRepository.save(media);
    }

    @Override
    public void deleteMediaById(String id) {
        LoggingUtils.logServiceOperation("MediaService", "deleteMediaById", "Deleting media by id: " + id);
        mediaRepository.deleteById(id);
    }

    @Override
    public List<Media> getAllMedia() {
        LoggingUtils.logServiceOperation("MediaService", "getAllMedia", "Getting all media");
        return mediaRepository.findAll();
    }

    @Override
    public List<Media> searchMedia(String searchType, String searchQuery) {
        LoggingUtils.logServiceOperation("MediaService", "searchMedia", 
            "Searching media by type: " + searchType + " with query: " + searchQuery);
        
        // Determine which field to search based on searchType
        if ("title".equalsIgnoreCase(searchType)) {
            return mediaRepository.findByField("title", searchQuery);
        }  else if ("director".equalsIgnoreCase(searchType)) {
            return mediaRepository.findByDirector(searchQuery);
       
        } else {
            // Default to searching by title if searchType is not recognized
            return mediaRepository.findByField("title", searchQuery);
        }
    }

    @Override
    public void addMedia(String title, String format, String barcode) {
        LoggingUtils.logServiceOperation("MediaService", "addMedia", 
            "Adding media with title: " + title + ", format: " + format);
        
        Media media = new Media();
        media.setTitle(title);
        media.setFormat(format);
        media.setAvailable(true);
        media.setMediaType(ItemType.MEDIA);
        
        mediaRepository.save(media);
    }

    @Override
    public void updateMedia(String mediaId, String title, String format) {
        LoggingUtils.logServiceOperation("MediaService", "updateMedia", 
            "Updating media with id: " + mediaId);
        
        Map<String, Object> updates = new HashMap<>();
        updates.put("title", title);
        updates.put("format", format);
        
        mediaRepository.update(mediaId, updates);
    }


    @Override
    public boolean isAvailable(String mediaId) {
        LoggingUtils.logServiceOperation("MediaService", "isAvailable", 
            "Checking availability for media id: " + mediaId);
        return mediaRepository.isAvailable(mediaId);
    }

    @Override
    public Loan borrowMedia(String userId, String mediaId) {
        LoggingUtils.logServiceOperation("MediaService", "borrowMedia", 
            "Borrowing media id: " + mediaId + " for user id: " + userId);
        
        // Update the media to mark it as unavailable
        Map<String, Object> updates = new HashMap<>();
        updates.put("available", false);
        
        mediaRepository.update(mediaId, updates);
        return loanService.createLoan(userId, mediaId);
    }

    @Override
    public void returnMedia(String userId, String mediaId) {
        LoggingUtils.logServiceOperation("MediaService", "returnMedia", 
            "Returning media id: " + mediaId + " for user id: " + userId);
        
        // Update the media to mark it as available
        Map<String, Object> updates = new HashMap<>();
        updates.put("available", true);
        
        mediaRepository.update(mediaId, updates);
    }
}
