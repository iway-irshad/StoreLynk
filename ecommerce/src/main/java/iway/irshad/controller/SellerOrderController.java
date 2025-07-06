package iway.irshad.controller;

import iway.irshad.domain.OrderStatus;
import iway.irshad.entity.Order;
import iway.irshad.entity.Seller;
import iway.irshad.exceptions.SellerException;
import iway.irshad.service.OrderService;
import iway.irshad.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller/orders")
public class SellerOrderController {

    private final SellerService sellerService;
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrdersHandler(
            @RequestHeader("Authorization") String jwtToken
    ) throws SellerException {
        Seller seller = sellerService.getSellerProfile(jwtToken);
        List<Order> orders = orderService.sellersOrder(seller.getId());
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @PatchMapping("/{orderId}/status/{orderStatus}")
    public ResponseEntity<Order> updateOrderHandler(
            @PathVariable Long orderId,
            @PathVariable OrderStatus orderStatus,
            @RequestHeader("Authorization") String jwtToken
            ) throws Exception {
        Order order = orderService.updateOrderStatus(orderId, orderStatus);
        return new ResponseEntity<>(order, HttpStatus.ACCEPTED);
    }
}
