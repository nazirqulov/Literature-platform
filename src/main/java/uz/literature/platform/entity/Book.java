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

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "book_subcategories",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "subcategory_id")
    )
    private Set<SubCategory> subCategories = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors = new HashSet<>();

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

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private Set<Chapter> chapters = new HashSet<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private Set<UserBookProgress> userProgresses = new HashSet<>();

    public String getAuthorsAsString() {
        if (authors == null || authors.isEmpty()) {
            return "Noma'lum muallif";
        }
        return authors.stream()
                .map(Author::getName)
                .reduce((a, b) -> a + ", " + b)
                .orElse("Noma'lum muallif");
    }


    public void incrementViewCount() {
        this.viewCount = (this.viewCount == null ? 0 : this.viewCount) + 1;
    }


    public void incrementDownloadCount() {
        this.downloadCount = (this.downloadCount == null ? 0 : this.downloadCount) + 1;
    }

    public void updateRating(Double newRating) {
        if (this.averageRating == null || this.ratingCount == null) {
            this.averageRating = newRating;
            this.ratingCount = 1;
        } else {
            double totalRating = this.averageRating * this.ratingCount;
            this.ratingCount++;
            this.averageRating = (totalRating + newRating) / this.ratingCount;
        }
    }

}
