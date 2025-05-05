package iway.irshad.controller;

import iway.irshad.entity.Product;
import iway.irshad.entity.Review;
import iway.irshad.entity.User;
import iway.irshad.request.CreateReviewRequest;
import iway.irshad.response.ApiResponse;
import iway.irshad.service.ProductService;
import iway.irshad.service.ReviewService;
import iway.irshad.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;
    private final ProductService productService;

    @GetMapping("products/{productId}/reviews")
    public ResponseEntity<List<Review>> getReviewsByProductId(
            @PathVariable Long productId
    ) {
        List<Review> reviews = reviewService.getReviewsByProductId(productId);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @PostMapping("products/{productId}/reviews")
    public ResponseEntity<Review> writeReview(
            @RequestBody CreateReviewRequest request,
            @PathVariable Long productId,
            @RequestHeader("Authorization") String jwtToken
            ) throws Exception {

        User user = userService.findUserByJwtToken(jwtToken);
        Product product = productService.findProductById(productId);
        Review review = reviewService.createReview(
                request,
                user,
                product
        );
        return new ResponseEntity<>(review, HttpStatus.OK);
    }

    @PatchMapping("/reviews/{reviewId}")
    public ResponseEntity<Review> updateReview(
            @RequestBody CreateReviewRequest request,
            @PathVariable Long reviewId,
            @RequestHeader("Authorization") String jwtToken
    ) throws Exception {

        User user = userService.findUserByJwtToken(jwtToken);
        Review review = reviewService.updateReview(
                reviewId,
                request.getReviewText(),
                request.getReviewRating(),
                user.getId()
        );
        return new ResponseEntity<>(review, HttpStatus.OK);
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse> deleteReview(
            @PathVariable Long reviewId,
            @RequestHeader("Authorization") String jwtToken
    ) throws Exception {

        User user = userService.findUserByJwtToken(jwtToken);

        reviewService.deleteReview(reviewId, user.getId());
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Review deleted");
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
