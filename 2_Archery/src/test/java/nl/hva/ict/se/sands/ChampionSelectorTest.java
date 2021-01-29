package nl.hva.ict.se.sands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChampionSelectorTest {
    protected Comparator<Archer> comparator;
    int ARCHER_NUM = 25;
    List<Archer> archerList;

    @BeforeEach
    public void createComparator() {
        comparator = new ArcherComperator();
        archerList = Archer.generateArchers(ARCHER_NUM);
    }

    @Test
    public void selInsSortAndCollectionSortResultInSameOrder() {
        List<Archer> unsortedArchersForSelIns = new ArrayList<>(archerList);
        List<Archer> unsortedArchersForCollection = new ArrayList<>(archerList);

        List<Archer> sortedArchersSelIns = ChampionSelector.selInsSort(unsortedArchersForSelIns, comparator);
        List<Archer> sortedArchersCollection = ChampionSelector.collectionSort(unsortedArchersForCollection, comparator);
        assertEquals(sortedArchersCollection, sortedArchersSelIns);
    }

    @Test
    public void quickSortAndCollectionSortResultInSameOrder() {
        List<Archer> unsortedArchersForQuickSort = new ArrayList<>(archerList);
        List<Archer> unsortedArchersForCollection = new ArrayList<>(archerList);

        List<Archer> sortedArchersQuickSort = ChampionSelector.quickSort(unsortedArchersForQuickSort, comparator);
        List<Archer> sortedArchersCollection = ChampionSelector.collectionSort(unsortedArchersForCollection, comparator);
        assertEquals(sortedArchersCollection, sortedArchersQuickSort);
    }

}
