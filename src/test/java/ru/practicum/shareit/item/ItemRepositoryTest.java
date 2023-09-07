package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    private Item item;
    private User owner;

    @BeforeEach
    void beforeEach() {
        owner = new User("owner", "owner@mail");
        item = new Item("Item name", "Item description", true, owner, null);
        entityManager.persist(owner);
        entityManager.persist(item);
        entityManager.flush();
    }

    @Test
    void findByOwner_IdTest() {
        List<Item> foundItems = itemRepository.findByOwner_Id(owner.getId(), Sort.unsorted());

        assertEquals(1, foundItems.size());
        assertEquals(item.getId(), foundItems.get(0).getId());
        assertEquals(item.getName(), foundItems.get(0).getName());
    }

    @Test
    void searchTest() {
        List<Item> foundItems = itemRepository.search("descR");

        assertEquals(1, foundItems.size());
        assertEquals(item.getId(), foundItems.get(0).getId());
        assertEquals(item.getName(), foundItems.get(0).getName());
    }

}
