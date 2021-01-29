import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;

public class Patient{

    public enum Symptom {
        DRY_COUGH,
        TIREDNESS,
        FEVER,
        SHORT_OF_BREATH,
        CHEST_PAIN,
        SORE_MUSCLES,
        HEADACHE,
        LOSS_OF_TASTE,
        DIARRHOEA
    }
    private final String zipCode;
    private final LocalDate dateOfBirth;
    private final boolean[] symptoms;   // indicates for every symptom whether the patient is suffering from it
    private final boolean hasPriority;  // indicates the patient has a key profession which should be granted priority

    private final LocalTime arrivedAt;  // the arrival time of the patient at the test lane
                                        // this is also the time of queueing for being sampled
    private LocalTime sampledAt;        // the time when the nurse starts sampling the patient
    private Nurse sampledBy;            // the nurse who has sampled the patient

    /**
     * Creates a patient record with semi-random data
     * @param earliestArrival   start of the time-interval in which the randomized arrival of the patient shall occur
     * @param latestArrival     end of the time-interval in which the randomized arrival of the patient shall occur
     * @param priorityFraction  fraction of the patients that shall be given priority of sampling
     * @param randomizer        used to generate reproducible semi-random data
     */
    public Patient(LocalTime earliestArrival, LocalTime latestArrival, double priorityFraction, Random randomizer) {
        this.zipCode = createRandomZipcode(randomizer);

        // some date of birth that fits an age range between 15 and 95
        this.dateOfBirth =
                LocalDate.of(1925 + randomizer.nextInt(81),
                        1 + randomizer.nextInt(12),
                        1 + randomizer.nextInt(28));

        // grant a semi-random mix of covid-19 symptoms
        this.symptoms = new boolean[Symptom.values().length];
        this.symptoms[Symptom.DRY_COUGH.ordinal()] = randomizer.nextDouble() < 0.80;
        this.symptoms[Symptom.TIREDNESS.ordinal()] = randomizer.nextDouble() < 0.65;
        this.symptoms[Symptom.FEVER.ordinal()] = randomizer.nextDouble() < 0.60;
        this.symptoms[Symptom.SHORT_OF_BREATH.ordinal()] = randomizer.nextDouble() < 0.45;
        this.symptoms[Symptom.CHEST_PAIN.ordinal()] = randomizer.nextDouble() < 0.15;
        this.symptoms[Symptom.SORE_MUSCLES.ordinal()] = randomizer.nextDouble() < 0.2;
        this.symptoms[Symptom.HEADACHE.ordinal()] = randomizer.nextDouble() < 0.25;
        this.symptoms[Symptom.LOSS_OF_TASTE.ordinal()] = randomizer.nextDouble() < 0.25;
        this.symptoms[Symptom.DIARRHOEA.ordinal()] = randomizer.nextDouble() < 0.15;

        // grant priority with probability cf. the priorityFraction
        this.hasPriority = randomizer.nextDouble() < priorityFraction;

        // estimate a semi-random arrival time at the test lane, within the interval, but front-loaded.
        int arrivalWindow = latestArrival.toSecondOfDay() - earliestArrival.toSecondOfDay();
        this.arrivedAt = earliestArrival.plusSeconds(
                (1 + randomizer.nextInt(3))*randomizer.nextInt(arrivalWindow)/3
        );

        // the actual time of the sampling will be known when the nurse is handling this patient
        this.sampledAt = null;
    }

    /**
     * Create some random zipCode within the Amsterdam Area
     * @param randomizer    used to generate reproducible semi-random data
     * @return              the generated zipcode
     */
    private static String createRandomZipcode(Random randomizer) {
        return String.format("%d%c%c",
                1000 + (7 * randomizer.nextInt(31)) % 22,
                'A' + 7 * randomizer.nextInt(4),
                'B' + 11 * randomizer.nextInt(3)
                );
    }

    public LocalTime getSampledAt() {
        return sampledAt;
    }

    public void setSampledAt(LocalTime sampledAt) {
        this.sampledAt = sampledAt;
    }

    public LocalTime getArrivedAt() {
        return arrivedAt;
    }

    public boolean[] getSymptoms() {
        return symptoms;
    }

    public String getZipCode() {
        return zipCode;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public boolean isHasPriority() {
        return hasPriority;
    }


    public void setSampledBy(Nurse sampledBy) {
        this.sampledBy = sampledBy;
    }

    public StringBuilder getPositiveSymptoms(){
        StringBuilder sb = new StringBuilder();

        for (int i=0; i < getSymptoms().length; i++) {
            if (getSymptoms()[i]) {

                sb.append(Symptom.values()[i]);
                sb.append(",");
            }
        }

        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }

        return sb;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(zipCode);
        sb.append("(");
        sb.append(getDateOfBirth());
        sb.append(")@");
        sb.append(getArrivedAt());
        sb.append("[");
        sb.append(getPositiveSymptoms());
        sb.append("]");

        return sb.toString();
    }

    public int getWaitedTimeinSeconds() {
        return (int) (Duration.between(this.getArrivedAt(), this.getSampledAt()).toSeconds());
    }

    public String getZipArea(){
        return this.getZipCode().substring(0, 4);
    }

}
