package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    private Item item;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService mockService;

    @BeforeEach
    void setUp() {
        item = Item.builder()
                .id(1L)
                .name("item")
                .description("description")
                .owner(User.builder()
                        .id(1L)
                        .build())
                .available(true)
                .build();
        Mockito.when(mockService.findById(1L, 1L)).thenReturn(ItemMapper.toItemDtoOut(item));
    }

    @Test
    void createTest() throws Exception {
        Mockito.when(mockService.create(item, 1L)).thenReturn(item);
        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("item")))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.available", is(true)));
        Mockito.verify(mockService, Mockito.times(1)).create(item, 1L);
    }

    @Test
    void updateTest() throws Exception {
        Item updatedItem = Item.builder()
                .id(1L)
                .name("updated")
                .description("updated description")
                .owner(User.builder()
                        .id(1L)
                        .build())
                .available(false)
                .build();
        Mockito.when(mockService.update(updatedItem, 1L)).thenReturn(updatedItem);
        mvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(updatedItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("updated")))
                .andExpect(jsonPath("$.description", is("updated description")))
                .andExpect(jsonPath("$.available", is(false)));
        Mockito.verify(mockService, Mockito.times(1)).update(updatedItem, 1L);
    }

    @Test
    void findTest() throws Exception {
        mvc.perform(get("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("item")))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.available", is(true)));
        Mockito.verify(mockService, Mockito.times(1)).findById(1L, 1L);
    }

    @Test
    void findByOwnerTest() throws Exception {
        Mockito.when(mockService.findByOwner(1L, 0, 20))
                .thenReturn(List.of(ItemMapper.toItemDtoOut(item)));
        mvc.perform(get("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("item")))
                .andExpect(jsonPath("$[0].description", is("description")))
                .andExpect(jsonPath("$[0].available", is(true)));
        Mockito.verify(mockService, Mockito.times(1)).findByOwner(1L, 0, 20);
    }

    @Test
    void findByTestTest() throws Exception {
        Mockito.when(mockService.findByText("text", 0, 20))
                .thenReturn(List.of(item));
        mvc.perform(get("/items/search")
                        .param("text", "text")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("item")))
                .andExpect(jsonPath("$[0].description", is("description")))
                .andExpect(jsonPath("$[0].available", is(true)));
        Mockito.verify(mockService, Mockito.times(1)).findByText("text", 0, 20);
    }

    @Test
    void deleteTest() throws Exception {
        mvc.perform(delete("/items/1"))
                .andExpect(status().isOk());
        Mockito.verify(mockService, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void addCommentTest() throws Exception {
        LocalDateTime created = LocalDateTime.now().withNano(0);
        Comment comment = Comment.builder()
                .id(1L)
                .text("text")
                .author(User.builder()
                        .id(1L)
                        .name("user")
                        .build())
                .item(item)
                .created(created)
                .build();
        Mockito.when(mockService.addComment(1L, 1L, comment)).thenReturn(comment);
        mvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is("text")))
                .andExpect(jsonPath("$.authorName", is("user")))
                .andExpect(jsonPath("$.created", is(created.toString())));
        Mockito.verify(mockService, Mockito.times(1)).addComment(1L, 1L, comment);
    }
}