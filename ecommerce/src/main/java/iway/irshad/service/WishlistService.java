package iway.irshad.service;

import iway.irshad.entity.Product;
import iway.irshad.entity.User;
import iway.irshad.entity.Wishlist;

public interface WishlistService {
    Wishlist createWishlist(User user);
    Wishlist getWishlistByUserId(User user);
    // Wishlist updateWishlist(Wishlist wishlist);
    Wishlist addProductToWishlist(User user, Product product);
}
