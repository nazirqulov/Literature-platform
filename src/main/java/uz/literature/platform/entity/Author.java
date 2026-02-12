package uz.literature.platform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.literature.platform.entity.base.BaseLongEntity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by: Barkamol
 * DateTime: 2/9/2026 10:35 AM
 */
@Entity
@Table(name = "authors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Author extends BaseLongEntity {
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String biography;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "death_date")
    private LocalDate deathDate;

    private String nationality;

    @Column(name = "profile_image")
    private String profileImage;

    @ManyToMany(mappedBy = "authors")
    private Set<Book> books = new HashSet<>();
}
