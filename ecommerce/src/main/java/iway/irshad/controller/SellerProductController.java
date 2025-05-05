package iway.irshad.controller;

import iway.irshad.entity.Product;
import iway.irshad.entity.Seller;
import iway.irshad.exceptions.ProductException;
import iway.irshad.request.CreateProductRequest;
import iway.irshad.service.ProductService;
import iway.irshad.service.SellerService;
import iway.irshad.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sellers/products")
public class SellerProductController {
    private final ProductService productService;
    private final SellerService sellerService;
    private final UserService userService;

    @GetMapping()
    public ResponseEntity<List<Product>> getProductBySellerId(
            @RequestHeader("Authorization") String jwtToken
    ) {
        Seller seller = sellerService.getSellerProfile(jwtToken);

        List<Product> products = productService.getProductBySellerId(seller.getId());
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<Product> createProduct(
            @RequestBody CreateProductRequest createProductRequest,
            @RequestHeader("Authorization") String jwtToken

    ) {
        Seller seller = sellerService.getSellerProfile(jwtToken);
        Product product = productService.createProduct(createProductRequest, seller);
        System.out.println("Created Product: " + product.toString());
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long productId
    ) {
        try {
            productService.deleteProduct(productId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ProductException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long productId,
            @RequestBody Product product
    ) {
        try {
            Product updateProduct = productService.updateProduct(productId, product);
            return new ResponseEntity<>(updateProduct, HttpStatus.OK);
        } catch (ProductException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
