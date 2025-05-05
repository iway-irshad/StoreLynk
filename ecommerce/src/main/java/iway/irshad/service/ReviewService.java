package iway.irshad.service;

import iway.irshad.entity.Product;
import iway.irshad.entity.Review;
import iway.irshad.entity.User;
import iway.irshad.request.CreateReviewRequest;

import java.util.List;

public interface ReviewService {

    Review createReview(CreateReviewRequest request,
                        User user,
                        Product product
    );
    List<Review> getReviewsByProductId(Long productId);

    Review updateReview(
            Long reviewId,
            String reviewText,
            double reviewRating,
            Long userId
    ) throws Exception;

    void deleteReview(Long reviewId, Long userId) throws Exception;

    Review getReviewById(Long reviewId) throws Exception;
}
