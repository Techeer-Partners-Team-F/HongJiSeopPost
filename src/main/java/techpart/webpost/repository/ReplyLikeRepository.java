package techpart.webpost.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import techpart.webpost.domain.ReplyLike;

@Repository
public interface ReplyLikeRepository extends JpaRepository<ReplyLike,Long> {

}
