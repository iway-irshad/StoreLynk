package iway.irshad.repository;

import iway.irshad.entity.Cart;
import iway.irshad.entity.CartItem;
import iway.irshad.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    CartItem findByCartAndProductAndSize(Cart cart, Product product, String size);
}
