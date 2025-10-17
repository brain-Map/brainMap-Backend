package com.app.brainmap.services.impl;

import com.app.brainmap.domain.InquiryStatus;
import com.app.brainmap.domain.InquiryType;
import com.app.brainmap.domain.dto.CreateInquiryRequestDto;
import com.app.brainmap.domain.dto.InquiryDto;
import com.app.brainmap.domain.dto.InquiryOverviewDto;
import com.app.brainmap.domain.dto.RespondInquiryRequestDto;
import com.app.brainmap.domain.entities.Inquiry;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.mappers.InquiryMapper;
import com.app.brainmap.repositories.InquiryRepository;
import com.app.brainmap.repositories.UserRepository;
import com.app.brainmap.services.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class InquiryServiceImpl implements InquiryService {

    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;
    private final InquiryMapper inquiryMapper;

    @Override
    public InquiryDto createInquiry(CreateInquiryRequestDto request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + request.getUserId()));

        Inquiry inquiry = Inquiry.builder()
                .user(user)
                .inquiryType(request.getInquiryType())
                .title(request.getTitle())
                .inquiryContent(request.getInquiryContent())
                .build();

        Inquiry saved = inquiryRepository.save(inquiry);
        return inquiryMapper.toDto(saved);
    }

    @Override
    public Page<InquiryDto> getAllInquiries(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = "ASC".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return inquiryRepository.findAll(pageable).map(inquiryMapper::toDto);
    }

    @Override
    public Page<InquiryDto> filterInquiries(InquiryStatus status, InquiryType type, String search, int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = "ASC".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        String q = (search == null) ? "" : search.trim();
        return inquiryRepository.findByFilters(status, type, q, pageable).map(inquiryMapper::toDto);
    }

    @Override
    public InquiryOverviewDto getOverview() {
        long total = inquiryRepository.count();
        long pending = inquiryRepository.countByStatus(InquiryStatus.PENDING);
        long resolved = inquiryRepository.countByStatus(InquiryStatus.RESOLVED);
        long reviewed = inquiryRepository.countByStatus(InquiryStatus.REVIEWED);
        return InquiryOverviewDto.builder()
                .totalInquiries(total)
                .pending(pending)
                .resolved(resolved)
                .reviewed(reviewed)
                .build();
    }

    @Override
    public InquiryDto respondToInquiry(java.util.UUID inquiryId, java.util.UUID resolverId, RespondInquiryRequestDto request) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new NoSuchElementException("Inquiry not found with id: " + inquiryId));

        User resolver = userRepository.findById(resolverId)
                .orElseThrow(() -> new NoSuchElementException("Resolver user not found with id: " + resolverId));

        inquiry.setResolver(resolver);
        inquiry.setStatus(request.getStatus());
        inquiry.setResponseContent(request.getResponseContent());
        inquiry.setResolvedAt(java.time.LocalDateTime.now());

        Inquiry saved = inquiryRepository.save(inquiry);
        return inquiryMapper.toDto(saved);
    }

    @Override
    public Void deleteInquiry(java.util.UUID inquiryId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new NoSuchElementException("Inquiry not found with id: " + inquiryId));
        inquiryRepository.delete(inquiry);
        return null;
    }
}
