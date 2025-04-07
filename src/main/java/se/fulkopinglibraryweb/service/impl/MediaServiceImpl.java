package se.fulkopinglibraryweb.service.impl;

import se.fulkopinglibraryweb.exception.MediaServiceException;
import se.fulkopinglibraryweb.repository.MediaRepository;
import se.fulkopinglibraryweb.service.interfaces.MediaService;
import se.fulkopinglibraryweb.service.interfaces.LoanService;
import se.fulkopinglibraryweb.model.Media;
import se.fulkopinglibraryweb.model.Loan;
import se.fulkopinglibraryweb.utils.LoggerUtil;

import java.util.List;
import java.util.Optional;

public class MediaServiceImpl implements MediaService {
    private final MediaRepository mediaRepository;
    private final LoanService loanService;

    public MediaServiceImpl(MediaRepository mediaRepository, LoanService loanService) {
        this.mediaRepository = mediaRepository;
        this.loanService = loanService;
    }

    @Override
    public void addMedia(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new MediaServiceException("Title cannot be empty", "addMedia", String.valueOf(title), MediaServiceException.ErrorType.VALIDATION);
        }
        Media media = new Media();
        media.setTitle(title);
        try {
            mediaRepository.save(media);
        } catch (Exception e) {
            throw new MediaServiceException("Failed to add media", "addMedia", String.valueOf(title), MediaServiceException.ErrorType.DATABASE, e);
        }
    }

    @Override
    public void updateMedia(String mediaId, String title) {
        Optional<Media> mediaOpt = mediaRepository.findById(mediaId);
        if (!mediaOpt.isPresent()) {
            throw new MediaServiceException("Media not found", "updateMedia", String.valueOf(mediaId), MediaServiceException.ErrorType.NOT_FOUND);
        }
        Media media = mediaOpt.get();
        media.setTitle(title);
        mediaRepository.save(media);
    }

    @Override
    public List<Media> getAllMedia() {
        return mediaRepository.findAll();
    }

    @Override
    public List<Media> searchMedia(String searchType, String searchQuery) {
        if (searchType.equalsIgnoreCase("type")) {
            return mediaRepository.findByType(searchQuery);
        }
            throw new MediaServiceException("Invalid search type", "searchMedia", String.valueOf(searchType), MediaServiceException.ErrorType.VALIDATION);
    }

    @Override
    public Media getMediaById(String mediaId) {
        return mediaRepository.findById(mediaId)
            .orElseThrow(() -> new MediaServiceException("Media not found", "getMediaById", String.valueOf(mediaId), MediaServiceException.ErrorType.NOT_FOUND));
    }

    @Override
    public boolean isAvailable(String mediaId) {
        if (mediaId == null || mediaId.trim().isEmpty()) {
            throw new MediaServiceException("Media ID cannot be empty", "isAvailable", String.valueOf(mediaId), MediaServiceException.ErrorType.VALIDATION);
        }
        try {
            return mediaRepository.isAvailable(mediaId);
        } catch (Exception e) {
            throw new MediaServiceException("Failed to check media availability", "isAvailable", String.valueOf(mediaId), MediaServiceException.ErrorType.DATABASE, e);
        }
    }

    @Override
    public Loan borrowMedia(String userId, String mediaId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new MediaServiceException("User ID cannot be empty", "borrowMedia", String.valueOf(userId), MediaServiceException.ErrorType.VALIDATION);
        }
        if (mediaId == null || mediaId.trim().isEmpty()) {
            throw new MediaServiceException("Media ID cannot be empty", "borrowMedia", String.valueOf(mediaId), MediaServiceException.ErrorType.VALIDATION);
        }
        try {
            return loanService.borrowItem(userId, mediaId);
        } catch (Exception e) {
            throw new MediaServiceException("Failed to borrow media", "borrowMedia", String.valueOf(mediaId), MediaServiceException.ErrorType.DATABASE, e);
        }
    }

    @Override
    public void returnMedia(String userId, String mediaId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new MediaServiceException("User ID cannot be empty", "returnMedia", String.valueOf(userId), MediaServiceException.ErrorType.VALIDATION);
        }
        if (mediaId == null || mediaId.trim().isEmpty()) {
            throw new MediaServiceException("Media ID cannot be empty", "returnMedia", String.valueOf(mediaId), MediaServiceException.ErrorType.VALIDATION);
        }
        try {
            loanService.returnItem(userId, mediaId);
        } catch (Exception e) {
            throw new MediaServiceException("Failed to return media", "returnMedia", String.valueOf(mediaId), MediaServiceException.ErrorType.DATABASE, e);
        }
    }

    @Override
    public List<Media> getMediaByType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new MediaServiceException("Type cannot be empty", "getMediaByType", String.valueOf(type), MediaServiceException.ErrorType.VALIDATION);
        }
        try {
            return mediaRepository.findByType(type);
        } catch (Exception e) {
            throw new MediaServiceException("Failed to get media by type", "getMediaByType", String.valueOf(type), MediaServiceException.ErrorType.DATABASE, e);
        }
    }

    @Override
    public Media saveMedia(Media media) {
        if (media == null) {
            throw new MediaServiceException("Media cannot be null", "saveMedia", "null", MediaServiceException.ErrorType.VALIDATION);
        }
        if (media.getTitle() == null || media.getTitle().trim().isEmpty()) {
            throw new MediaServiceException("Media title cannot be empty", "saveMedia", media.toString(), MediaServiceException.ErrorType.VALIDATION);
        }
        try {
            return mediaRepository.save(media);
        } catch (Exception e) {
            throw new MediaServiceException("Failed to save media", "saveMedia", media.toString(), MediaServiceException.ErrorType.DATABASE, e);
        }
    }

    @Override
    public List<Loan> getLoansForMedia(String mediaId) {
        if (mediaId == null || mediaId.trim().isEmpty()) {
            throw new MediaServiceException("Media ID cannot be empty", "getLoansForMedia", String.valueOf(mediaId), MediaServiceException.ErrorType.VALIDATION);
        }
        try {
            return loanService.getLoansForItem(mediaId);
        } catch (Exception e) {
            throw new MediaServiceException("Failed to get loans for media", "getLoansForMedia", String.valueOf(mediaId), MediaServiceException.ErrorType.DATABASE, e);
        }
    }

    @Override
    public void deleteMediaById(String mediaId) {
        if (mediaId == null || mediaId.trim().isEmpty()) {
            throw new MediaServiceException("Media ID cannot be empty", "deleteMediaById", String.valueOf(mediaId), MediaServiceException.ErrorType.VALIDATION);
        }
        try {
            mediaRepository.deleteById(mediaId);
        } catch (Exception e) {
            throw new MediaServiceException("Failed to delete media", "deleteMediaById", String.valueOf(mediaId), MediaServiceException.ErrorType.DATABASE, e);
        }
    }
}
