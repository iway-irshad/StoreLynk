package iway.irshad.service;

import iway.irshad.entity.Product;
import iway.irshad.entity.Seller;
import iway.irshad.exceptions.ProductException;
import iway.irshad.request.CreateProductRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

    Product createProduct(CreateProductRequest createProductRequest, Seller seller);
    void deleteProduct(Long productId) throws ProductException;
    Product updateProduct(Long productId, Product product) throws ProductException;
    Product findProductById(Long productId) throws ProductException;
    List<Product> searchProducts(String query);
    Page<Product> getAllProducts(
            String category,
            String brand,
            String colors,
            String sizes,
            Integer minPrice,
            Integer maxPrice,
            Integer minDiscount,
            String sort,
            String stock,
            Integer pageNumber
    );

    List<Product> getProductBySellerId(Long sellerId);
}
