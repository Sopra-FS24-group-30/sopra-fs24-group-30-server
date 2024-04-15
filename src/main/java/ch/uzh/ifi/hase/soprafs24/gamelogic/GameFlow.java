package ch.uzh.ifi.hase.soprafs24.gamelogic;

import ch.uzh.ifi.hase.soprafs24.entity.GameBoard;
import ch.uzh.ifi.hase.soprafs24.service.GameBoardService;

import java.util.Random;

public class GameFlow {

    public int throwDice(){
        Random random = new Random();
        int randomNumber = random.nextInt(6) + 1;
        return randomNumber;
    }

    public void movePlayer(){
        int diceThrow = throwDice();
        while (diceThrow > 0){


        }
    }
}
