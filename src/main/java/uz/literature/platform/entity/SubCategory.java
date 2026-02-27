package uz.literature.platform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.literature.platform.entity.base.BaseLongEntity;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by: Barkamol
 * DateTime: 2/6/2026 12:02 PM
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(
        name = "subcategories",
        uniqueConstraints = @UniqueConstraint(columnNames = {"category_id", "name"})
)
@SQLDelete(sql = "UPDATE subcategories SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class SubCategory extends BaseLongEntity {

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToMany(mappedBy = "subCategories")
    private Set<Book> books = new HashSet<>();


}
