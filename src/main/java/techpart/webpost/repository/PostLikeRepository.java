package techpart.webpost.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import techpart.webpost.domain.Post;
import techpart.webpost.domain.PostLike;
import techpart.webpost.domain.User;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike,Long> {


    Optional<PostLike> findByUserAndPost(User user, Post post);
}
