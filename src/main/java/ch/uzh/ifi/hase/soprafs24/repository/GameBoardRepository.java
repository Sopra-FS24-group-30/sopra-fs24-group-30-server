package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.GameBoardSpace;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Qualifier;
import java.util.Optional;

@Repository
@Qualifier("gameBoardRepository")
public interface GameBoardRepository extends JpaRepository<GameBoard, Long> {

}
