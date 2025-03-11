package se.fulkopinglibraryweb.service.interfaces;

import se.fulkopinglibraryweb.model.Loan;
import se.fulkopinglibraryweb.model.User;
import se.fulkopinglibraryweb.model.LibraryItem;
import se.fulkopinglibraryweb.model.LoanStatus;
import java.util.List;

public interface LoanService {
    List<Loan> getLoansByMediaId(String mediaId);
    Loan createLoan(String userId, String mediaId);
    List<Loan> getLoansByUserId(String userId);
    List<Loan> getLoansByUserIdWithItems(String userId);
    List<Loan> batchProcessLoans(User user, List<LibraryItem> items);
    List<Loan> getUserLoansWithItems(String userId);
    List<Loan> getLoansByBookId(String bookId);
    Loan borrowItem(String userId, String bookId);
    Boolean returnItem(String userId, String itemId);
    Loan returnLoan(String loanId);
    void returnLoan(String userId, String bookId);
    List<Loan> getLoansByStatus(LoanStatus status);
    List<Loan> getBorrowedItems(String userId);
    List<Loan> getLoansForItem(String itemId);
}
