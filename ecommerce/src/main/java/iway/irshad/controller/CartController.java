package iway.irshad.controller;

import iway.irshad.entity.Cart;
import iway.irshad.entity.CartItem;
import iway.irshad.entity.Product;
import iway.irshad.entity.User;
import iway.irshad.request.AddItemRequest;
import iway.irshad.response.ApiResponse;
import iway.irshad.service.CartItemService;
import iway.irshad.service.CartService;
import iway.irshad.service.ProductService;
import iway.irshad.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final CartItemService cartItemService;
    private final UserService userService;
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Cart> findUserCartHandler(
            @RequestHeader("Authorization") String jwtToken
    ) throws Exception {

        User user = userService.findUserByJwtToken(jwtToken);
        Cart cart = cartService.findUserCart(user);
        return new ResponseEntity<>(cart, HttpStatus.ACCEPTED);
    }

    @PostMapping("/add")
    public ResponseEntity<CartItem> addCartItem(
            @RequestBody AddItemRequest addItemRequest,
            @RequestHeader("Authorization") String jwtToken
    ) throws Exception {
        User user = userService.findUserByJwtToken(jwtToken);
        Product product = productService.findProductById(addItemRequest.getProductId());

        CartItem item = cartService.addCartItem(
                user, product,
                addItemRequest.getSize(),
                addItemRequest.getQuantity()
        );

        ApiResponse addItemResponse = new ApiResponse();
        addItemResponse.setMessage("Successfully added item to the cart");

        return new ResponseEntity<>(item, HttpStatus.OK);
    }

    @DeleteMapping("/item/{cartItemId}")
    public ResponseEntity<ApiResponse> deleteCartItemHandler(
            @PathVariable Long cartItemId,
            @RequestHeader("Authorization") String jwtToken
    ) throws Exception {
        User user = userService.findUserByJwtToken(jwtToken);
        cartItemService.removeCartItem(user.getId(), cartItemId);

        ApiResponse deleteItemResponse = new ApiResponse();
        deleteItemResponse.setMessage("Successfully deleted item from the cart");

        return new ResponseEntity<>(deleteItemResponse, HttpStatus.ACCEPTED);
    }

    @PutMapping("/item/{cartItemId}")
    public ResponseEntity<CartItem> updateCartItemHandler(
            @PathVariable Long cartItemId,
            @RequestBody CartItem cartItem,
            @RequestHeader("Authorization") String jwtToken
    ) throws Exception {
        User user = userService.findUserByJwtToken(jwtToken);
        CartItem updatedCartItem = null;
        if (cartItem.getQuantity() > 0) {
            updatedCartItem = cartItemService.updateCartItem(
                    user.getId(),
                    cartItemId,
                    cartItem
            );
            ApiResponse updateCartItemResponse = new ApiResponse();
            updateCartItemResponse.setMessage("Successfully updated cart item");

        }

        return new ResponseEntity<>(updatedCartItem, HttpStatus.ACCEPTED);
    }

}
