package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User requestor;
    private ItemRequest request;

    @BeforeEach
    void beforeEach() {
        requestor = new User("requestor", "requestor@mail");
        request = new ItemRequest("Description", requestor, LocalDateTime.now());
        entityManager.persist(requestor);
        entityManager.persist(request);
        entityManager.flush();
    }

    @Test
    void findByRequestor_IdTest() {
        List<ItemRequest> foundRequests = itemRequestRepository
                .findByRequestor_Id(requestor.getId(), Sort.unsorted());

        assertEquals(1, foundRequests.size());
        assertEquals(request.getId(), foundRequests.get(0).getId());
        assertEquals(request.getDescription(), foundRequests.get(0).getDescription());
    }

    @Test
    void findByRequestor_IdNotTest() {
        List<ItemRequest> foundRequests = itemRequestRepository
                .findByRequestor_IdNot(100L, Sort.unsorted());

        assertEquals(1, foundRequests.size());
        assertEquals(request.getId(), foundRequests.get(0).getId());
        assertEquals(request.getDescription(), foundRequests.get(0).getDescription());

        List<ItemRequest> emptyRequests = itemRequestRepository
                .findByRequestor_IdNot(requestor.getId(), Sort.unsorted());

        assertEquals(0, emptyRequests.size());
    }
}
