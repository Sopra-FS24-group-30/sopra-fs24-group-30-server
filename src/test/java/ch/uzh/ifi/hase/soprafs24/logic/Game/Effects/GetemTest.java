package ch.uzh.ifi.hase.soprafs24.logic.Game.Effects;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
public class GetemTest {

    @Test
    void testGetNoChoiceCards(){
        for(int i=0;i<100;i++){
            String item = Getem.getNoChoiceItem();
            assertFalse(item.contains("choice"));
        }
    }

}


