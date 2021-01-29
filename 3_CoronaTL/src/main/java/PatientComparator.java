import java.util.Comparator;

public class PatientComparator implements Comparator<Patient> {

    @Override
    public int compare(Patient p1, Patient p2) {
        if (p1.isHasPriority() && p2.isHasPriority() || !p1.isHasPriority() && !p2.isHasPriority()) {
            return p1.getArrivedAt().compareTo(p2.getArrivedAt());
        } else if (p1.isHasPriority() && !p2.isHasPriority()) {
            return -1;
        } else return 1;
    }

}
