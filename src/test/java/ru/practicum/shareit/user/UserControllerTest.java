package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    private User user;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService mockService;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("user")
                .email("user@user.com")
                .build();
        Mockito.when(mockService.findById(1L)).thenReturn(user);
    }

    @Test
    void createTest() throws Exception {
        Mockito.when(mockService.create(user)).thenReturn(user);
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("user")))
                .andExpect(jsonPath("$.email", is("user@user.com")));
        Mockito.verify(mockService, Mockito.times(1)).create(Mockito.any(User.class));
    }

    @Test
    void updateTest() throws Exception {
        User updatedUser = User.builder()
                .id(1L)
                .name("updated")
                .email("updated@updated.com")
                .build();
        Mockito.when(mockService.update(updatedUser)).thenReturn(updatedUser);
        mvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("updated")))
                .andExpect(jsonPath("$.email", is("updated@updated.com")));
        Mockito.verify(mockService, Mockito.times(1)).update(updatedUser);
    }

    @Test
    void findTest() throws Exception {
        mvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("user")))
                .andExpect(jsonPath("$.email", is("user@user.com")));
        Mockito.verify(mockService, Mockito.times(1)).findById(1L);
    }

    @Test
    void findAllTest() throws Exception {
        Mockito.when(mockService.findAll()).thenReturn(List.of(user));
        mvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("user")))
                .andExpect(jsonPath("$[0].email", is("user@user.com")));
        Mockito.verify(mockService, Mockito.times(1)).findAll();
    }

    @Test
    void deleteTest() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
        Mockito.verify(mockService, Mockito.times(1)).deleteById(1L);
    }
}