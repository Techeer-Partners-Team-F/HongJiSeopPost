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
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reply")
@Builder
@DynamicUpdate
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "content",nullable = false)
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
