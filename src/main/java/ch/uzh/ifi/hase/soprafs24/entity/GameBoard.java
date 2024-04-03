package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.constant.GameBoardStatus;

import javax.persistence.*;
import java.io.Serializable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;


@Entity
@Table(name = "GAMEBOARD")
public class GameBoard implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    // One-to-many relationship with Player
    //@OneToMany(mappedBy = "GameBoard", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    //private Set<User> players;

    @Column(nullable = false)
    private LocalDate creationDate;


    @Column(nullable = false)
    private GameBoardStatus status;

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


    //public Set<User> getPlayers(){
      //  return players;
    //}

    //public void setPlayers(Set<Player> players){
      //  this.players = players;
    //}
    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

}
