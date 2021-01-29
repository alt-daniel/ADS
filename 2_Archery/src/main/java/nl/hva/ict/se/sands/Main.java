package nl.hva.ict.se.sands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Comparator<Archer> comparator =  new ArcherComperator();
        int nStartingArchers = 100;
        final int MAXIMUM_ARCHERS = 5000000;
        final long MAX_MILISECONDS = 20000;
        long remainingSeconds = MAX_MILISECONDS;


        while(remainingSeconds > 0 && nStartingArchers < MAXIMUM_ARCHERS) {
            List<Archer> unsortedArchersForSelIns = Archer.generateArchers(nStartingArchers);
            List<Archer> unsortedArchersForQuickSort = new ArrayList<Archer>(unsortedArchersForSelIns);
            List<Archer> unsortedArcherForMergeSort = new ArrayList<Archer>(unsortedArchersForSelIns);

            long startSelIns = System.currentTimeMillis();
            List<Archer> sortedArchersSelIns = ChampionSelector.selInsSort(unsortedArchersForSelIns, comparator);
            long endSelIns = System.currentTimeMillis();
            long measuredTimeSelIns = endSelIns-startSelIns;

            long startQuickSort = System.currentTimeMillis();
            List<Archer> sortedArchersQuickSort = ChampionSelector.quickSort(unsortedArchersForQuickSort, comparator);
            long endQuickSort= System.currentTimeMillis();
            long measuredTimeQuickSort = endQuickSort-startQuickSort;

            long startMergeSort = System.currentTimeMillis();
            Collections.sort(unsortedArcherForMergeSort, comparator);
            long endMergeSort = System.currentTimeMillis();
            long measuredTimeMergeSort = endMergeSort-startMergeSort;

            System.out.println("Generated Archers: " + nStartingArchers);
            remainingSeconds = remainingSeconds - measuredTimeSelIns;
            nStartingArchers = nStartingArchers * 2;

            System.out.println("Measured miliseconds Selection Sort: " + measuredTimeSelIns);
            System.out.println("Measured miliseconds Quick Sort: " + measuredTimeQuickSort);
            System.out.println("Measured miliseconds MergeSort: " + measuredTimeMergeSort);

            System.out.println("Remaining miliseconds (Based on Selection Sort): " + remainingSeconds + "...\n");
        }

    }
}
