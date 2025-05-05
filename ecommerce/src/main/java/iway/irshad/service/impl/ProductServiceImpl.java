package iway.irshad.service.impl;

import iway.irshad.entity.Category;
import iway.irshad.entity.Product;
import iway.irshad.entity.Seller;
import iway.irshad.exceptions.ProductException;
import iway.irshad.repository.CategoryRepository;
import iway.irshad.repository.ProductRepository;
import iway.irshad.request.CreateProductRequest;
import iway.irshad.service.ProductService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Product createProduct(CreateProductRequest createProductRequest, Seller seller) {

        Category category = categoryRepository.findByCategoryId(createProductRequest.getCategory());
        if (category == null) {
            Category newCategory = new Category();
            newCategory.setCategoryId(createProductRequest.getCategory());
            newCategory.setLevel(1);
            category = categoryRepository.save(newCategory);
        }

        Category category2 = categoryRepository.findByCategoryId(createProductRequest.getCategory2());
        if (category2 == null) {
            Category newCategory = new Category();
            newCategory.setCategoryId(createProductRequest.getCategory2());
            newCategory.setLevel(2);
            newCategory.setParentCategory(category);
            category2 = categoryRepository.save(newCategory);
        }

        Category category3 = categoryRepository.findByCategoryId(createProductRequest.getCategory3());
        if (category3 == null) {
            Category newCategory = new Category();
            newCategory.setCategoryId(createProductRequest.getCategory3());
            newCategory.setLevel(3);
            newCategory.setParentCategory(category2);
            category3 = categoryRepository.save(newCategory);
        }

        int discountPercentage = calculateDiscountPercentage(createProductRequest.getMrpPrice(), createProductRequest.getSellingPrice());

        Product product = new Product();
        product.setSeller(seller);
        product.setCategory(category3);
        product.setDescription(createProductRequest.getDescription());
        product.setCreatedAt(LocalDateTime.now());
        product.setTitle(createProductRequest.getTitle());
        product.setColor(createProductRequest.getColor());
        product.setQuantity(createProductRequest.getQuantity());
        product.setSellingPrice(createProductRequest.getSellingPrice());
        product.setImages(createProductRequest.getImages());
        product.setMrpPrice(createProductRequest.getMrpPrice());
        product.setSizes(createProductRequest.getSizes());
        product.setDiscountPercent(discountPercentage);

        return productRepository.save(product);
    }

    private int calculateDiscountPercentage(double mrpPrice, double salePrice) {
        if (mrpPrice < salePrice) {
            throw new IllegalArgumentException("MrpPrice must be greater than salePrice");
        } else if (mrpPrice <= 0) {
            throw new IllegalArgumentException("MrpPrice must be greater than 0");
        }
        double discount = mrpPrice - salePrice;
        double discountPercentage = (discount / mrpPrice) * 100;

        return (int) discountPercentage;
    }

    @Override
    public void deleteProduct(Long productId) throws ProductException {
        Product product = findProductById(productId);
        productRepository.delete(product);

    }

    @Override
    public Product updateProduct(Long productId, Product product) throws ProductException {
        findProductById(productId);
        product.setId(productId);

        return productRepository.save(product);
    }

    @Override
    public Product findProductById(Long productId) throws ProductException {
        return productRepository.findById(productId).orElseThrow(() -> new ProductException("Product not found with this id" + productId));
    }

    @Override
    public List<Product> searchProducts(String query) {
        return productRepository.searchProduct(query);
    }

    @Override
    public Page<Product> getAllProducts(String category, String brand, String colors, String sizes, Integer minPrice, Integer maxPrice, Integer minDiscount, String sort, String stock, Integer pageNumber) {
        Specification<Product> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (category != null) {
                Join<Product, Category> categoryJoin = root.join("category");
                predicates.add(criteriaBuilder.equal(categoryJoin.get("categoryId"), category));
            }
            if (colors != null && !colors.isEmpty()) {
                // System.out.println("colors: " + colors);
                predicates.add(criteriaBuilder.equal(root.get("color"), colors));
            }
            if (sizes != null && !sizes.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("size"), sizes));
            }
            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("Selling price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("Selling price"), maxPrice));
            }
            if (minDiscount != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("Discount"), minDiscount));
            }
            if (stock != null) {
                predicates.add(criteriaBuilder.equal(root.get("stock"), stock));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        Pageable pageable;
        if (sort != null && !sort.isEmpty()) {
            pageable = switch (sort) {
                case "price_low" -> PageRequest.of(pageNumber != null ? pageNumber : 0, 10,
                        Sort.by("sellingPrice").ascending());
                case "price_high" -> PageRequest.of(pageNumber != null ? pageNumber : 0, 10,
                        Sort.by("sellingPrice").descending());
                default -> PageRequest.of(pageNumber != null ? pageNumber : 0, 10,
                        Sort.unsorted());
            };
        } else {
            pageable = PageRequest.of(pageNumber != null ? pageNumber : 0, 10, Sort.unsorted());
        }
        return productRepository.findAll(specification, pageable);
    }


    @Override
    public List<Product> getProductBySellerId(Long sellerId) {
        return productRepository.findBySellerId(sellerId);
    }
}
