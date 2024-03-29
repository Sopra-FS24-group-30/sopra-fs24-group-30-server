package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/*
To create a user
 */

public class UserPostDTO {

  private String name;

  private String username;

  private String password;

  private LocalDate creationDate;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword(){
      return password;
  }

  public void setPassword(String password) {
      this.password = password;
  }
  

  public LocalDate getCreationDate(){
      return creationDate;
  }

  public void setCreationDate(LocalDate creationDate) {
      this.creationDate = creationDate;
  }
}
