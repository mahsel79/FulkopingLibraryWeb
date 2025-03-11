package se.fulkopinglibraryweb.service.impl;

import se.fulkopinglibraryweb.model.Loan;
import se.fulkopinglibraryweb.model.LoanStatus;
import se.fulkopinglibraryweb.model.User;
import se.fulkopinglibraryweb.model.LibraryItem;
import se.fulkopinglibraryweb.model.Book;
import se.fulkopinglibraryweb.model.Magazine;
import se.fulkopinglibraryweb.model.Media;



import se.fulkopinglibraryweb.utils.LoggingUtils;
import se.fulkopinglibraryweb.service.interfaces.LoanService;
import se.fulkopinglibraryweb.repository.BookRepository;
import se.fulkopinglibraryweb.repository.MagazineRepository;
import se.fulkopinglibraryweb.repository.MediaRepository;

import com.google.cloud.firestore.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;

/**
 * Service class that manages library item loans
 */
public class LoanServiceImpl implements LoanService {
    @Override
    public List<Loan> getLoansByUserId(String userId) {
        try {
            return loanCollection.whereEqualTo("userId", userId).get().get()
                    .toObjects(Loan.class);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error getting loans for user {}", userId, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Loan> getLoansByBookId(String bookId) {
        try {
            return loanCollection.whereEqualTo("bookId", bookId).get().get()
                    .toObjects(Loan.class);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error getting loans for book {}", bookId, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Loan> getLoansByMediaId(String mediaId) {
        try {
            return loanCollection.whereEqualTo("mediaId", mediaId).get().get()
                    .toObjects(Loan.class);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error getting loans for media {}", mediaId, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Loan> getLoansForItem(String itemId) {
        try {
            return loanCollection.whereEqualTo("itemId", itemId).get().get()
                    .toObjects(Loan.class);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error getting loans for item {}", itemId, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Loan createLoan(String userId, String itemId) {
        try {
            DocumentReference docRef = loanCollection.document();
            Date loanDate = new Date();
            String itemType = itemId.split("_")[0];
            Date dueDate = calculateDueDate(loanDate, itemType);
            Loan loan = new Loan(userId, itemId, loanDate, dueDate, LoanStatus.ACTIVE);
            docRef.set(loan).get();
            return loan;
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error creating loan for user {} and item {}", userId, itemId, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Loan> getUserLoansWithItems(String userId) {
        try {
            List<Loan> loans = loanCollection.whereEqualTo("userId", userId).get().get()
                    .toObjects(Loan.class);
            return loans.stream()
                    .map(loan -> {
                        try {
                            return enrichLoanWithItem(loan);
                        } catch (InterruptedException | ExecutionException e) {
                            logger.error("Error enriching loan with item for user {}", userId, e);
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error getting loans with items for user {}", userId, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Loan> getLoansByUserIdWithItems(String userId) {
        try {
            List<Loan> loans = loanCollection.whereEqualTo("userId", userId).get().get()
                    .toObjects(Loan.class);
            return loans.stream()
                    .map(loan -> {
                        try {
                            return enrichLoanWithItem(loan);
                        } catch (InterruptedException | ExecutionException e) {
                            logger.error("Error enriching loan with item for user {}", userId, e);
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error getting loans with items for user {}", userId, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Loan returnLoan(String loanId) {
        try {
            DocumentReference docRef = loanCollection.document(loanId);
            docRef.update("status", LoanStatus.RETURNED).get();
            return docRef.get().get().toObject(Loan.class);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error returning loan {}", loanId, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void returnLoan(String userId, String itemId) {
        try {
            Query query = loanCollection
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("itemId", itemId)
                    .whereEqualTo("status", LoanStatus.ACTIVE);
            
            QuerySnapshot snapshot = query.get().get();
            if (snapshot.isEmpty()) {
                throw new RuntimeException("No active loan found for user and item");
            }
            
            DocumentReference docRef = snapshot.getDocuments().get(0).getReference();
            docRef.update("status", LoanStatus.RETURNED);
            docRef.update("status", LoanStatus.RETURNED).get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error returning loan for user {} and item {}", userId, itemId, e);
            throw new RuntimeException(e);
        }
    }

    private Date calculateDueDate(Date loanDate, String itemType) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(loanDate);
        
        if ("MEDIA".equalsIgnoreCase(itemType)) {
            calendar.add(Calendar.DAY_OF_MONTH, 10);
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, 30);
        }
        return calendar.getTime();
    }

    @Override
    public Loan borrowItem(String userId, String itemId) {
        try {
            DocumentReference docRef = loanCollection.document();
            Date loanDate = new Date();
            String itemType = itemId.split("_")[0];
            Date dueDate = calculateDueDate(loanDate, itemType);
            Loan loan = new Loan(userId, itemId, loanDate, dueDate, LoanStatus.ACTIVE);
            docRef.set(loan).get();
            return loan;
        } catch (Exception e) {
            logger.error("Error borrowing item {} for user {}", itemId, userId, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Loan> batchProcessLoans(User user, List<LibraryItem> items) {
        WriteBatch batch = firestore.batch();
        List<Loan> loans = new ArrayList<>();
        items.forEach(item -> {
            DocumentReference docRef = loanCollection.document();
            Date loanDate = new Date();
            String itemType = item.getId().split("_")[0];
            Date dueDate = calculateDueDate(loanDate, itemType);
            Loan loan = new Loan(user.getId(), item.getId(), loanDate, dueDate, LoanStatus.ACTIVE);
            batch.set(docRef, loan);
            loans.add(loan);
        });
        try {
            batch.commit().get();
            return loans;
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error processing batch loans", e);
            throw new RuntimeException(e);
        }
    }

    private final CollectionReference loanCollection;
    private final Firestore firestore;
    private final BookRepository bookRepository;
    private final MagazineRepository magazineRepository;
    private final MediaRepository mediaRepository;
    
    public LoanServiceImpl(Firestore firestore, 
                          BookRepository bookRepository,
                          MagazineRepository magazineRepository,
                          MediaRepository mediaRepository) {
        this.firestore = firestore;
        this.bookRepository = bookRepository;
        this.magazineRepository = magazineRepository;
        this.mediaRepository = mediaRepository;
        this.loanCollection = firestore.collection("loans");
    }

    @Override
    public List<Loan> getBorrowedItems(String userId) {
        try {
            return loanCollection
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("status", LoanStatus.ACTIVE)
                    .get()
                    .get()
                    .toObjects(Loan.class);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error getting borrowed items for user {}", userId, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Boolean returnItem(String userId, String itemId) {
        try {
            Query query = loanCollection
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("itemId", itemId)
                    .whereEqualTo("status", LoanStatus.ACTIVE);
            
            QuerySnapshot snapshot = query.get().get();
            if (snapshot.isEmpty()) {
                return false;
            }
            
            DocumentReference docRef = snapshot.getDocuments().get(0).getReference();
            docRef.update("status", LoanStatus.RETURNED).get();
            return true;
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error returning item {} for user {}", itemId, userId, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Loan> getLoansByStatus(LoanStatus status) {
        try {
            return loanCollection
                    .whereEqualTo("status", status)
                    .get()
                    .get()
                    .toObjects(Loan.class);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error getting loans by status {}", status, e);
            throw new RuntimeException(e);
        }
    }

    private Loan enrichLoanWithItem(Loan loan) throws InterruptedException, ExecutionException {
        String itemId = loan.getItemId();
        if (itemId != null) {
            String itemType = itemId.split("_")[0];
            
            if ("BOOK".equalsIgnoreCase(itemType)) {
                Optional<Book> optionalBook = bookRepository.findById(itemId);
                optionalBook.ifPresent(book -> {
                    loan.setBook(itemId);
                    loan.setBookDetails(book);
                });
            } else if ("MAGAZINE".equalsIgnoreCase(itemType)) {
                Optional<Magazine> optionalMagazine = magazineRepository.findById(itemId);
                optionalMagazine.ifPresent(magazine -> {
                    loan.setMagazine(itemId);
                    loan.setMagazineDetails(magazine);
                });
            } else if ("MEDIA".equalsIgnoreCase(itemType)) {
                Optional<Media> optionalMedia = mediaRepository.findById(itemId);
                optionalMedia.ifPresent(media -> {
                    loan.setMedia(itemId);
                    loan.setMediaDetails(media);
                });
            }
        }
        return loan;
    }

    private static final Logger logger = LoggingUtils.getLogger(LoanServiceImpl.class);
}
