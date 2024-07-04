package techpart.webpost.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import techpart.webpost.domain.Refresh;

@Repository
public interface RefreshRepository extends JpaRepository<Refresh,Long> {
    boolean existsByRefresh(String refresh);
    void deleteByRefresh(String refresh);
}
