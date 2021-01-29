import java.time.Duration;
import java.time.LocalTime;
import java.util.Random;

public class Nurse implements Comparable<Nurse> {
    private final static int SAMPLE_TIME_MIN = 60;
    private final static int SAMPLE_TIME_MAX = 160;

    private final String name;
    private LocalTime availableAt;      // the earliest time when this nurse will be available (after finishing a current patient)
    private int numPatientsSampled;     // the total number of patients this nurse has sampled today.
    private int totalSamplingTime;      // the total time spend (in seconds) by this nurse in sampling patients

    private final Random randomizer;

    /**
     * Creates a new Nurse record for simulating and tracking sampling of patients at the Test Lane
     *
     * @param name       some arbitrary name for reporting
     * @param startTime  The start time of the work day; the earliest time available for the next patient
     * @param randomizer used to generate reproducible simulation results
     */
    public Nurse(String name, LocalTime startTime, Random randomizer) {
        this.name = name;
        this.availableAt = startTime;
        this.numPatientsSampled = 0;
        this.totalSamplingTime = 0;
        this.randomizer = randomizer;
    }

    /**
     * Handle the sampling of the given patient
     * register all related simulation results as required later for reporting
     *
     * @param patient   the patient to be sampled
     * @param startTime the time at the start of the sampling of the patient
     */
    public void samplePatient(Patient patient, LocalTime startTime) {
        // determine the time needed to complete the sampling procedure
        // this time varies between 60 and 159 seconds as is advised from GGD experience.
        int sampleDuration = SAMPLE_TIME_MIN + randomizer.nextInt(SAMPLE_TIME_MAX - SAMPLE_TIME_MIN);

        //patient info
        patient.setSampledBy(this);
        patient.setSampledAt(startTime);

        // update nurse info
        this.setNumPatientsSampled(getNumPatientsSampled() + 1);
        this.setTotalSamplingTime(getTotalSamplingTime() + sampleDuration);
        //  calculate and set the new availableAt time of the nurse
        this.setAvailableAt(startTime.plusSeconds(sampleDuration));
    }

    public LocalTime getAvailableAt() {
        return availableAt;
    }

    public void setAvailableAt(LocalTime availableAt) {
        this.availableAt = availableAt;
    }

    public int getNumPatientsSampled() {
        return numPatientsSampled;
    }

    public String getName() {
        return name;
    }

    public int getTotalSamplingTime() {
        return totalSamplingTime;
    }

    public void setNumPatientsSampled(int numPatientsSampled) {
        this.numPatientsSampled = numPatientsSampled;
    }

    public void setTotalSamplingTime(int totalSamplingTime) {
        this.totalSamplingTime = totalSamplingTime;
    }

    public double getAverageSamplingTime() {
        return (double) (getTotalSamplingTime()) / getNumPatientsSampled();
    }

    // Get workload in seconds: fraction of sampling time to seconds open
    public int getWorkload(LocalTime startTime, LocalTime endTime) {
        double secondsOpen = (double) Duration.between(startTime, endTime).toSeconds();
        double converted = (double) (this.getTotalSamplingTime()) / secondsOpen * 100;

        return (int) converted;
    }


    @Override
    public int compareTo(Nurse n2) {
        return this.getAvailableAt().compareTo(n2.getAvailableAt());
    }

}
