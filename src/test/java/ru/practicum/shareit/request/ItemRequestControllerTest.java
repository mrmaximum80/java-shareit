package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoItems;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    User requestor;
    ItemRequestDto itemRequestDto;
    ItemRequestDtoItems itemRequestDtoItems;
    ItemRequest itemRequest;

    @BeforeEach
    void beforeEach() {
        requestor = new User(1L, "requestor", "requestor@mail");
        itemRequestDto = new ItemRequestDto("Item description");
        itemRequest = new ItemRequest(1L, "Item description", requestor, LocalDateTime.now());
        itemRequestDtoItems = new ItemRequestDtoItems(1L, "Item description",
                itemRequest.getCreated(), Collections.EMPTY_LIST);
    }

    @Test
    void addRequestTest() throws Exception {

        Mockito.when(itemRequestService.addRequest(Mockito.any(), Mockito.any()))
                .thenReturn(itemRequest);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequest.getId()), Long.class))
                .andExpect(jsonPath("$.requestor.name", is(itemRequest.getRequestor().getName())));
    }


    @Test
    void getRequestsByUserIdTest() throws Exception {

        List<ItemRequestDtoItems> itemRequestDtoItemsList = new ArrayList<>();
        itemRequestDtoItemsList.add(itemRequestDtoItems);

        Mockito.when(itemRequestService.getRequestsByUserId(requestor.getId()))
                .thenReturn(itemRequestDtoItemsList);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemRequest.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is("Item description")));
    }

    @Test
    void getAllRequestsTest() throws Exception {

        List<ItemRequestDtoItems> itemRequestDtoItemsList = new ArrayList<>();
        itemRequestDtoItemsList.add(itemRequestDtoItems);

        Mockito.when(itemRequestService.getAllRequests(100L, 0, 20))
                .thenReturn(itemRequestDtoItemsList);

        mvc.perform(get("/requests/all?from=0&size=20")
                        .header("X-Sharer-User-Id", 100)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemRequest.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is("Item description")));
    }

    @Test
    void getRequestByIdTest() throws Exception {

        Mockito.when(itemRequestService.getRequestById(requestor.getId(), itemRequest.getId()))
                .thenReturn(itemRequestDtoItems);

        mvc.perform(get("/requests//{requestId}", itemRequest.getId())
                        .header("X-Sharer-User-Id", requestor.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequest.getId()), Long.class))
                .andExpect(jsonPath("$.description", is("Item description")));
    }
}
