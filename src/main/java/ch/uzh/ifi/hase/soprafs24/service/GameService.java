package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.constant.GameBoardStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoard;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GameService {

    private final GameRepository gameRepository;
    private final GameBoardService gameBoardService;

    public GameService(@Qualifier("gameRepository") GameRepository gameRepository, GameBoardService gameBoardService) {
        this.gameRepository = gameRepository;
        this.gameBoardService = gameBoardService;
    }

    public List<Game> getGames() {
        return this.gameRepository.findAll();
    }

    public Game createGame(GamePostDTO gamePostDTO) {
        Game game = new Game();
        GameBoard gameBoard = gameBoardService.createGameBoard();
        game.setGameBoard(gameBoard);
        game.setId(gamePostDTO.getId());
        gameBoard.setStatus(GameBoardStatus.ACTIVE);
        game.setStatus(GameStatus.PLAYING);
        gameBoard.setGame(game);
        return gameRepository.saveAndFlush(game);
    }

    public Game getGame(Long id) {
        Optional<Game> game = gameRepository.findById(id);
        return game.orElse(null);
    }
}