package se.fulkopinglibraryweb.service;

import se.fulkopinglibraryweb.exception.MediaServiceException;
import se.fulkopinglibraryweb.exception.MediaServiceException.ErrorType;
import se.fulkopinglibraryweb.model.MediaType;
import se.fulkopinglibraryweb.model.Media;
import se.fulkopinglibraryweb.model.Loan;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;
import se.fulkopinglibraryweb.repository.MediaRepository;
import se.fulkopinglibraryweb.service.interfaces.LoanService;
import se.fulkopinglibraryweb.service.interfaces.MediaService;
import se.fulkopinglibraryweb.utils.LoggerUtil;
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
        LoggerUtil.logInfo(MediaServiceImpl.class, String.format("Finding media by id: %s", id));
        try {
            return mediaRepository.findById(id)
                .orElseThrow(() -> {
                    Media media = new Media();
                    media.setId(id);
                    MediaServiceException ex = new MediaServiceException("Media not found", "getMediaById", media, ErrorType.NOT_FOUND);
                    return ex;
                });
        } catch (Exception e) {
            LoggerUtil.logError(MediaServiceImpl.class, "Failed to get media by id", e);
            Media media = new Media();
            media.setId(id);
            MediaServiceException ex = new MediaServiceException(
                "Failed to get media by id", 
                "getMediaById", 
                media,  // Explicitly pass Media object
                ErrorType.DATABASE, 
                e
            );
            throw ex;
        }
    }


    @Override
    public boolean isAvailable(String mediaId) {
        LoggerUtil.logInfo(MediaServiceImpl.class, String.format("Checking availability for media id: %s", mediaId));
        try {
            return mediaRepository.isAvailable(mediaId);
        } catch (Exception e) {
            LoggerUtil.logError(MediaServiceImpl.class, "Failed to check media availability", e);
            Media media = new Media();
            media.setId(mediaId);
            throw new MediaServiceException("Failed to check media availability", "isAvailable", media, ErrorType.DATABASE, e);
        }
    }

    @Override
    public Media saveMedia(Media media) {
        LoggerUtil.logInfo(MediaServiceImpl.class, String.format("Saving media: %s", media.getTitle()));
        try {
            return mediaRepository.save(media);
        } catch (Exception e) {
            LoggerUtil.logError(MediaServiceImpl.class, "Failed to save media", e);
            throw new MediaServiceException("Failed to save media", "saveMedia", media, ErrorType.DATABASE, e);
        }
    }

    @Override
    public void deleteMediaById(String id) {
        LoggerUtil.logInfo(MediaServiceImpl.class, String.format("Deleting media by id: %s", id));
        try {
            mediaRepository.deleteById(id);
        } catch (Exception e) {
            LoggerUtil.logError(MediaServiceImpl.class, "Failed to delete media", e);
            Media media = new Media();
            media.setId(id);
            throw new MediaServiceException("Failed to delete media", "deleteMediaById", media, ErrorType.DATABASE, e);
        }
    }

    @Override
    public List<Media> getAllMedia() {
        LoggerUtil.logInfo(MediaServiceImpl.class, "Getting all media");
        try {
            return mediaRepository.findAll();
        } catch (Exception e) {
            LoggerUtil.logError(MediaServiceImpl.class, "Failed to get all media", e);
            Media media = new Media();
            media.setTitle("All Media");
            throw new MediaServiceException("Failed to get all media", "getAllMedia", media, ErrorType.DATABASE, e);
        }
    }

    @Override
    public List<Media> getMediaByType(String type) {
        LoggerUtil.logInfo(MediaServiceImpl.class, String.format("Finding media by type: %s", type));
        try {
            return mediaRepository.findByType(type);
        } catch (Exception e) {
            LoggerUtil.logError(MediaServiceImpl.class, "Failed to get media by type", e);
            Media media = new Media();
            media.setMediaType(MediaType.valueOf(type));
            throw new MediaServiceException("Failed to get media by type", "getMediaByType", media, ErrorType.DATABASE, e);
        }
    }

    @Override
    public List<Media> searchMedia(String searchType, String searchQuery) {
        LoggerUtil.logInfo(MediaServiceImpl.class, 
            String.format("Searching media by type: %s with query: %s", searchType, searchQuery));
        
        try {
            // Determine which field to search based on searchType
            if ("title".equalsIgnoreCase(searchType)) {
                return mediaRepository.findByField("title", searchQuery);
            } else if ("director".equalsIgnoreCase(searchType)) {
                return mediaRepository.findByDirector(searchQuery);
            } else {
                Media media = new Media();
                media.setMediaType(MediaType.valueOf(searchType));
                throw new MediaServiceException("Invalid search type", "searchMedia", media, ErrorType.VALIDATION);
            }
        } catch (Exception e) {
            LoggerUtil.logError(MediaServiceImpl.class, "Failed to search media", e);
            if (e instanceof MediaServiceException) {
                throw e;
            }
            Media media = new Media();
            media.setTitle(searchQuery);
            throw new MediaServiceException("Failed to search media", "searchMedia", media, ErrorType.DATABASE, e);
        }
    }

    @Override
    public void addMedia(String title) {
        LoggerUtil.logInfo(MediaServiceImpl.class, String.format("Adding media with title: %s", title));
        
        try {
            Media media = new Media();
            media.setTitle(title);
            media.setAvailable(true);
            media.setMediaType(MediaType.valueOf("MEDIA"));
            
            mediaRepository.save(media);
        } catch (Exception e) {
            LoggerUtil.logError(MediaServiceImpl.class, "Failed to add media", e);
            Media media = new Media();
            media.setTitle(title);
            throw new MediaServiceException("Failed to add media", "addMedia", media, ErrorType.DATABASE, e);
        }
    }

    @Override
    public void updateMedia(String mediaId, String title) {
        LoggerUtil.logInfo(MediaServiceImpl.class, String.format("Updating media with id: %s", mediaId));
        
        try {
            Map<String, Object> updates = new HashMap<>();
            updates.put("title", title);
            
            mediaRepository.update(mediaId, updates);
        } catch (Exception e) {
            LoggerUtil.logError(MediaServiceImpl.class, "Failed to update media", e);
            Media media = new Media();
            media.setId(mediaId);
            throw new MediaServiceException("Failed to update media", "updateMedia", media, ErrorType.DATABASE, e);
        }
    }


    @Override
    public List<Loan> getLoansForMedia(String mediaId) {
        LoggerUtil.logInfo(MediaServiceImpl.class, String.format("Getting loans for media id: %s", mediaId));
        try {
            return loanService.getLoansByMediaId(mediaId);
        } catch (Exception e) {
            LoggerUtil.logError(MediaServiceImpl.class, "Failed to get loans for media", e);
            Media media = new Media();
            media.setId(mediaId);
            throw new MediaServiceException("Failed to get loans for media", "getLoansForMedia", media, ErrorType.DATABASE, e);
        }
    }

    @Override
    public Loan borrowMedia(String userId, String mediaId) {
        LoggerUtil.logInfo(MediaServiceImpl.class, 
            String.format("Borrowing media id: %s for user id: %s", mediaId, userId));
        
        try {
            // Update the media to mark it as unavailable
            Map<String, Object> updates = new HashMap<>();
            updates.put("available", false);
            
            mediaRepository.update(mediaId, updates);
            return loanService.createLoan(userId, mediaId);
        } catch (Exception e) {
            LoggerUtil.logError(MediaServiceImpl.class, "Failed to borrow media", e);
            Media media = new Media();
            media.setId(mediaId);
            throw new MediaServiceException("Failed to borrow media", "borrowMedia", media, ErrorType.DATABASE, e);
        }
    }

    @Override
    public void returnMedia(String userId, String mediaId) {
        LoggerUtil.logInfo(MediaServiceImpl.class, 
            String.format("Returning media id: %s for user id: %s", mediaId, userId));
        
        try {
            // Update the media to mark it as available
            Map<String, Object> updates = new HashMap<>();
            updates.put("available", true);
            
            mediaRepository.update(mediaId, updates);
        } catch (Exception e) {
            LoggerUtil.logError(MediaServiceImpl.class, "Failed to return media", e);
            Media media = new Media();
            media.setId(mediaId);
            throw new MediaServiceException("Failed to return media", "returnMedia", media, ErrorType.DATABASE, e);
        }
    }
}
