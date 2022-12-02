package ru.practicum.shareit.booking;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    private Booking booking;
    private LocalDateTime start;
    private LocalDateTime end;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService mockService;

    @BeforeEach
    void setUp() {
        start = LocalDateTime.now().plusDays(1).withNano(0);
        end = LocalDateTime.now().plusDays(2).withNano(0);
        booking = Booking.builder()
                .id(1L)
                .status(BookingStatus.WAITING)
                .start(start)
                .end(end)
                .booker(User.builder()
                        .id(1L)
                        .build())
                .item(Item.builder()
                        .id(1L)
                        .build())
                .build();
        Mockito.when(mockService.findById(1L, 1L)).thenReturn(booking);
    }

    @Test
    void createTest() throws Exception {
        Mockito.when(mockService.create(Mockito.any(Booking.class), Mockito.anyLong())).thenReturn(booking);
        mvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(booking)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is(BookingStatus.WAITING.toString())))
                .andExpect(jsonPath("$.start", is(start.toString())))
                .andExpect(jsonPath("$.end", is(end.toString())));
        Mockito.verify(mockService, Mockito.times(1)).create(Mockito.any(Booking.class), Mockito.anyLong());
    }

    @Test
    void findByIdTest() throws Exception {
        mvc.perform(get("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(booking)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is(BookingStatus.WAITING.toString())))
                .andExpect(jsonPath("$.start", is(start.toString())))
                .andExpect(jsonPath("$.end", is(end.toString())));
        Mockito.verify(mockService, Mockito.times(1)).findById(1L, 1L);
    }

    @Test
    void findByBookerTest() throws Exception {
        Mockito.when(mockService.findByBooker(1L, "ALL", 0, 20))
                .thenReturn(List.of(booking));
        mvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].status", is(BookingStatus.WAITING.toString())))
                .andExpect(jsonPath("$[0].start", is(start.toString())))
                .andExpect(jsonPath("$[0].end", is(end.toString())));
        Mockito.verify(mockService, Mockito.times(1)).findByBooker(1L, "ALL", 0, 20);
    }

    @Test
    void findByOwnerTest() throws Exception {
        Mockito.when(mockService.findByOwner(1L, "ALL", 0, 20))
                .thenReturn(List.of(booking));
        mvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].status", is(BookingStatus.WAITING.toString())))
                .andExpect(jsonPath("$[0].start", is(start.toString())))
                .andExpect(jsonPath("$[0].end", is(end.toString())));
        Mockito.verify(mockService, Mockito.times(1)).findByOwner(1L, "ALL", 0, 20);
    }

    @Test
    void updateStatusTest() throws Exception {
        booking.setStatus(BookingStatus.APPROVED);
        Mockito.when(mockService.updateStatus(1L, 1L, true)).thenReturn(booking);
        mvc.perform(patch("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("approved", String.valueOf(true))
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is(BookingStatus.APPROVED.toString())))
                .andExpect(jsonPath("$.start", is(start.toString())))
                .andExpect(jsonPath("$.end", is(end.toString())));
        Mockito.verify(mockService, Mockito.times(1)).updateStatus(1L, 1L, true);
    }
}