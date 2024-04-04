package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.GameBoardStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "GAMEBOARD")
public class GameBoard implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private LocalDate creationDate;

    @Column(nullable = false)
    private GameBoardStatus status;

    // Add the one-to-many relationship with GameBoardSpace
    @OneToMany(mappedBy = "gameBoard", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<GameBoardSpace> spaces; // Ensure GameBoardSpace class has a 'gameBoard' field with @ManyToOne annotation

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public GameBoardStatus getStatus() {
        return status;
    }

    public void setStatus(GameBoardStatus status) {
        this.status = status;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public Set<GameBoardSpace> getSpaces() {
        return spaces;
    }

    public void setSpaces(Set<GameBoardSpace> spaces) {
        this.spaces = spaces;
        // Set the gameBoard reference in each GameBoardSpace
        for (GameBoardSpace space : spaces) {
            space.setGameBoard(this);
        }
    }

    // Constructors, other getters, setters, and methods...
}
