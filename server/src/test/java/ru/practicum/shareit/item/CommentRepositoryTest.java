package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class CommentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CommentRepository commentRepository;

    private Item item;
    private User owner;
    private User author;
    private Comment comment;

    @BeforeEach
    void beforeEach() {
        owner = new User("owner", "owner@mail");
        item = new Item("Item name", "Item description", true, owner, null);
        author = new User("author", "author@mail");
        comment = new Comment("Comment", item, author, LocalDateTime.now());
        entityManager.persist(owner);
        entityManager.persist(item);
        entityManager.persist(author);
        entityManager.persist(comment);
        entityManager.flush();
    }

    @Test
    void findByItem_IdTest() {
        List<Comment> foundComments = commentRepository.findByItem_Id(item.getId());

        assertEquals(1, foundComments.size());
        assertEquals(comment.getId(), foundComments.get(0).getId());
        assertEquals(comment.getText(), foundComments.get(0).getText());
    }

}
