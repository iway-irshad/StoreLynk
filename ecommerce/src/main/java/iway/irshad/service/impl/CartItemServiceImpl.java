package iway.irshad.service.impl;

import iway.irshad.entity.CartItem;
import iway.irshad.entity.User;
import iway.irshad.repository.CartItemRepository;
import iway.irshad.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;

    @Override
    public CartItem updateCartItem(Long userId, Long id, CartItem cartItem) throws Exception {
        CartItem item = findCartItemById(id);

        User cartItemUser = item.getCart().getUser();

        if (cartItemUser.getId().equals(userId)) {
            item.setQuantity(cartItem.getQuantity());
            item.setMrpPrice(item.getQuantity() * item.getProduct().getMrpPrice());
            item.setSellingPrice(item.getQuantity() * item.getProduct().getSellingPrice());
            return cartItemRepository.save(item);
        }

        throw new Exception("You can't update this cart item");
    }

    @Override
    public void removeCartItem(Long userId, Long cartItemId) throws Exception {

        CartItem cartItem = findCartItemById(cartItemId);
        User cartItemUser = cartItem.getCart().getUser();
        if (cartItemUser.getId().equals(userId)) {
            cartItemRepository.delete(cartItem);
        } else throw new Exception("You can't remove this cart item");
    }

    @Override
    public CartItem findCartItemById(Long id) throws Exception {
        return cartItemRepository.findById(id).orElseThrow( () ->
                new Exception("cart item not found with this id " + id
                )
        );
    }
}
