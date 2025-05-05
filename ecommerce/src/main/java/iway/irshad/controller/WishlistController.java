package iway.irshad.controller;


import iway.irshad.entity.Product;
import iway.irshad.entity.User;
import iway.irshad.entity.Wishlist;
import iway.irshad.service.ProductService;
import iway.irshad.service.UserService;
import iway.irshad.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;
    private final UserService userService;
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Wishlist> getWishlistByUserId(
            @RequestHeader("Authorization") String jwtToken
    ) throws Exception{

        User user = userService.findUserByJwtToken(jwtToken);
        Wishlist wishlist = wishlistService.getWishlistByUserId(user);
        return new ResponseEntity<>(wishlist, HttpStatus.OK);
    }

    @PostMapping("/add-product/{productId}")
    public ResponseEntity<Wishlist> addProductToWishlist(
            @PathVariable Long productId,
            @RequestHeader("Authorization") String jwtToken
    ) throws Exception{
        Product product = productService.findProductById(productId);
        User user = userService.findUserByJwtToken(jwtToken);
        Wishlist updatedWishlist = wishlistService.addProductToWishlist(
                user,
                product
        );
        return new ResponseEntity<>(updatedWishlist, HttpStatus.OK);
    }
}
