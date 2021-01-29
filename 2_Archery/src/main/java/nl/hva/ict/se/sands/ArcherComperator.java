package nl.hva.ict.se.sands;

import java.util.Comparator;

public class ArcherComperator implements Comparator<Archer> {

    @Override
    public int compare(Archer a1, Archer a2) {
        if (a1.getTotalScore() - a2.getTotalScore() != 0) {
            return a2.getTotalScore() - a1.getTotalScore();
        } else if (a1.getTens() - a2.getTens() != 0){
            return a2.getTens() - a1.getTens();
        } else if (a1.getNines() - a2.getNines() != 0) {
            return a2.getNines() - a1.getNines();
        } else return a2.getId()-a1.getId();
    }
}
