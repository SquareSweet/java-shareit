package ru.practicum.shareit.item;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.persistence.*;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"owner", "request"})
@ToString(exclude = {"owner", "request"})
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String name;

    @Column(nullable = false)
    String description;

    @Column(name = "is_available", nullable = false)
    Boolean available;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    User owner;

    @ManyToOne
    @JoinColumn(name = "request_id", referencedColumnName = "id")
    ItemRequest request;
}
