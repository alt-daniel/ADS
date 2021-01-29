package nl.hva.ict.se.sands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ComparatorTest {
    // This is the place for unit tests for your comparator.

    List<Archer> archers, archersGenerated;
    Archer a1, a2, a3, a4;
    int[] scoreSheet1 = {10, 10, 9};
    int[] scoreSheet2 = {10, 10, 8};
    int[] scoreSheet3 = {10, 7, 2};
    int[] scoreSheet4 = {10, 4, 5};


    @BeforeEach
    private void setup() {
        archers = new ArrayList<Archer>();
        archersGenerated = Archer.generateArchers(10);
        a1 = new Archer("Jan", "de Boer");
        a2 = new Archer("Piet", "Bakker");
        a3 = new Archer("Klaas", "van de Berg");
        a4 = new Archer("Nico", "Bakker");

        a1.registerScoreForRound(0, scoreSheet1);
        a2.registerScoreForRound(0, scoreSheet2);
        a3.registerScoreForRound(0, scoreSheet3);
        a4.registerScoreForRound(0, scoreSheet4);

        archers.add(a1);
        archers.add(a2);
        archers.add(a3);
        archers.add(a4);

        archers.sort(new ArcherComperator());
    }

    @Test
    void IfgetTenAreEqualuseGetNine() {
        assertEquals(archers.get(0), a1);
        assertTrue(archersGenerated.get(0).getTotalScore() >= archersGenerated.get(1).getTotalScore());
    }

    @Test
    void higherTotalScoreSortedFirst() {
        assertEquals(archers.get(1), a2);
    }

    @Test
    void lessExperiencedFirst() {
        assertEquals(archers.get(2), a4);
    }

}
