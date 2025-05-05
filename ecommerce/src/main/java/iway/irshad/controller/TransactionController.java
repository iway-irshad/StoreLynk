package iway.irshad.controller;

import iway.irshad.entity.Seller;
import iway.irshad.entity.Transaction;
import iway.irshad.service.SellerService;
import iway.irshad.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transaction")
public class TransactionController {

    private final TransactionService transactionService;
    private final SellerService sellerService;

    @GetMapping("/seller")
    public ResponseEntity<List<Transaction>> getTransactionBySeller(
            @RequestHeader("Authorization") String jwtToken
    ) {

        Seller seller = sellerService.getSellerProfile(jwtToken);

        List<Transaction> transactions = transactionService.getTransactionBySellerId(seller);
        return ResponseEntity.ok(transactions);

    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransaction() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }
}
