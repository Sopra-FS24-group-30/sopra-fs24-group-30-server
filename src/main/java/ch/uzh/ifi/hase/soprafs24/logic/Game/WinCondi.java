package ch.uzh.ifi.hase.soprafs24.logic.Game;

/**
 * WinConditions and what they do:
 * - JackSparrow: You win if the other Team wins, and you lose if your Partner wins. If the game ends after 20 Turns, everyone except for your Partner loses.
 * - theMarooned: As long as you have exactly 0 Moneys, 0 Items and 0 Cards the Win Condition is fulfilled.
 * - goldenIsMy...: Land on seven golden spaces.
 * - drunk: Land on a tsunami Space thrice.
 */
public class WinCondi {
    private static final String[] winCondis = {"JackSparrow", "theMarooned", "goldenIsMy...", "drunk"};
}
