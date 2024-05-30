package techpart.webpost.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import techpart.webpost.domain.Bookmark;
import techpart.webpost.domain.Post;
import techpart.webpost.domain.User;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark,Long> {
    Optional<Bookmark> findByUserAndPost(User user, Post post);
}
