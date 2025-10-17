package com.app.brainmap.controllers;

import com.app.brainmap.domain.InquiryStatus;
import com.app.brainmap.domain.InquiryType;
import com.app.brainmap.domain.dto.CreateInquiryRequestDto;
import com.app.brainmap.domain.dto.InquiryDto;
import com.app.brainmap.services.InquiryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inquiries")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    @GetMapping
    public ResponseEntity<Page<InquiryDto>> getAllInquiries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
    ){
        Page<InquiryDto> inquiries = inquiryService.getAllInquiries(page, size, sortBy, direction);
        return ResponseEntity.ok(inquiries);
    }

    @PostMapping("/create")
    public ResponseEntity<InquiryDto> createInquiry(@RequestBody @Valid CreateInquiryRequestDto request) {
        InquiryDto created = inquiryService.createInquiry(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<InquiryDto>> filterInquiries(
            @RequestParam(required = false) InquiryStatus status,
            @RequestParam(required = false) InquiryType type,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
    ){
        Page<InquiryDto> inquiries = inquiryService.filterInquiries(status, type, search, page, size, sortBy, direction);
        return ResponseEntity.ok(inquiries);
    }

}
