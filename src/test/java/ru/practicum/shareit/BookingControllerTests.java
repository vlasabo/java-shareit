package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.webjars.NotFoundException;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.handler.ErrorHandler;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock
    BookingService bookingService;

    @InjectMocks
    private BookingController controller;

    private Booking booking;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(ErrorHandler.class)
                .build();

        booking = new Booking();
        booking.setId(1);
        booking.setItemOwnerId(1);
        booking.setUserId(2);
        booking.setItemId(1);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(2));

        mockMvc.perform(post("/bookings")
                .content(objectMapper.writeValueAsString(booking)));
    }

    @Test
    void addBookingTest() throws Exception {
        when(bookingService.addBooking(any(), anyInt()))
                .thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(booking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isOk());
    }

    @Test
    void confirmationOrRejectionBookingRequestTest() throws Exception {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingService.confirmOrRejectBooking(1, true, 1))
                .thenReturn(booking);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(booking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isOk());
    }

    @Test
    void getAllBookingsTest() throws Exception {
        when(bookingService.getBookingsForUser(any(), anyString()))
                .thenReturn(List.of(booking));

        this.mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(booking))));
    }

    @Test
    void getBookingForOwnerTest() throws Exception {
        when(bookingService.getBookingsForOwner(anyInt(), anyString()))
                .thenReturn(List.of(booking));

        this.mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(booking))))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingByIdTest() throws Exception {
        when(bookingService.getBooking(1, 1))
                .thenReturn(booking);

        this.mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(content().json(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingForOwnerWithNotExistingOwnerTest() throws Exception {
        when(bookingService.getBookingsForOwner(anyInt(), anyString()))
                .thenThrow(NotFoundException.class);

        this.mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 4))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBookingByIdWithNotExistingBookingIdTest() throws Exception {
        when(bookingService.getBooking(100, 100))
                .thenThrow(NotFoundException.class);

        this.mockMvc.perform(get("/bookings/100")
                        .header("X-Sharer-User-Id", 100))
                .andExpect(status().isNotFound());
    }
}
