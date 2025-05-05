package iway.irshad.controller;

import iway.irshad.entity.*;
import iway.irshad.response.ApiResponse;
import iway.irshad.response.PaymentLinkResponse;
import iway.irshad.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final UserService userService;
    private final SellerService sellerService;
    private final SellerReportService sellerReportService;
    private final TransactionService transactionService;

    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse> paymentSuccessHandler(
            @PathVariable String paymentId,
            @RequestParam String paymentLinkId,
            @RequestHeader("Authorization") String jwtToken
    ) throws Exception {
        User user = userService.findUserByJwtToken(jwtToken);
        PaymentLinkResponse paymentLinkResponse;

        PaymentOrder paymentOrder = paymentService.getPaymentOrderByPaymentId(paymentLinkId);

        boolean paymentSuccess = paymentService.proceedPaymentOrder(
                paymentOrder,
                paymentId,
                paymentLinkId
        );
        if (paymentSuccess) {
            for (Order order : paymentOrder.getOrders()) {
                transactionService.createTransaction(order);
                Seller seller = sellerService.getSellerById(order.getSellerId());
                SellerReport sellerReport = sellerReportService.getSellerReport(seller);
                sellerReport.setTotalOrders(sellerReport.getTotalOrders() + 1);
                sellerReport.setTotalEarnings(sellerReport.getTotalEarnings() + order.getTotalSellingPrice());
                sellerReport.setTotalSales(sellerReport.getTotalSales() + order.getOrderItem().size());

                sellerReportService.updateSellerReport(sellerReport);
            }
        }

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Payment successful");

        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }
}
