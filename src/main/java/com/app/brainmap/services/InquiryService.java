package com.app.brainmap.services;

import com.app.brainmap.domain.InquiryStatus;
import com.app.brainmap.domain.InquiryType;
import com.app.brainmap.domain.dto.CreateInquiryRequestDto;
import com.app.brainmap.domain.dto.InquiryDto;
import org.springframework.data.domain.Page;

public interface InquiryService {
    InquiryDto createInquiry(CreateInquiryRequestDto request);
    Page<InquiryDto> getAllInquiries(int page, int size, String sortBy, String direction);
    Page<InquiryDto> filterInquiries(InquiryStatus status, InquiryType type, String search, int page, int size, String sortBy, String direction);
}
