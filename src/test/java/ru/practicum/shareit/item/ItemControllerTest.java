package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.dto.ItemDtoComment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    @Test
    void addItemTest() throws Exception {

        ItemDto itemDto = new ItemDto("Item name", "Item description", true, null);
        ItemDto itemDtoWithId = new ItemDto("Item name", "Item description", true, null);
        itemDtoWithId.setId(1L);

        Mockito.when(itemService.addNewItem(1L, itemDto))
                .thenReturn(itemDtoWithId);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoWithId.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));

    }

    @Test
    void editItemTest() throws Exception {
        ItemDto itemDto = new ItemDto("Item name", "Item description", true, null);
        itemDto.setId(1L);

        Mockito.when(itemService.editItem(1L, itemDto, 1L))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/{itemId}", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void getItemTest() throws Exception {
        ItemDto itemDto = new ItemDto("Item name", "Item description", true, null);
        itemDto.setId(1L);

        ItemDtoBooking itemDtoBooking = new ItemDtoBooking(1L, "Item name", "Item description",
                true, new User(1L, "user", "user@mail"), null, null,
                null, null);

        Mockito.when(itemService.getItem(1L, 1L))
                .thenReturn(itemDtoBooking);

        mvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoBooking.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoBooking.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoBooking.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoBooking.getAvailable())))
                .andExpect(jsonPath("$.lastBooking", is(itemDtoBooking.getLastBooking())));
    }

    @Test
    void getItemsByUserIdTest() throws Exception {
        ItemDtoBooking itemDtoBooking = new ItemDtoBooking(1L, "Item name", "Item description",
                true, new User(1L, "user", "user@mail"), null, null,
                null, null);
        List<ItemDtoBooking> itemDtoBookings = new ArrayList<>();

        itemDtoBookings.add(itemDtoBooking);

        Mockito.when(itemService.getItemsByUserId(1L))
                .thenReturn(itemDtoBookings);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[0].id", is(itemDtoBooking.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemDtoBooking.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDtoBooking.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemDtoBooking.getAvailable())))
                .andExpect(jsonPath("$.[0].lastBooking", is(itemDtoBooking.getLastBooking())));
    }

    @Test
    void findItemsTest() throws Exception {
        Item item = new Item("Item name", "Item description",
                true, new User(1L, "user", "user@mail"), null);
        List<Item> items = new ArrayList<>();
        items.add(item);

        Mockito.when(itemService.findItems("nam"))
                .thenReturn(items);

        mvc.perform(get("/items/search?text=nam")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(item.getName())))
                .andExpect(jsonPath("$.[0].description", is(item.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(item.getAvailable())));
    }

    @Test
    void addCommentTest() throws Exception {
        CommentDto commentDto = new CommentDto("Comment");
        ItemDtoComment itemDtoComment =
                new ItemDtoComment(1L, "Comment", "Author", LocalDateTime.now());

        Long itemId = 1L;
        Long userId = 1L;

        Mockito.when(itemService.addComment(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(itemDtoComment);

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .content(mapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoComment.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(itemDtoComment.getText())))
                .andExpect(jsonPath("$.authorName", is(itemDtoComment.getAuthorName())));
    }
}
