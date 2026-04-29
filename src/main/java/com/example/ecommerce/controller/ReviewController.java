package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ReviewDto;
import com.example.ecommerce.model.Review;
import com.example.ecommerce.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewDto>> getReviewsByProduct(@PathVariable String productId) {
        List<ReviewDto> reviews = reviewService.findByProductId(productId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reviews);
    }

    @PostMapping
    public ResponseEntity<ReviewDto> addReview(@Valid @RequestBody ReviewDto dto) {
        Review review = new Review();
        review.setProductId(dto.getProductId());
        review.setUserId(dto.getUserId());
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        return ResponseEntity.ok(toDto(reviewService.save(review)));
    }

    private ReviewDto toDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        dto.setProductId(review.getProductId());
        dto.setUserId(review.getUserId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        return dto;
    }
}
