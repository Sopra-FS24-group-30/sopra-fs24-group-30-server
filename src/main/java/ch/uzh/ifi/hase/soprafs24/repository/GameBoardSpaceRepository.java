package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.GameBoardSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoard;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Qualifier;
import java.util.Optional;
@Repository
@Qualifier("gameBoardSpaceRepository")
public interface GameBoardSpaceRepository extends JpaRepository<GameBoardSpace, Long> {
    Optional<GameBoardSpace> findBySpaceId(Long spaceId);
}
