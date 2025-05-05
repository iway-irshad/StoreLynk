package iway.irshad.service.impl;

import iway.irshad.domain.OrderStatus;
import iway.irshad.domain.PaymentStatus;
import iway.irshad.entity.*;
import iway.irshad.repository.AddressRepository;
import iway.irshad.repository.OrderItemRepository;
import iway.irshad.repository.OrderRepository;
import iway.irshad.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public Set<Order> createOrder(User user, Address shippingAddress, Cart cart) {

        if (!user.getAddresses().contains(shippingAddress)) {
            user.getAddresses().add(shippingAddress);
        }
        Address address = addressRepository.save(shippingAddress);

        Map<Long, List<CartItem>> itemBySeller = cart.getCartItems().stream()
                .collect(Collectors.groupingBy(item -> item.getProduct()
                        .getSeller().getId()));
        Set<Order> orders = new HashSet<>();

        for (Map.Entry<Long, List<CartItem>> entry : itemBySeller.entrySet()) {
            Long sellerId = entry.getKey();
            List<CartItem> cartItems = entry.getValue();

            int totalOrderPrice = cartItems.stream().mapToInt(
                    CartItem::getSellingPrice
            ).sum();
            int totalItem = cartItems.stream().mapToInt(CartItem::getQuantity).sum();

            Order order = new Order();
            order.setUser(user);
            order.setSellerId(sellerId);
            order.setTotalMrpPrice(totalOrderPrice);
            order.setTotalSellingPrice(totalOrderPrice);
            order.setTotalItem(totalItem);
            order.setShippingAddress(address);
            order.setOrderStatus(OrderStatus.PENDING);
            order.getPaymentDetails().setPaymentStatus(PaymentStatus.PENDING);

            Order savedOrder = orderRepository.save(order);
            orders.add(savedOrder);

            List<OrderItem> orderItems = new ArrayList<>();

            for (CartItem cartItem : cartItems) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(savedOrder);
                orderItem.setMrpPrice(cartItem.getMrpPrice());
                orderItem.setProduct(cartItem.getProduct());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setSize(cartItem.getSize());
                orderItem.setUserId(user.getId());
                orderItem.setSellingPrice(cartItem.getSellingPrice());

                savedOrder.getOrderItem().add(orderItem);

                OrderItem savedOrderItem = orderItemRepository.save(orderItem);
                orderItems.add(savedOrderItem);
            }
        }

        return orders;
    }

    @Override
    public Order findOrderById(Long id) throws Exception {
        return orderRepository.findById(id).orElseThrow(
                () -> new Exception("Order not found")
        );
    }

    @Override
    public List<Order> userOrderHistory(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public List<Order> sellersOrder(Long sellerId) {
        return orderRepository.findBySellerId(sellerId);
    }

    @Override
    public Order updateOrderStatus(Long orderId, OrderStatus orderStatus) throws Exception {

        Order order = findOrderById(orderId);
        order.setOrderStatus(orderStatus);
        return orderRepository.save(order);
    }

    @Override
    public Order cancelOrder(Long orderId, User user) throws Exception {
        Order order = findOrderById(orderId);

        if (!user.getId().equals(order.getUser().getId())) {
            throw new Exception("You don't have access to this order");
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    @Override
    public OrderItem getOrderItemById(Long id) throws Exception {
        return orderItemRepository.findById(id).orElseThrow(() ->
                new Exception("Order item not exist")
        );
    }
}
