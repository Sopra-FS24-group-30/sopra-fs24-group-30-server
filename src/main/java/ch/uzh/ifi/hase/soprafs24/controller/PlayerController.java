package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.AchievementService;
import ch.uzh.ifi.hase.soprafs24.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
public class PlayerController {
    private final PlayerService playerService;
    private final AchievementService achievementService;

    PlayerController(PlayerService playerService, AchievementService achievementService) {
        this.playerService = playerService;
        this.achievementService = achievementService;
    }

    @PostMapping("/player")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody()
    public PlayerPostDTO createPlayer(@RequestBody PlayerPostDTO playerPostDTO){
        Player newPlayer = DTOMapper.INSTANCE.convertPlayerPostDTOtoEntity(playerPostDTO);
        Player generatedPlayer = this.playerService.createPlayer(newPlayer);
        this.achievementService.saveInitialAchievements(generatedPlayer);
        return DTOMapper.INSTANCE.convertPlayerToPlayerPostDTO(generatedPlayer);
    }

}
