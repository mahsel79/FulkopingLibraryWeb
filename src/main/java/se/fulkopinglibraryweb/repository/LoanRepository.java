package se.fulkopinglibraryweb.repository;

import se.fulkopinglibraryweb.model.Loan;
import se.fulkopinglibraryweb.model.LoanStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends FirestoreRepository<Loan, String> {
    List<Loan> findByMediaId(String mediaId);
    List<Loan> findByUserId(String userId);
    List<Loan> findByUserIdWithItems(String userId);
    List<Loan> findByBookId(String bookId);
    Optional<Loan> findById(String id);
    List<Loan> findByStatus(LoanStatus status);
    List<Loan> findByUserIdAndStatus(String userId, LoanStatus status);
    Optional<Loan> findByUserIdAndBookId(String userId, String bookId);
}
