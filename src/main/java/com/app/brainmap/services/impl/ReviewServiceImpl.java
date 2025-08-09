package com.app.brainmap.services.impl;

import com.app.brainmap.domain.dto.ReviewDto;
import com.app.brainmap.domain.entities.Promise;
import com.app.brainmap.domain.entities.Review;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.mappers.ReviewMapper;
import com.app.brainmap.repositories.PromiseRepository;
import com.app.brainmap.repositories.ReviewRepository;
import com.app.brainmap.repositories.UserRepository;
import com.app.brainmap.services.ReviewService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final UserRepository userRepository;
    private final PromiseRepository promiseRepository;

    public ReviewServiceImpl(
            ReviewRepository reviewRepository,
            ReviewMapper reviewMapper,
            UserRepository userRepository,
            PromiseRepository promiseRepository
    ) {
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
        this.userRepository = userRepository;
        this.promiseRepository = promiseRepository;
    }

    @Override
    public List<ReviewDto> listReviews() {
        return reviewRepository.findAll()
                .stream()
                .map(reviewMapper::toDto)
                .toList();
    }

    @Override
    public ReviewDto createReview(ReviewDto reviewDto) {
        Review review = reviewMapper.toEntity(reviewDto);

        User member = userRepository.findById(reviewDto.memberId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        User mentor = userRepository.findById(reviewDto.mentorId())
                .orElseThrow(() -> new IllegalArgumentException("Mentor not found"));
        Promise promise = promiseRepository.findById(reviewDto.promiseId())
                .orElseThrow(() -> new IllegalArgumentException("Promise not found"));

        review.setMember(member);
        review.setMentor(mentor);
        review.setPromise(promise);

        Review saved = reviewRepository.save(review);
        return reviewMapper.toDto(saved);
    }

    @Override
    public Optional<ReviewDto> getReview(UUID reviewId) {
        return reviewRepository.findById(reviewId)
                .map(reviewMapper::toDto);
    }

    @Override
    public ReviewDto updateReview(UUID reviewId, ReviewDto reviewDto) {
        Review existing = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        existing.setRate(reviewDto.rate());
        existing.setReview(reviewDto.review());

        if (!existing.getMember().getUserId().equals(reviewDto.memberId())) {
            User member = userRepository.findById(reviewDto.memberId())
                    .orElseThrow(() -> new IllegalArgumentException("Member not found"));
            existing.setMember(member);
        }
        if (!existing.getMentor().getUserId().equals(reviewDto.mentorId())) {
            User mentor = userRepository.findById(reviewDto.mentorId())
                    .orElseThrow(() -> new IllegalArgumentException("Mentor not found"));
            existing.setMentor(mentor);
        }
        if (!existing.getPromise().getPromiseId().equals(reviewDto.promiseId())) {
            Promise promise = promiseRepository.findById(reviewDto.promiseId())
                    .orElseThrow(() -> new IllegalArgumentException("Promise not found"));
            existing.setPromise(promise);
        }

        Review saved = reviewRepository.save(existing);
        return reviewMapper.toDto(saved);
    }

    @Override
    public void deleteReview(UUID reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}
