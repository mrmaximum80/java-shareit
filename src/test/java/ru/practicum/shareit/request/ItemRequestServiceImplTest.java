package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoItems;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplTest {

    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final ItemRequestService itemRequestService;

    User requestor;
    UserDto userDto;
    ItemRequestDto itemRequestDto;

    @BeforeEach
    void beforeEach() {
        userDto = new UserDto("requestor", "owner@mail");
        requestor = userService.createUser(userDto);
        itemRequestDto = new ItemRequestDto("Request description");
    }

    @Test
    void addRequestTest() {
        ItemRequest itemRequest = itemRequestService.addRequest(requestor.getId(), itemRequestDto);

        assertNotNull(itemRequest.getId());
        assertEquals("Request description", itemRequest.getDescription());
        assertEquals("requestor", itemRequest.getRequestor().getName());
    }

    @Test
    void getRequestsByUserId() {
        ItemRequest itemRequest = itemRequestService.addRequest(requestor.getId(), itemRequestDto);
        List<ItemRequestDtoItems> itemRequests = itemRequestService.getRequestsByUserId(requestor.getId());

        assertEquals(1, itemRequests.size());
        assertNotNull(itemRequests.get(0).getId());
        assertEquals("Request description", itemRequests.get(0).getDescription());
        assertNotNull(itemRequests.get(0).getCreated());
        assertEquals(0, itemRequests.get(0).getItems().size());
    }

    @Test
    void getAllRequestsTest() {
        ItemRequest itemRequest = itemRequestService.addRequest(requestor.getId(), itemRequestDto);
        List<ItemRequestDtoItems> itemRequests = itemRequestService.getAllRequests(100L, 0, 20);

        assertEquals(1, itemRequests.size());
        assertNotNull(itemRequests.get(0).getId());
        assertEquals("Request description", itemRequests.get(0).getDescription());
        assertNotNull(itemRequests.get(0).getCreated());
        assertEquals(0, itemRequests.get(0).getItems().size());
    }

    @Test
    void getAllRequestsTestNullebleFromAndSize() {
        ItemRequest itemRequest = itemRequestService.addRequest(requestor.getId(), itemRequestDto);
        List<ItemRequestDtoItems> itemRequests = itemRequestService.getAllRequests(100L, null, null );

        assertEquals(1, itemRequests.size());
        assertNotNull(itemRequests.get(0).getId());
        assertEquals("Request description", itemRequests.get(0).getDescription());
        assertNotNull(itemRequests.get(0).getCreated());
        assertEquals(0, itemRequests.get(0).getItems().size());
    }

    @Test
    void getRequestByIdTest() {
        ItemRequest itemRequest = itemRequestService.addRequest(requestor.getId(), itemRequestDto);
        ItemRequestDtoItems itemRequestDtoItems = itemRequestService
                .getRequestById(requestor.getId(), itemRequest.getId());


        assertNotNull(itemRequestDtoItems.getId());
        assertEquals("Request description", itemRequestDtoItems.getDescription());
        assertNotNull(itemRequestDtoItems.getCreated());
        assertEquals(0, itemRequestDtoItems.getItems().size());
    }

}
