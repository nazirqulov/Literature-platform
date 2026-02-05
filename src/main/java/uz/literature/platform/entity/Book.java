package uz.literature.platform.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import uz.literature.platform.entity.base.BaseLongEntity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book extends BaseLongEntity {
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;

    
    @ManyToMany
    @JoinTable(
        name = "book_categories",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories;
    
    private String isbn;
    
    @Column(name = "published_year")
    private Integer publishedYear;
    
    private String publisher;
    
    private String language;
    
    @Column(name = "page_count")
    private Integer pageCount;
    
    @Column(name = "cover_image")
    private String coverImage;
    
    @Column(name = "pdf_file")
    private String pdfFile;
    
    @Column(name = "audio_file")
    private String audioFile;
    
    @Column(name = "view_count")
    private Integer viewCount = 0;
    
    @Column(name = "download_count")
    private Integer downloadCount = 0;
    
    @Column(name = "average_rating")
    private Double averageRating = 0.0;
    
    @Column(name = "rating_count")
    private Integer ratingCount = 0;
    
    @Column(name = "is_featured")
    private Boolean isFeatured = false;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private Set<Review> reviews = new HashSet<>();
    
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private Set<Favorite> favorites = new HashSet<>();

}
