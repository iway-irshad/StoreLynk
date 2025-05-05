package iway.irshad.service;

import iway.irshad.entity.Cart;
import iway.irshad.entity.CartItem;
import iway.irshad.entity.Product;
import iway.irshad.entity.User;

public interface CartService {

    CartItem addCartItem(
            User user,
            Product product,
            String size,
            int quantity
    );

    Cart findUserCart(User user);
}
