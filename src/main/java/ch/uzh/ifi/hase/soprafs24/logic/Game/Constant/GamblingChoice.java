package ch.uzh.ifi.hase.soprafs24.logic.Game.Constant;//NOSONAR

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum GamblingChoice {
    ITEM,
    CARD,
    CASH;

    private static final List<GamblingChoice> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    public static GamblingChoice getGamblingChoice(){
        return VALUES.get(RANDOM.nextInt(SIZE));
    }
}
