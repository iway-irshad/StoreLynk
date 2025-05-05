package iway.irshad.service;

import iway.irshad.entity.Order;
import iway.irshad.entity.Seller;
import iway.irshad.entity.Transaction;

import java.util.List;

public interface TransactionService {

    Transaction createTransaction(Order order);
    List<Transaction> getTransactionBySellerId(Seller seller);
    List<Transaction> getAllTransactions();
}
