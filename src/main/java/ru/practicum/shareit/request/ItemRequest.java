package ru.practicum.shareit.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"items", "requester"})
@ToString(exclude = {"items", "requester"})
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String description;

    @ManyToOne
    @JoinColumn(name = "requester_id", referencedColumnName = "id")
    User requester;

    LocalDateTime created;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "request")
    List<Item> items = new ArrayList<>();
}
