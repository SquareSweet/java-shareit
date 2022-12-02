package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    private LocalDateTime created;
    private ItemRequest request;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService mockService;

    @BeforeEach
    void setUp() {
        created = LocalDateTime.now().withNano(0);
        request = ItemRequest.builder()
                .id(1L)
                .requester(User.builder().id(1L).build())
                .description("description")
                .created(created)
                .items(List.of(Item.builder().id(1L).build()))
                .build();
        Mockito.when(mockService.findById(1L, 1L)).thenReturn(request);
    }

    @Test
    void createTest() throws Exception {
        Mockito.when(mockService.create(request, 1L)).thenReturn(request);
        mvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.created", is(created.toString())));
        Mockito.verify(mockService, Mockito.times(1)).create(request, 1L);
    }

    @Test
    void findByIdTest() throws Exception {
        mvc.perform(get("/requests/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.created", is(created.toString())));
        Mockito.verify(mockService, Mockito.times(1)).findById(1L, 1L);
    }

    @Test
    void findByRequesterTest() throws Exception {
        Mockito.when(mockService.findByUser(1L)).thenReturn(List.of(request));
        mvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is("description")))
                .andExpect(jsonPath("$[0].created", is(created.toString())));
        Mockito.verify(mockService, Mockito.times(1)).findByUser(1L);
    }

    @Test
    void findAllTest() throws Exception {
        Mockito.when(mockService.findAll(1L, 0, 20)).thenReturn(List.of(request));
        mvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is("description")))
                .andExpect(jsonPath("$[0].created", is(created.toString())));
        Mockito.verify(mockService, Mockito.times(1)).findAll(1L, 0, 20);
    }
}