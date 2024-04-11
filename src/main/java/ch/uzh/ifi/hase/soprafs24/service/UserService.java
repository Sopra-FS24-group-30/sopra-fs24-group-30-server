package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.AchievementStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
public class UserService {

    private final UserRepository UserRepository;


    @Autowired
    public UserService(@Qualifier("UserRepository") UserRepository UserRepository) {
        this.UserRepository = UserRepository;
    }

    /**
     * get the token of a given user to authenticate
     * @param username
     * @param password
     * @return the token of the user
     */
    public String getUserToken(String username, String password){
        Optional<User> foundUser = this.UserRepository.findByUsername(username);
        if (foundUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,String.format("the username %s does not exist",username));
        }
        User actualUser = foundUser.get();
        if (!password.equals(actualUser.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"the password is incorrect");
        }
        return actualUser.getToken();
    }

    /**
     * save a new user to the DB and return it
     * @param newUser user object with username and password already set
     * @return returns a user object with complete information
     */
    public User createUser(User newUser){

        //can be removed if frontend does not allow this
        if (newUser.getUsername() == null || newUser.getPassword() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"missing username or password");
        }
        else if (checkUsernameExists(newUser.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,String.format("the username %s already exists",newUser.getUsername()));
        }
        newUser.setCreationDate(LocalDate.now());
        newUser.setToken(UUID.randomUUID().toString());
        AchievementStatus ach = new AchievementStatus();
        newUser.setAchievement(ach);
        this.UserRepository.save(newUser);
        UserRepository.flush();
        return newUser;
    }

    /**
     * fetch the user with the given id
     * @param id give the id of the user you want to find
     * @return user of which the id was specified
     */
    public User findUserWithId(Long id){
        Optional<User> foundUser = this.UserRepository.findById(id);
        if(foundUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,String.format("the user with id %d does not exist",id));
        }
        return foundUser.get();
    }

    /**
     * create a new lobbyId and return it
     * @return a unique LobbyId
     */
    public String getLobbyId(){
        StringBuilder lobbyId = new StringBuilder();
        for(int i=0; i<6;i++){
            lobbyId.append(Integer.toString(ThreadLocalRandom.current().nextInt(0, 10)));
        }

        //TODO check against already active lobbies to avoid conflicts
        return lobbyId.toString();
    }
    /**
    *create a game and add the players to it so they can join the game and no one else
     * @return if successful returns true
     */
    public boolean createGame(String lobbyId, ArrayList<Long> playerIds){

        try{
            //TODO create the game with the lobbyID and the players
        } catch(Exception e){
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"the server could not start the game correctly");
        }
        return true;

        //
    }

    /**
     * start the game and let sockets take over
     */
    public void startGame(){
        //TODO trigger the start of game => websockets take over
    }


    private boolean checkUsernameExists(String username){
        Optional<User> existingUser = this.UserRepository.findByUsername(username);
        if(existingUser.isPresent()){
            return true;
        }
        return false;
    }


}
