package iway.irshad.service.impl;

import iway.irshad.entity.Cart;
import iway.irshad.entity.CartItem;
import iway.irshad.entity.Product;
import iway.irshad.entity.User;
import iway.irshad.repository.CartItemRepository;
import iway.irshad.repository.CartRepository;
import iway.irshad.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;


    @Override
    public CartItem addCartItem(User user, Product product, String size, int quantity) {
        Cart cart = findUserCart(user);

        CartItem isPresent = cartItemRepository.findByCartAndProductAndSize(cart, product, size);
        if (isPresent == null) {
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setSize(size);
            cartItem.setUserId(user.getId());

            int totalPrice = quantity * product.getSellingPrice();
            cartItem.setSellingPrice(totalPrice);
            cartItem.setMrpPrice(quantity * product.getSellingPrice());

            cart.getCartItems().add(cartItem);
            cartItem.setCart(cart);

            return cartItemRepository.save(cartItem);
        }
        return isPresent;
    }
    @Override
    public Cart findUserCart(User user) {
        Cart cart = cartRepository.findByUserId(user.getId());

        int totalPrice = 0;
        int totalDiscount = 0;
        int totalItem = 0;

        for (CartItem cartItem : cart.getCartItems()) {
            totalItem += cartItem.getMrpPrice();
            totalDiscount += cartItem.getSellingPrice();
            totalItem += cartItem.getQuantity();
        }

        cart.setTotalMrpPrice(totalPrice);
        cart.setTotalItems(totalItem);
        cart.setTotalSellingPrice(totalDiscount);
        cart.setDiscount(calculateDiscountPercentage(totalPrice, totalDiscount));
        return cart;
    }

    private int calculateDiscountPercentage(double mrpPrice, double salePrice) {
//        if (mrpPrice < salePrice) {
//            throw new IllegalArgumentException("MrpPrice must be greater than salePrice");
//        } else if (mrpPrice <= 0) {
//            throw new IllegalArgumentException("MrpPrice must be greater than 0");
//        }
        double discount = mrpPrice - salePrice;
        double discountPercentage = (discount / mrpPrice) * 100;

        return (int) discountPercentage;
    }
}
