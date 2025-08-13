package com.app.brainmap.services;

import com.app.brainmap.domain.dto.ReviewDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewService {
    List<ReviewDto> listReviews();
    ReviewDto createReview(ReviewDto reviewDto);
    Optional<ReviewDto> getReview(UUID reviewId);
    ReviewDto updateReview(UUID reviewId, ReviewDto reviewDto);
    void deleteReview(UUID reviewId);
}