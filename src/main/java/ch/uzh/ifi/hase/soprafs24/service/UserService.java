package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
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

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */

@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    public User findUser(String username) {
        return userRepository.findByUsername(username);
    }

    public User createUser(User newUser) {
        newUser.setToken(UUID.randomUUID().toString());
        newUser.setStatus(UserStatus.OFFLINE);
        checkIfUserExists(newUser);
        newUser.setCreationDate(LocalDate.now());
        // saves the given entity but data is only persisted in the database once
        // flush() is called
        newUser = userRepository.save(newUser);
        userRepository.flush();

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    /**
     * This is a helper method that will check the uniqueness criteria of the
     * username and the name
     * defined in the User entity. The method will do nothing if the input is unique
     * and throw an error otherwise.
     *
     * @param userToBeCreated
     * @throws org.springframework.web.server.ResponseStatusException
     * @see User
     */
    private void checkIfUserExists(User userToBeCreated) {
        User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());
        User userByName = userRepository.findByName(userToBeCreated.getName());

        String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
        if (userByUsername != null && userByName != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format(baseErrorMessage, "username and the name", "are"));
        }
        else if (userByUsername != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "username", "is"));
        }
        else if (userByName != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "name", "is"));
        }
    }

    public User login(User loginUser) {
        User user = userRepository.findByUsername(loginUser.getUsername());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist!");
        }
        String savedPassword = user.getPassword();
        String givenPassword = loginUser.getPassword();
        if (!savedPassword.equals(givenPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is incorrect!");
        }
        userRepository.saveAndFlush(user);
        return user;
    }

    public User profile(Long userID) {
        Optional<User> user = userRepository.findById(userID);
        return user.orElse(null);
    }

    public User edit(User user, User updates) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User could not be found");
        }
        if (updates.getName() != null) {
            User exists = userRepository.findByName(updates.getName());
            if (exists != null && !exists.getId().equals(user.getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The name is already taken");
            }
            user.setName(updates.getName());
        }
        if (updates.getUsername() != null) {
            User exists = userRepository.findByUsername(updates.getUsername());
            if (exists != null && !exists.getId().equals(user.getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The name is already taken");
            }
            user.setUsername(updates.getUsername());
        }
        if (updates.getPassword() != null) {
            user.setPassword(updates.getPassword());
        }
        if (updates.getBirthday() != null) {
            user.setBirthday(updates.getBirthday());
        }
        userRepository.saveAndFlush(user);
        return user;
    }

    public void logout(User usernameToFind) {
        User user = userRepository.findByUsername(usernameToFind.getUsername());
        if (user != null) {
            user.setStatus(UserStatus.OFFLINE);
            userRepository.saveAndFlush(user);
        }
    }

    public void status(String username) {
        User user = findUser(username);
        if (user != null) {
            user.setStatus(UserStatus.ONLINE);
            userRepository.saveAndFlush(user);
        }
    }
}
