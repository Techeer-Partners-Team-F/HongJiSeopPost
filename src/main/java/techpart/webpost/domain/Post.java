package techpart.webpost.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
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
import techpart.webpost.dto.request.PostDto;

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

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @PastOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PastOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "modified_at", nullable = true)
    private LocalDateTime modifiedAt;

    @Min(value = 0)
    @ColumnDefault("0")
    @Column(name = "like_cnt",nullable = false)
    private int likeCnt;

    @Column(name = "view_cnt", nullable = false)
    private int viewCnt;

    public Post(User user, String title, String content) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = null;
        this.viewCnt = 0;
        this.likeCnt = 0;
    }

    public void increaseViewCnt(){
        this.viewCnt++;
    }

    public void increaseLike(){
        this.likeCnt++;
    }

    public void decreaseLike(){
        this.likeCnt--;
    }

    //techpart/webpost/service/PostServiceTest
    //null값일 경우 update를 하지 않는다.
    public void update(PostDto postDto){
        this.content = postDto.getContent();
        this.title = postDto.getTitle();
        this.modifiedAt = LocalDateTime.now();
    }
}
