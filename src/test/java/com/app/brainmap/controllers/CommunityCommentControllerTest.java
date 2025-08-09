// package com.app.brainmap.controllers;

// import com.app.brainmap.domain.dto.CreateCommunityCommentRequestDto;
// import com.app.brainmap.domain.dto.CommunityCommentDto;
// import com.app.brainmap.services.CommunityCommentService;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;

// import java.time.LocalDateTime;
// import java.util.Arrays;
// import java.util.UUID;

// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.eq;
// import static org.mockito.Mockito.when;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @WebMvcTest(CommunityCommentController.class)
// public class CommunityCommentControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @MockBean
//     private CommunityCommentService commentService;

//     @Autowired
//     private ObjectMapper objectMapper;

//     @Test
//     public void testCreateComment() throws Exception {
//         // Given
//         UUID postId = UUID.randomUUID();
//         CreateCommunityCommentRequestDto requestDto = new CreateCommunityCommentRequestDto();
//         requestDto.setContent("This is a test comment");

//         CommunityCommentDto responseDto = new CommunityCommentDto();
//         responseDto.setId(UUID.randomUUID());
//         responseDto.setContent("This is a test comment");
//         responseDto.setAuthorId(UUID.randomUUID());
//         responseDto.setAuthorName("Test User");
//         responseDto.setCreatedAt(LocalDateTime.now());

//         when(commentService.createComment(eq(postId), any(CreateCommunityCommentRequestDto.class)))
//                 .thenReturn(responseDto);

//         // When & Then
//         mockMvc.perform(post("/api/v1/posts/{postId}/comments", postId)
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(objectMapper.writeValueAsString(requestDto)))
//                 .andExpect(status().isCreated())
//                 .andExpect(jsonPath("$.content").value("This is a test comment"))
//                 .andExpect(jsonPath("$.authorName").value("Test User"));
//     }

//     @Test
//     public void testGetCommentsByPost() throws Exception {
//         // Given
//         UUID postId = UUID.randomUUID();

//         CommunityCommentDto comment1 = new CommunityCommentDto();
//         comment1.setId(UUID.randomUUID());
//         comment1.setContent("First comment");
//         comment1.setAuthorName("User 1");

//         CommunityCommentDto comment2 = new CommunityCommentDto();
//         comment2.setId(UUID.randomUUID());
//         comment2.setContent("Second comment");
//         comment2.setAuthorName("User 2");

//         when(commentService.getCommentsByPost(postId))
//                 .thenReturn(Arrays.asList(comment1, comment2));

//         // When & Then
//         mockMvc.perform(get("/api/v1/posts/{postId}/comments", postId))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.length()").value(2))
//                 .andExpect(jsonPath("$[0].content").value("First comment"))
//                 .andExpect(jsonPath("$[1].content").value("Second comment"));
//     }
// }
