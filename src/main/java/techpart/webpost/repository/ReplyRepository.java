package techpart.webpost.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import techpart.webpost.domain.Post;
import techpart.webpost.domain.Reply;

@Repository
public interface ReplyRepository extends JpaRepository<Reply,Long> {

    List<Reply> findAllByPost(Post post);
}
