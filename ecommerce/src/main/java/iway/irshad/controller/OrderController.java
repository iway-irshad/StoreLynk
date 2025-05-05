package iway.irshad.controller;

import com.razorpay.PaymentLink;
import com.stripe.service.PaymentLinkService;
import iway.irshad.domain.PaymentMethod;
import iway.irshad.entity.*;
import iway.irshad.repository.PaymentOrderRepository;
import iway.irshad.response.PaymentLinkResponse;
import iway.irshad.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final CartService cartService;
    private final SellerService sellerService;
    private final SellerReportService sellerReportService;
    private final PaymentService paymentService;
    private final PaymentOrderRepository paymentOrderRepository;


    @PostMapping
    public ResponseEntity<PaymentLinkResponse> createOrderHandler(
            @RequestBody Address shippingAddress,
            @RequestParam PaymentMethod paymentMethod,
            @RequestHeader("Authorization") String jwtToken
            ) throws Exception {
        User user = userService.findUserByJwtToken(jwtToken);
        Cart cart = cartService.findUserCart(user);
        Set<Order> orders = orderService.createOrder(user, shippingAddress, cart);

        PaymentOrder paymentOrder = paymentService.createOrder(user, orders);

        PaymentLinkResponse paymentLinkResponse = new PaymentLinkResponse();

        if (paymentMethod.equals(PaymentMethod.RAZORPAY)) {
            PaymentLink paymentLink = paymentService.createRazorpayPaymentLink(
                    user,
                    paymentOrder.getAmount(),
                    paymentOrder.getId()
            );

            String paymentLinkUrl = paymentLink.get("short_url");
            String paymentUrlId = paymentLink.get("id");

            paymentLinkResponse.setPayment_link_url(paymentLinkUrl);

            paymentOrder.setPaymentLinkId(paymentUrlId);
            paymentOrderRepository.save(paymentOrder);

        } else {
            String paymentUrl = paymentService.createStripePaymentLink(
                    user,
                    paymentOrder.getAmount(),
                    paymentOrder.getId()
            );
            paymentLinkResponse.setPayment_link_url(paymentUrl);
        }

        return new ResponseEntity<>(paymentLinkResponse, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<List<Order>> userOrderHistoryHandler(
            @RequestHeader("Authorization") String jwtToken
    ) throws Exception {
        User user = userService.findUserByJwtToken(jwtToken);
        List<Order> orders = orderService.userOrderHistory(user.getId());
        return new ResponseEntity<>(orders, HttpStatus.ACCEPTED);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String jwtToken
    ) throws Exception {
        User user = userService.findUserByJwtToken(jwtToken);
        Order orders = orderService.findOrderById(orderId);
        return new ResponseEntity<>(orders, HttpStatus.ACCEPTED);

    }

    @GetMapping("/item/{orderItemId}")
    public ResponseEntity<OrderItem> getOrderItemById(
            @PathVariable Long orderItemId,
            @RequestHeader("Authorization") String jwtToken
    ) throws Exception {
        User user = userService.findUserByJwtToken(jwtToken);
        OrderItem orderItems = orderService.getOrderItemById(orderItemId);
        return new ResponseEntity<>(orderItems, HttpStatus.ACCEPTED);
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Order> cancelOrderHandler(
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String jwtToken
    ) throws Exception {
        User user = userService.findUserByJwtToken(jwtToken);
        Order orders = orderService.findOrderById(orderId);

        Seller seller = sellerService.getSellerById(orders.getSellerId());
        SellerReport sellerReport = sellerReportService.getSellerReport(seller);

        sellerReport.setCanceledOrders(sellerReport.getCanceledOrders() + 1);
        sellerReport.setTotalRefunds(sellerReport.getTotalRefunds() + orders.getTotalSellingPrice());
        sellerReportService.updateSellerReport(sellerReport);

        return ResponseEntity.ok(orders);

    }



}
