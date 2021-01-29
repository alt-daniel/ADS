package nl.hva.ict.se.sands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ArcherTest {
    Archer archer;
    List<Archer> archers;
    int[] scores1, scores2;

    @BeforeEach
    private void setup() {
        archers = Archer.generateArchers(3);
        archer = new Archer("Jan", "de Boer");
        scores1 = new int[] {1, 5, 20};
        scores2 = new int[] {10, 9, 9};

    }

    @Test
    void archerIdsIncreaseCorrectly() {
        assertTrue(archers.get(1).getId() == archers.get(0).getId()+ 1);
        assertTrue(archers.get(2).getId() == archers.get(1).getId()+ 1);
    }

    @Test
    void visibilityOfConstructorsShouldBeUnchanged() {
        for (Constructor constructor : Archer.class.getDeclaredConstructors()) {
            assertTrue((constructor.getModifiers() & 0x00000004) != 0);
        }
    }

    @Test
    void idFieldShouldBeUnchangeable() throws NoSuchFieldException {
        assertTrue((Archer.class.getDeclaredField("id").getModifiers() & 0x00000010) != 0);
    }

    @Test
    void checkForCorrectTotalScore() {

        archer.registerScoreForRound(0, scores1);
        assertTrue(26 == archer.getTotalScore());
        archer.registerScoreForRound(1, scores1);
        assertTrue(52 == archer.getTotalScore());
    }

    @Test
    void countCorrectNinesAndTens() {
        assertTrue(0 == archer.getTens());

        archer.registerScoreForRound(2, scores2);
        assertTrue(1 == archer.getTens());
        assertTrue(2 == archer.getNines());
    }

}
