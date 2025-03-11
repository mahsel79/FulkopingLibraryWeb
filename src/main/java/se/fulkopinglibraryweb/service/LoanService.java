package se.fulkopinglibraryweb.service;

import se.fulkopinglibraryweb.model.Loan;
import java.util.List;

/**
 * Service interface for managing library loans.
 */
public interface LoanService {
    /**
     * Records a loan when a user borrows an item.
     *
     * @param userId The ID of the user borrowing the item
     * @param itemId The ID of the item being borrowed
     * @return true if the loan was successfully recorded, false otherwise
     */
    boolean borrowItem(String userId, String itemId);

    /**
     * Retrieves all items currently borrowed by a user.
     *
     * @param userId The ID of the user
     * @return A list of loans associated with the user
     */
    List<Loan> getBorrowedItems(String userId);

    /**
     * Records the return of a borrowed item.
     *
     * @param userId The ID of the user returning the item
     * @param itemId The ID of the item being returned
     * @return true if the return was successfully recorded, false otherwise
     */
    boolean returnItem(String userId, String itemId);
}