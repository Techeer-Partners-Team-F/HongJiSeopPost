package techpart.webpost.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import techpart.webpost.domain.PostLike;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike,Long> {

}
