package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwner_Id(long ownerId, Sort sort);

    @Query(" select i from Item i " +
            "where i.available IS TRUE " +
            "and (upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%')))")
    List<Item> search(String text);

    List<Item> findByRequest_IdIn(Set<Long> requestId);

    List<Item> findByRequest_Id(Long requestId);
}
