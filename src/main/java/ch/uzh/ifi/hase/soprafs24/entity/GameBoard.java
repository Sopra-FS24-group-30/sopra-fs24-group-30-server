package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.GameBoardStatus;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "GAMEBOARD")
public class GameBoard implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column(nullable = false)
    private GameBoardStatus status;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "gameBoard_id")
    // not transient, since it won't work.
    private List<GameBoardSpace> spaces;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GameBoardStatus getStatus() {
        return status;
    }

    public void setStatus(GameBoardStatus status) {
        this.status = status;
    }


    public List<GameBoardSpace> getSpaces() {
        return spaces;
    }

    public void setSpaces(List<GameBoardSpace> spaces) {
        this.spaces = spaces;
    }
}
