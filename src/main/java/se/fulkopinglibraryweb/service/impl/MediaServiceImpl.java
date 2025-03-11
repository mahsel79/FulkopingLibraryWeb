package se.fulkopinglibraryweb.service.impl;

import se.fulkopinglibraryweb.repository.MediaRepository;
import se.fulkopinglibraryweb.service.interfaces.MediaService;
import se.fulkopinglibraryweb.service.interfaces.LoanService;
import se.fulkopinglibraryweb.model.Media;
import se.fulkopinglibraryweb.model.Loan;
import se.fulkopinglibraryweb.utils.LoggingUtils;

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
        Media media = new Media();
        media.setTitle(title);
        mediaRepository.save(media);
    }

    @Override
    public void updateMedia(String mediaId, String title) {
        Optional<Media> mediaOpt = mediaRepository.findById(mediaId);
        if (!mediaOpt.isPresent()) {
            throw new IllegalArgumentException("Media not found");
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
        throw new IllegalArgumentException("Invalid search type");
    }

    @Override
    public Media getMediaById(String mediaId) {
        return mediaRepository.findById(mediaId)
            .orElseThrow(() -> new IllegalArgumentException("Media not found"));
    }

    @Override
    public boolean isAvailable(String mediaId) {
        return mediaRepository.isAvailable(mediaId);
    }

    @Override
    public Loan borrowMedia(String userId, String mediaId) {
        return loanService.borrowItem(userId, mediaId);
    }

    @Override
    public void returnMedia(String userId, String mediaId) {
        loanService.returnItem(userId, mediaId);
    }

    @Override
    public List<Media> getMediaByType(String type) {
        return mediaRepository.findByType(type);
    }

    @Override
    public Media saveMedia(Media media) {
        return mediaRepository.save(media);
    }

    @Override
    public List<Loan> getLoansForMedia(String mediaId) {
        return loanService.getLoansForItem(mediaId);
    }

    @Override
    public void deleteMediaById(String mediaId) {
        mediaRepository.deleteById(mediaId);
    }
}
