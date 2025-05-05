package iway.irshad.service.impl;

import iway.irshad.entity.Product;
import iway.irshad.entity.Review;
import iway.irshad.entity.User;
import iway.irshad.repository.ReviewRepository;
import iway.irshad.request.CreateReviewRequest;
import iway.irshad.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;

    @Override
    public Review createReview(CreateReviewRequest request, User user, Product product) {

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setReviewText(request.getReviewText());
        review.setRating(request.getReviewRating());
        review.setProductImage(request.getProductImage());

        product.getReviews().add(review);

        return reviewRepository.save(review);
    }

    @Override
    public List<Review> getReviewsByProductId(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    @Override
    public Review updateReview(Long reviewId, String reviewText, double reviewRating, Long userId) throws Exception {

        Review review = getReviewById(reviewId);
        if (review.getReviewText().equals(reviewText)) {
            review.setReviewText(reviewText);
            review.setRating(reviewRating);
            return reviewRepository.save(review);
        }
        throw new Exception("You are not allowed to update this review");

    }

    @Override
    public void deleteReview(Long reviewId, Long userId) throws Exception {
        Review review = getReviewById(reviewId);
        if (review.getUser().getId().equals(userId)) {
            reviewRepository.delete(review);
        }
        throw new Exception("You are not allowed to delete this review");
    }

    @Override
    public Review getReviewById(Long reviewId) throws Exception {
        return reviewRepository.findById(reviewId).orElseThrow(() ->
                new Exception("Review not found"));
    }
}
