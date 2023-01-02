package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.webjars.NotFoundException;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingWithItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.handler.ErrorHandler;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingTests {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock
    BookingService bookingService;
    BookingService bookingService2;
    @Mock
    UserService userService;

    @Mock
    BookingRepository bookingRepository;
    @Mock
    ItemService itemService;
    @InjectMocks
    private BookingController controller;

    private Booking booking;
    private ItemDto itemDto;
    private User user;

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
        itemDto = new ItemDto();
        itemDto.setId(1);
        itemDto.setAvailable(true);
        itemDto.setOwnerId(2);
        booking.setItem(itemDto);
        user = new User();
        user.setId(1);
        booking.setBooker(user);
        booking.setItemOwnerId(2);
        mockMvc.perform(post("/bookings")
                .content(objectMapper.writeValueAsString(booking)));
        bookingService2 = new BookingService(bookingRepository, userService, itemService);
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
    void addBookingCorrect() {
        when(bookingRepository.save(any()))
                .thenReturn(booking);
        when(itemService.findItemDtoById(anyInt(), anyInt()))
                .thenReturn(itemDto);
        when(itemService.findItemById(anyInt()))
                .thenReturn(itemDto);
        when(userService.findUserById(anyInt()))
                .thenReturn(Optional.of(user));
        Assertions.assertEquals(bookingService2.addBooking(booking, 1), booking);
    }


    @Test
    void addBookingIncorrectStart() {
        booking.setStart(LocalDateTime.now().minusDays(1));
        Assertions.assertThrows(ValidationException.class, () -> bookingService2.addBooking(booking, 1));
    }

    @Test
    void addBookingIncorrectEnd() {
        booking.setEnd(LocalDateTime.now().minusDays(1));
        Assertions.assertThrows(ValidationException.class, () -> bookingService2.addBooking(booking, 1));
    }

    @Test
    void addBookingIncorrectEndBeforeStart() {
        booking.setEnd(LocalDateTime.now().plusDays(3));
        booking.setStart(LocalDateTime.now().plusDays(5));
        Assertions.assertThrows(ValidationException.class, () -> bookingService2.addBooking(booking, 1));
    }

    @Test
    void addBookingIncorrectRequestingFromItself() {
        booking.setUserId(2);
        user.setId(2);
        booking.setBooker(user);

        when(itemService.findItemDtoById(anyInt(), anyInt()))
                .thenReturn(itemDto);
        when(itemService.findItemById(anyInt()))
                .thenReturn(itemDto);
        when(userService.findUserById(anyInt()))
                .thenReturn(Optional.of(user));
        Assertions.assertThrows(NotFoundException.class, () -> bookingService2.addBooking(booking, 2));
    }

    @Test
    void addBookingIncorrectItemNotAvailable() {
        itemDto.setAvailable(false);
        booking.setItem(itemDto);

        when(itemService.findItemDtoById(anyInt(), anyInt()))
                .thenReturn(itemDto);
        when(itemService.findItemById(anyInt()))
                .thenReturn(itemDto);

        Assertions.assertThrows(ValidationException.class, () -> bookingService2.addBooking(booking, 2));
    }

    @Test
    void addBookingIncorrectUserNotFound() {
        booking.setUserId(99);

        when(itemService.findItemDtoById(anyInt(), anyInt()))
                .thenReturn(itemDto);
        when(itemService.findItemById(anyInt()))
                .thenReturn(itemDto);

        Assertions.assertThrows(NotFoundException.class, () -> bookingService2.addBooking(booking, 2));
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
    void confirmBookingIncorrectNotOwner() {
        booking.setUserId(99);
        user.setId(99);
        booking.setBooker(user);
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.of(booking));
        when(itemService.findItemDtoById(anyInt(), anyInt()))
                .thenReturn(itemDto);
        when(itemService.findItemById(anyInt()))
                .thenReturn(itemDto);
        when(userService.findUserById(anyInt()))
                .thenReturn(Optional.of(user));
        Assertions.assertThrows(NotFoundException.class, () -> bookingService2.confirmOrRejectBooking(1, true, 99));
    }

    @Test
    void confirmBookingIncorrectNoBookingWithId() {
        booking.setUserId(1);
        user.setId(1);
        booking.setBooker(user);
        itemDto.setOwnerId(99);
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> bookingService2.confirmOrRejectBooking(1, true, 99));
    }

    @Test
    void changeStatusConfirmedBooking() {
        booking.setUserId(1);
        user.setId(1);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.APPROVED);
        itemDto.setOwnerId(99);
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.of(booking));

        Assertions.assertThrows(ValidationException.class, () -> bookingService2.confirmOrRejectBooking(1, true, 99));
    }

    @Test
    void confirmBookingCorrectApprove() {
        booking.setUserId(1);
        user.setId(1);
        booking.setBooker(user);
        itemDto.setOwnerId(99);
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.of(booking));
        when(itemService.findItemDtoById(anyInt(), anyInt()))
                .thenReturn(itemDto);
        when(itemService.findItemById(anyInt()))
                .thenReturn(itemDto);
        when(userService.findUserById(anyInt()))
                .thenReturn(Optional.of(user));
        Assertions.assertEquals(bookingService2.confirmOrRejectBooking(1, true, 99), booking);
    }

    @Test
    void confirmBookingCorrectReject() {
        booking.setUserId(1);
        user.setId(1);
        booking.setBooker(user);
        itemDto.setOwnerId(99);
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.of(booking));
        when(itemService.findItemDtoById(anyInt(), anyInt()))
                .thenReturn(itemDto);
        when(itemService.findItemById(anyInt()))
                .thenReturn(itemDto);
        when(userService.findUserById(anyInt()))
                .thenReturn(Optional.of(user));
        Assertions.assertEquals(bookingService2.confirmOrRejectBooking(1, false, 99), booking);
    }

    @Test
    void getAllBookingsTest() throws Exception {
        when(bookingService.getBookingsForUser(any(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(booking));

        this.mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(booking))));
    }

    @Test
    void getAllBookingsForUserAll() {
        booking.setUserId(8);
        when(bookingRepository.findAllByUserIdOrderByStartDesc(any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(itemService.findItemDtoById(anyInt(), anyInt()))
                .thenReturn(itemDto);
        when(itemService.findItemById(anyInt()))
                .thenReturn(itemDto);
        when(userService.findUserById(anyInt()))
                .thenReturn(Optional.of(user));
        Assertions.assertEquals(bookingService2.getBookingsForUser(8, "ALL", 0, 20), List.of(booking));
    }

    @Test
    void getAllBookingsForUserPast() {
        booking.setUserId(8);
        Assertions.assertEquals(bookingService2.getBookingsForUser(8, "PAST", 0, 20), new ArrayList<>());
    }

    @Test
    void getAllBookingsForUserFuture() {
        booking.setUserId(8);
        Assertions.assertEquals(bookingService2.getBookingsForUser(8, "FUTURE", 0, 20), new ArrayList<>());
    }

    @Test
    void getAllBookingsForUserCurrent() {
        booking.setUserId(8);
        Assertions.assertEquals(bookingService2.getBookingsForUser(8, "CURRENT", 0, 20), new ArrayList<>());
    }

    @Test
    void getAllBookingsForUserRejected() {
        booking.setUserId(8);
        Assertions.assertEquals(bookingService2.getBookingsForUser(8, "REJECTED", 0, 20), new ArrayList<>());
    }

    @Test
    void getAllBookingsForUserWaiting() {
        booking.setUserId(8);
        Assertions.assertEquals(bookingService2.getBookingsForUser(8, "WAITING", 0, 20), new ArrayList<>());
    }

    @Test
    void getBookingForOwnerTest() throws Exception {
        when(bookingService.getBookingsForOwner(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(booking));

        this.mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(booking))))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingForOwnerCorrectAll() {
        booking.setUserId(8);
        when(itemService.findItemDtoById(anyInt(), anyInt()))
                .thenReturn(itemDto);
        when(itemService.findItemById(anyInt()))
                .thenReturn(itemDto);
        when(userService.findUserById(anyInt()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        Assertions.assertEquals(bookingService2.getBookingsForOwner(8, "ALL", 0, 20), List.of(booking));
    }

    @Test
    void getBookingForOwnerCorrectWaiting() {
        booking.setUserId(8);
        when(itemService.findItemDtoById(anyInt(), anyInt()))
                .thenReturn(itemDto);
        when(itemService.findItemById(anyInt()))
                .thenReturn(itemDto);
        when(userService.findUserById(anyInt()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(any(), any()))
                .thenReturn(List.of(booking));
        Assertions.assertEquals(bookingService2.getBookingsForOwner(8, "WAITING", 0, 20), List.of(booking));
    }

    @Test
    void getBookingForOwnerCorrectRejected() {
        booking.setUserId(8);
        when(itemService.findItemDtoById(anyInt(), anyInt()))
                .thenReturn(itemDto);
        when(itemService.findItemById(anyInt()))
                .thenReturn(itemDto);
        when(userService.findUserById(anyInt()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(any(), any()))
                .thenReturn(List.of(booking));
        Assertions.assertEquals(bookingService2.getBookingsForOwner(8, "REJECTED", 0, 20), List.of(booking));
    }

    @Test
    void getBookingForOwnerCorrectCurrent() {
        booking.setUserId(8);
        when(itemService.findItemDtoById(anyInt(), anyInt()))
                .thenReturn(itemDto);
        when(itemService.findItemById(anyInt()))
                .thenReturn(itemDto);
        when(userService.findUserById(anyInt()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(booking));
        Assertions.assertEquals(bookingService2.getBookingsForOwner(8, "CURRENT", 0, 20), List.of(booking));
    }

    @Test
    void getBookingForOwnerCorrectFuture() {
        booking.setUserId(8);
        when(itemService.findItemDtoById(anyInt(), anyInt()))
                .thenReturn(itemDto);
        when(itemService.findItemById(anyInt()))
                .thenReturn(itemDto);
        when(userService.findUserById(anyInt()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerIdAndStartAfterAndEndAfterOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(booking));
        Assertions.assertEquals(bookingService2.getBookingsForOwner(8, "FUTURE", 0, 20), List.of(booking));
    }

    @Test
    void getBookingForOwnerCorrectPast() {
        booking.setUserId(8);
        when(itemService.findItemDtoById(anyInt(), anyInt()))
                .thenReturn(itemDto);
        when(itemService.findItemById(anyInt()))
                .thenReturn(itemDto);
        when(userService.findUserById(anyInt()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(any(), any()))
                .thenReturn(List.of(booking));
        Assertions.assertEquals(bookingService2.getBookingsForOwner(8, "PAST", 0, 20), List.of(booking));
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
    void getBookingByIdCorrect() {
        booking.setUserId(8);
        booking.getItem().setOwner(user);
        booking.getItem().getOwner().setId(8);
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.of(booking));
        when(itemService.findItemDtoById(anyInt(), anyInt()))
                .thenReturn(itemDto);
        when(itemService.findItemById(anyInt()))
                .thenReturn(itemDto);
        when(userService.findUserById(anyInt()))
                .thenReturn(Optional.of(user));
        Assertions.assertEquals(bookingService2.getBooking(1, 8), booking);
    }

    @Test
    void getBookingByIdIncorrectNotAnOwner() {
        booking.setUserId(8);
        booking.getItem().setOwner(user);
        booking.getItem().getOwner().setId(8);
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.of(booking));
        when(itemService.findItemDtoById(anyInt(), anyInt()))
                .thenReturn(itemDto);
        when(itemService.findItemById(anyInt()))
                .thenReturn(itemDto);
        when(userService.findUserById(anyInt()))
                .thenReturn(Optional.of(user));
        Assertions.assertThrows(NotFoundException.class, () -> bookingService2.getBooking(1, 1));
    }

    @Test
    void getBookingByIdIncorrectBookingId() {
        booking.setUserId(8);
        booking.getItem().setOwner(user);
        booking.getItem().getOwner().setId(8);
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> bookingService2.getBooking(1, 8));
    }

    @Test
    void getBookingForOwnerWithNotExistingOwnerTest() throws Exception {
        when(bookingService.getBookingsForOwner(anyInt(), anyString(), anyInt(), anyInt()))
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

    @Test
    void bookingDtoTest() {
        BookingWithItemDto bookingWithItemDto = new BookingWithItemDto(1, 2);
        Assertions.assertEquals(BookingMapper.toBookingWithItemDto(booking), bookingWithItemDto);
    }
}
