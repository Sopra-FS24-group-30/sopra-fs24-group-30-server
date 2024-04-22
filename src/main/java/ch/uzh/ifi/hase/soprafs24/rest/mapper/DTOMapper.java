package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoard;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.logic.Game.Player;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameBoardGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameBoardPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameJoinRequest;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;
import java.util.Map;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper
public interface DTOMapper {

    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "id", target = "id")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "token", target = "token")
    UserPostDTO convertUserToUserPostDTO(User user);

    @Mapping(source = "id", target = "id")
    GameBoardGetDTO convertEntityToGameBoardGetDTO(GameBoard gameBoard);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "gameBoard", target = "gameBoard")
    @Mapping(source = "active_Players", target = "active_players")
    GameGetDTO convertEntityToGameGetDTO(Game game);

    @Mapping(source = "status", target = "status")
    @Mapping(source = "active_Players", target = "active_players")
    Game convertGamePutDTOtoGame(GamePutDTO gamePutDTO);

    @Mapping(source = "status", target = "status")
    @Mapping(source = "active_players", target = "active_players")
    Game convertGameGetDTOtoGame(GameGetDTO gameGetDTO);
    @Mapping(source = "id", target = "id")
    Game convertGamePostDTOtoEntity(GamePostDTO gamePostDTO);

    @Mapping(source = "id", target = "id")
    GameBoard convertGameBoardPostDTOtoEntity(GameBoardPostDTO gameBoardPostDTO);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "token", target = "token")
    @Mapping(source = "creationDate", target = "creationDate")
    @Mapping(source = "achievement", target = "achievement")
    User userGetDTOtoEntity(UserGetDTO userGetDTO);
    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "token", target = "token")
    @Mapping(source = "creationDate", target = "creationDate")
    @Mapping(source = "achievement", target = "achievement")
    UserGetDTO convertUserToUserGetDTO(User user);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "birthday", target = "birthday")
    @Mapping(source = "password", target = "password")
    User convertUserPutDTOtoUser(UserPutDTO userPutDTO);

}