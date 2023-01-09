package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
class BookingIntegrationTests {

    @Autowired
    BookingService bookingService;
    @Autowired
    UserService userService;
    @Autowired
    ItemService itemService;
    Booking booking;
    User user;
    User user2;
    Item item;


    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1);
        user.setEmail("mail@mail.com");
        user.setName("name");
        user2 = new User();
        user2.setId(2);
        user2.setEmail("mail2@mail.com");
        user2.setName("name2");
        userService.save(user);
        userService.save(user2);

        item = new Item();
        item.setId(1);
        item.setOwnerId(2);
        item.setOwner(user2);
        item.setName("itemName");
        item.setDescription("itemDesc");
        item.setAvailable(true);
        itemService.save(ItemMapper.toItemDto(item), 2);

        booking = new Booking();
        booking.setUserId(1);
        booking.setId(1);
        booking.setItemId(1);
        booking.setItemOwnerId(2);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void addBooking() {
        Assertions.assertEquals(bookingService.addBooking(booking, 1), booking);
    }

    @Test
    void confirmBooking() {
        bookingService.addBooking(booking, 1);
        booking.setStatus(BookingStatus.APPROVED);
        Assertions.assertEquals(bookingService.confirmOrRejectBooking(1, true, 2), booking);
    }

    @Test
    void getBooking() {
        bookingService.addBooking(booking, 1);
        Assertions.assertEquals(bookingService.getBooking(1, 1), booking);
    }

    @Test
    void getBookingForUser() {
        bookingService.addBooking(booking, 1);
        Assertions.assertEquals(bookingService.getBookingsForUser(1, "ALL", 0, 20),
                List.of(booking));
    }

    @Test
    void getBookingForOwner() {
        bookingService.addBooking(booking, 1);
        Assertions.assertEquals(bookingService.getBookingsForOwner(2, "ALL", 0, 20),
                List.of(booking));
    }
}
