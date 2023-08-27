package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.ItemBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.dto.ItemDtoComment;
import ru.practicum.shareit.item.map.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository,
                           UserRepository userRepository,
                           BookingRepository bookingRepository,
                           CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    @Transactional
    public Item addNewItem(Long userId, ItemDto itemDto) {
        if (userRepository.findById(userId).isPresent()) {
            Item item = ItemMapper.toItem(userRepository.findById(userId).get(), itemDto);
            return itemRepository.save(item);
        } else {
            throw new NotFoundException("User with id=" + userId + " not found!");
        }
    }

    @Override
    @Transactional
    public Item editItem(Long userId, ItemDto itemDto, long itemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("{\"message\": \"User with id=" + userId + " not found.\"}"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("{\"message\": \"Item with id=" + itemId + " not found.\"}"));
        if (item.getOwner().getId() != userId) {
            throw new NotFoundException("{\"message\": \"Item with id=" + itemId
                    + " does not belong to user with id=" + userId + ".\"}");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public ItemDtoBooking getItem(long itemId, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("{\"message\": \"Item with id=" + itemId + " not found.\"}"));
        ItemBooking lastBooking = null;
        ItemBooking nextBooking = null;
        if (item.getOwner().getId() == userId) {
            List<Booking> bookings = bookingRepository
                    .findByItem_IdAndStatus(itemId, Status.APPROVED, Sort.by("start").ascending());
            for (int i = 0; i < bookings.size(); i++) {
                if (bookings.get(i).getStart().isAfter(LocalDateTime.now())) {
                    nextBooking = new ItemBooking(bookings.get(i).getId(), bookings.get(i).getBooker().getId());
                    if (i - 1 >= 0) {
                        lastBooking = new ItemBooking(bookings.get(i - 1).getId(), bookings.get(i - 1).getBooker().getId());
                    }

                    break;
                }
                if (bookings.size() == 1 && nextBooking == null) {
                    lastBooking = new ItemBooking(bookings.get(i).getId(), bookings.get(i).getBooker().getId());
                }
            }
        }

        List<Comment> comments = commentRepository.findByItem_Id(itemId);
        List<ItemDtoComment> itemDtoComments = new ArrayList<>();
        for (Comment comment : comments) {
            ItemDtoComment itemDtoComment = new ItemDtoComment(
                    comment.getId(),
                    comment.getText(),
                    comment.getAuthor().getName(),
                    comment.getCreated()
            );
            itemDtoComments.add(itemDtoComment);
        }

        ItemDtoBooking itemDtoBooking = new ItemDtoBooking(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                lastBooking,
                nextBooking,
                itemDtoComments,
                item.getRequest()
        );
        return itemDtoBooking;
    }

    @Override
    @Transactional
    public List<ItemDtoBooking> getItemsByUserId(long userId) {
        List<Item> items = itemRepository.findByOwner_Id(userId, Sort.by("id"));

        Map<Long, Item> itemMap = items.stream().collect(Collectors.toMap(Item::getId, Function.identity()));

        List<Booking> allBookings = bookingRepository
                .findByItem_IdInAndStatus(itemMap.keySet(), Status.APPROVED, Sort.by("start").ascending());

        List<ItemDtoBooking> itemsWithBookings = new ArrayList<>();

        for (Item item : items) {
//            List<Booking> bookings = bookingRepository
//                    .findByItem_IdAndStatus(item.getId(), Status.APPROVED, Sort.by("start").ascending());
            List<Booking> bookings = allBookings.stream()
                    .filter(b -> b.getItem().getId() == item.getId())
                    .collect(Collectors.toList());
            ItemBooking lastBooking = null;
            ItemBooking nextBooking = null;
            for (int i = 0; i < bookings.size(); i++) {
                if (bookings.get(i).getStart().isAfter(LocalDateTime.now())) {
                    nextBooking = new ItemBooking(bookings.get(i).getId(), bookings.get(i).getBooker().getId());
                    if (i - 1 >= 0) {
                        lastBooking = new ItemBooking(bookings.get(i - 1).getId(), bookings.get(i - 1).getBooker().getId());
                    }
                    break;
                }
            }

            List<Comment> comments = commentRepository.findByItem_Id(item.getId());
            List<ItemDtoComment> itemDtoComments = new ArrayList<>();
            for (Comment comment : comments) {
                ItemDtoComment itemDtoComment = new ItemDtoComment(
                        comment.getId(),
                        comment.getText(),
                        comment.getAuthor().getName(),
                        comment.getCreated()
                );
                itemDtoComments.add(itemDtoComment);
            }

            ItemDtoBooking itemDtoBooking = new ItemDtoBooking(
                    item.getId(),
                    item.getName(),
                    item.getDescription(),
                    item.getAvailable(),
                    item.getOwner(),
                    lastBooking,
                    nextBooking,
                    itemDtoComments,
                    item.getRequest());
            itemsWithBookings.add(itemDtoBooking);
        }

        return itemsWithBookings;
    }

    @Override
    @Transactional
    public List<Item> findItems(String text) {
        if (!text.isBlank()) {
            return itemRepository.search(text.toUpperCase());
        }
        return Collections.emptyList();
    }

    @Override
    @Transactional
    public ItemDtoComment addComment(Long userId, long itemId, CommentDto commentDto) {
        List<Booking> bookings = bookingRepository
                .findByBooker_IdAndItem_IdAndEndIsBefore(userId, itemId, LocalDateTime.now());
        if (bookings == null || bookings.isEmpty()) {
            throw new NotAvailableException("{\"message\": \"There is no booking with item with id="
                    + itemId + " and user with id=" + userId + ".\"}");
        }
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("{\"message\": \"Item with id=" + itemId + " not found.\"}"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("{\"message\": \"User with id=" + userId + " not found.\"}"));
        Comment comment = new Comment(
                commentDto.getText(),
                item,
                user,
                LocalDateTime.now()
        );
        comment = commentRepository.save(comment);

        return new ItemDtoComment(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

}
