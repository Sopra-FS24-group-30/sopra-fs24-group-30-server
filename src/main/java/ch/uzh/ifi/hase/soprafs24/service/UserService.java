package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.AchievementStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * get the token of a given user to authenticate
     * @param username
     * @param password
     * @return the token of the user
     */
    public String getUserToken(String username, String password){
        Optional<User> foundUser = this.userRepository.findByUsername(username);
        if (foundUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,String.format("the username %s does not exist",username));
        }
        User actualUser = foundUser.get();
        if (!password.equals(actualUser.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"the password is incorrect");
        }
        if(actualUser.getStatus().equals(UserStatus.ONLINE)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"the user is already online");
        }else{
            actualUser.setStatus(UserStatus.ONLINE);
        }

        return actualUser.getToken();
    }

    public User login(User loginUser) {
        User user = findUser(loginUser.getUsername());
        String savedPassword = user.getPassword();
        String givenPassword = loginUser.getPassword();
        if (!savedPassword.equals(givenPassword)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is incorrect!");
        }
        if (!user.getStatus().equals(UserStatus.OFFLINE)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already online!");
        }
        user.setStatus(UserStatus.ONLINE);
        userRepository.save(user);
        return user;
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
        newUser.setStatus(UserStatus.ONLINE);
        newUser.setAmountGamesCompleted(0);
        newUser.setAmountWins(0);
        this.userRepository.save(newUser);
        userRepository.flush();
        AchievementStatus ach = new AchievementStatus(newUser.getId());
        newUser.setAchievement(ach);
        return newUser;
    }

    /**
     * fetch the user with the given id
     * @param id give the id of the user you want to find
     * @return user of which the id was specified
     */
    public User findUserWithId(Long id){
        Optional<User> foundUser = this.userRepository.findById(id);
        if(foundUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,String.format("the user with id %d does not exist",id));
        }
        return foundUser.get();
    }

    public User findUser(String username){
        Optional<User> foundUser = this.userRepository.findByUsername(username);
        if (foundUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,String.format("the user with id %s does not exist",username));
        }
        return foundUser.get();
    }

    public void logout(Long userId) {
        User user = findUserWithId(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        user.setStatus(UserStatus.OFFLINE);
        userRepository.save(user);
    }

    /**
     * create a new lobbyId and return it
     * @return a unique LobbyId
     */
    public String getLobbyId(){
        StringBuilder lobbyId = new StringBuilder();
        for(int i=0; i<6;i++){
            lobbyId.append(Integer.toString(ThreadLocalRandom.current().nextInt(0, 10))); //NOSONAR
        }

        return lobbyId.toString();
    }
    /**
    *create a game and add the players to it, so they can join the game and no one else
     * @return if successful returns true
     */

    private boolean checkUsernameExists(String username){
        Optional<User> existingUser = this.userRepository.findByUsername(username);
        return existingUser.isPresent();
    }

    public void edit(UserPutDTO updates, Long gameId){
        User oldUser = userRepository.findById(gameId).get(); //NOSONAR
        if(updates.getPassword()!=null){
            oldUser.setPassword(updates.getPassword());
        }
        if(updates.getBirthday()!=null){
            oldUser.setBirthday(updates.getBirthday());
        }
        if(updates.getUsername() != null){
            if(userRepository.findByUsername(updates.getUsername()).isEmpty()){
                oldUser.setUsername(updates.getUsername());
            }else {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "the user with username " + updates.getUsername() + " does already exist");
            }
        }
        userRepository.save(oldUser);
        userRepository.flush();
    }
    public List<User> getUsers() {
        return this.userRepository.findAll();
    }
}
