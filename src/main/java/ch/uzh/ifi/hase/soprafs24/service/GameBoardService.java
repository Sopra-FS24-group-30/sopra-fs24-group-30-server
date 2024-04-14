package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.rest.dto.GameBoardPostDTO;
import ch.uzh.ifi.hase.soprafs24.constant.GameBoardStatus;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoard;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoardSpace;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoardLoader;
import ch.uzh.ifi.hase.soprafs24.repository.GameBoardRepository;
import ch.uzh.ifi.hase.soprafs24.repository.GameBoardSpaceRepository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

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
        return spaces;
    }

    public GameBoard createGameBoard(GameBoardPostDTO gameBoardPostDTO) {
        GameBoard gameBoard = new GameBoard();
        gameBoard.setStatus(GameBoardStatus.ACTIVE);
        gameBoard.setSpaces(loadAndSaveGameBoardSpaces());
        return gameBoardRepository.saveAndFlush(gameBoard);
    }

    public GameBoard getGameBoard(Long id) {
        Optional<GameBoard> gameboard = gameBoardRepository.findById(id);
        return gameboard.orElse(null);
    }




}