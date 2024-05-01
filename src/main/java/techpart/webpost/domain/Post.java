package techpart.webpost.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post")
@Builder
@DynamicUpdate
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "view_cnt", nullable = false)
    private int viewCnt;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @PastOrPresent
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PastOrPresent
    @Column(name = "modified_at", nullable = true)
    private LocalDateTime modifiedAt;

    @Min(value = 0)
    @ColumnDefault("0")
    @Column(name = "like_cnt",nullable = false)
    private int likeCnt;

    public void increaseLike(){
        likeCnt++;
    }

    public void decreaseLike(){
        likeCnt--;
    }
}
