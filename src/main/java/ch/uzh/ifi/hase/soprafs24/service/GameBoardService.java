package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.rest.dto.GameBoardGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameBoardPostDTO;
import ch.uzh.ifi.hase.soprafs24.constant.GameBoardStatus;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoard;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoardSpace;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoardLoader;
import ch.uzh.ifi.hase.soprafs24.repository.GameBoardRepository;
import ch.uzh.ifi.hase.soprafs24.repository.GameBoardSpaceRepository;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ch.uzh.ifi.hase.soprafs24.entity.UserInformation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Set;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
@Service
@Transactional
public class GameBoardService {

    private final GameBoardRepository gameBoardRepository;
    private final GameBoardSpaceRepository gameBoardSpaceRepository;

    public GameBoardService(@Qualifier("gameBoardRepository") GameBoardRepository gameBoardRepository, @Qualifier("gameBoardSpaceRepository") GameBoardSpaceRepository gameBoardSpaceRepository) {
        this.gameBoardRepository = gameBoardRepository;
        this.gameBoardSpaceRepository = gameBoardSpaceRepository;
    }

    public List<GameBoard> getGameBoards() {
        return this.gameBoardRepository.findAll();
    }
    public List<GameBoardSpace> loadAndSaveGameBoardSpaces() {
        List<GameBoardSpace> spaces = GameBoardLoader.createGameBoardSpacesFromFile();
        // Assuming you want to save these spaces to your database
        for (GameBoardSpace space : spaces) {
            gameBoardSpaceRepository.save(space);
        }
        System.out.println("Spaces loaded and saved successfully");
        System.out.println("Spaces: " + spaces);

        return spaces; // Or return the saved entities if needed
    }

    public GameBoard createGameBoard(GameBoardPostDTO gameBoardPostDTO) {
        GameBoard gameBoard = new GameBoard();
        gameBoard.setId(gameBoardPostDTO.getId());// Assuming the ID can be set directly. If IDs are auto-generated, this might not be needed.
        gameBoard.setStatus(GameBoardStatus.ACTIVE); // Assuming the status is set to ACTIVE by default
        gameBoard.setSpaces(loadAndSaveGameBoardSpaces());

        return gameBoardRepository.saveAndFlush(gameBoard);
    }

    public GameBoard getGameBoard(Long id) {
        Optional<GameBoard> gameboard = gameBoardRepository.findById(id);
        return gameboard.orElse(null);
    }




}