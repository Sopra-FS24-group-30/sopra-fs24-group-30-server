package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
public class UserPutDTO {
    private String name;
    private String username;
    private String password;
    private LocalDate birthday;
    private UserStatus status;

    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public String getUsername(){
        return username;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public String getPassword(){
        return password;
    }
    public void setBirthday(LocalDate birthday){
      this.birthday = birthday;
    }
    public LocalDate getBirthday(){
      return birthday;
    }

    public UserStatus getStatus(){
        return status;
    }
    public void setStatus(UserStatus status){
        this.status = status;
    }

}
