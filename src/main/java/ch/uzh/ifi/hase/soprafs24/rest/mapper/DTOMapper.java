package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoard;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameBoardGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameBoardPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

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
    User convertUserPostDTOtoEntity(UserPostDTO UserPostDTO);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "token", target = "token")
    UserPostDTO convertUserToUserPostDTO(User User);

    @Mapping(source = "id", target = "id")

    GameBoardGetDTO convertEntityToGameBoardGetDTO(GameBoard gameBoard);

    @Mapping(source = "id", target = "id")
    GameGetDTO convertEntityToGameGetDTO(Game game);

    @Mapping(source = "id", target = "id")
    Game convertGamePostDTOtoEntity(GamePostDTO gamePostDTO);

    @Mapping(source = "id", target = "id")
    GameBoard convertGameBoardPostDTOtoEntity(GameBoardPostDTO gameBoardPostDTO);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "token", target = "token")
    @Mapping(source = "creationDate", target = "creationDate")
    @Mapping(source = "achievement", target = "achievement")
    User UserGetDTOtoEntity(UserGetDTO userGetDTO);
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