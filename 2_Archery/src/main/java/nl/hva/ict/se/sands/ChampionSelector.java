package nl.hva.ict.se.sands;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Given a list of Archer's this class can be used to sort the list using one of three sorting algorithms.
 * Note that you are NOT allowed to change the signature of these methods! Adding method is perfectly fine.
 */
public class ChampionSelector {
    /**
     * This method uses either selection sort or insertion sort for sorting the archers.
     */
    public static List<Archer> selInsSort(List<Archer> archers, Comparator<Archer> scoringScheme) {
        for (int iteration = 0; iteration < archers.size() - 1; iteration++) {
            for (int position = iteration + 1; position < archers.size(); position++) {
                if (scoringScheme.compare(archers.get(iteration), archers.get(position)) > 0) {
                    Archer temp_archer = archers.get(iteration);
                    archers.set(iteration, archers.get(position));
                    archers.set(position, temp_archer);
                }
            }
        }
        return archers;
    }

    /**
     * This method uses quick sort for sorting the archers.
     */
    public static List<Archer> quickSort(List<Archer> archers, Comparator<Archer> scoringScheme) {

        quickSort(archers, 0, archers.size()-1, scoringScheme);

        return archers;
    }

    /**
     * This method uses the Java collections sort algorithm for sorting the archers.
     */
    public static List<Archer> collectionSort(List<Archer> archers, Comparator<Archer> scoringScheme) {
        archers.sort(scoringScheme);
        return archers;
    }

    public static int divide(List<Archer> archers, int low, int high, Comparator<Archer> scoringScheme) {
        Archer pivot = archers.get(high);
        int i = low - 1;
        for (int j = low; j <= high-1; j++) {
            if (scoringScheme.compare(archers.get(j), pivot) < 0) {
                i++;

                Archer temp = archers.get(i);
                archers.set(i, archers.get(j));
                archers.set(j, temp);
            }
        }
        Archer temp = archers.get(i+1);
        archers.set(i+1, archers.get(high));
        archers.set(high, temp);

        return i + 1;
    }

    public static void quickSort(List<Archer> archers, int low, int high, Comparator<Archer> scoringScheme) {
        if (low<high){
            int pi = divide(archers, low, high, scoringScheme);

            quickSort(archers, low, pi - 1, scoringScheme);
            quickSort(archers, pi+1, high, scoringScheme);
        }
    }

}
