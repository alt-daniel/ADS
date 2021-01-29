import java.time.LocalTime;
import java.util.*;

public class CoronaTestLane {

    private List<Patient> patients;     // all patients visiting the test lane today
    private List<Nurse> nurses;         // all nurses working at the test lane today
    private LocalTime openingTime;      // start time of sampling at the test lane today
    private LocalTime closingTime;      // latest time of passible arrivals of patients
    // hereafter, nurses will continue work until the queue is empty

    // simulation statistics for reporting
    private int maxQueueLength;             // the maximum queue length of waiting patients at any time today
    private int maxRegularWaitTime;         // the maximum wait time of regular patients today
    private int maxPriorityWaitTime;        // the maximum wait time of priority patients today
    private double averageRegularWaitTime;  // the average wait time of regular patients today
    private double averagePriorityWaitTime; // the average wait time of priority patients today
    private LocalTime workFinished;         // the time when all nurses have finished work with no more waiting patients

    private int totalRegularWaitTime;
    private int totalPriorityWaitTime;
    private int priorityPatients;
    private int regularPatients;

    private Random randomizer;              // used for generation of test data and to produce reproducible simulation results

    /**
     * Instantiates a corona test line for a given day of work
     *
     * @param openingTime start time of sampling at the test lane today
     * @param closingTime latest time of passible arrivals of patientss
     */
    public CoronaTestLane(LocalTime openingTime, LocalTime closingTime) {
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.workFinished = openingTime;
        this.randomizer = new Random(0);
        System.out.printf("\nCorona test lane simulation between %s and %s\n\n", openingTime, closingTime);
    }

    /**
     * Simulate a day at the Test Lane
     *
     * @param numNurses        the number of nurses that shall be scheduled to work in parallel
     * @param numPatients      the number of patient profiles that shall be generated to visit the Test Lane today
     * @param priorityFraction the fraction of patients that shall be given priority
     *                         and will be allowed to skip non-priority patients on the waiting queue
     * @param seed             used to initialize a randomizer to generate reproducible semi-random data
     */
    public void configure(int numNurses, int numPatients, double priorityFraction, long seed) {
        randomizer = new Random(seed);
        System.out.printf("Configuring test lane with %d nurse(s) and %d patients (%.0f%% priority); seed=%d.\n",
                numNurses, numPatients, 100 * priorityFraction, seed);

        // Configure the nurses
        nurses = new ArrayList<>();
        for (int n = 0; n < numNurses; n++) {
            nurses.add(new Nurse("Nurse-" + (n + 1), openingTime, randomizer));
        }

        // Generate the full list of patients that will be arriving at the test lane (and show a few)
        patients = new ArrayList<>();
        for (int p = 0; p < numPatients; p++) {
            patients.add(new Patient(openingTime, closingTime, priorityFraction, randomizer));
        }

        // echo some patients for runtime confirmation
        if (patients.size() > 2) {
            System.out.printf("   a few patients: %s - %s - %s - ...\n", patients.get(0), patients.get(1), patients.get(2));
        }
    }

    /**
     * Simulate a day at the Test Lane and calculate the relevant statistics from this simulation
     */
    public void simulate() {

        System.out.printf("Simulating the sampling of %d patients by %d nurse(s).\n",
                patients.size(), nurses.size());

        // interleaved by nurses inviting patients from the waiting queue to have their sample taken from their nose...

        // maintain the patients queue by priority and arrival time
        Queue<Patient> waitingPatients = new PriorityQueue<>(500, new PatientComparator());

        // reset availability of the nurses
        for (Nurse nurse : nurses) {
            nurse.setAvailableAt(openingTime);
            nurse.setNumPatientsSampled(0);
            nurse.setTotalSamplingTime(0);
        }

        // Reset helper variables for patients and the wait times
        this.setTotalPriorityWaitTime(0);
        this.setTotalRegularWaitTime(0);
        this.setRegularPatients(0);
        this.setPriorityPatients(0);
        this.setMaxRegularWaitTime(0);
        this.setMaxPriorityWaitTime(0);

        // maintain a queue of nurses ordered by earliest time of availability
        Queue<Nurse> availableNurses = new PriorityQueue<>();
        nurses.sort(Comparator.comparing(Nurse::getAvailableAt));
        availableNurses.addAll(nurses);

        // ensure patients are processed in order of arrival
        patients.sort(Comparator.comparing(Patient::getArrivedAt));

        // track the max queuelength as part of the simulation
        maxQueueLength = 0;

        // determine the first available nurse
        Nurse nextAvailableNurse = availableNurses.poll();

        // process all patients in order of arrival at the Test Lane
        for (Patient patient : patients) {
            // let nurses handle patients on the queue, if any
            // until the time of the next available nurse is later than the patient who just arrived
            while (waitingPatients.size() > 0 && nextAvailableNurse.getAvailableAt().compareTo(patient.getArrivedAt()) <= 0) {
                // handle the next patient from the queue
                Patient nextPatient = waitingPatients.poll();

                LocalTime startTime = nextAvailableNurse.getAvailableAt().isAfter(nextPatient.getArrivedAt()) ?
                        nextAvailableNurse.getAvailableAt() :
                        nextPatient.getArrivedAt();
                nextAvailableNurse.samplePatient(nextPatient, startTime);

                // reorder the current nurse into the queue of nurses as per her next availability
                // (after completing the current patient)
                availableNurses.add(nextAvailableNurse);

                // get the next available nurse for handling of the next patient
                nextAvailableNurse = availableNurses.poll();
            }

            // add the patient that just arrived to the queue before letting the nurses proceed
            waitingPatients.add(patient);

            // keep track of the maximum queue length
            maxQueueLength = Integer.max(maxQueueLength, waitingPatients.size());
        }

        // process the remaining patients on the queue, same as above
        while (waitingPatients.size() > 0) {
            Patient nextPatient = waitingPatients.poll();
            LocalTime startTime = nextAvailableNurse.getAvailableAt().isAfter(nextPatient.getArrivedAt()) ?
                    nextAvailableNurse.getAvailableAt() :
                    nextPatient.getArrivedAt();
            nextAvailableNurse.samplePatient(nextPatient, startTime);
            availableNurses.add(nextAvailableNurse);
            nextAvailableNurse = availableNurses.poll();
        }

        // all patients are underway
        for (Patient patient : patients) {

            if (!patient.isHasPriority()) {
                regularPatients++;
                totalRegularWaitTime += patient.getWaitedTimeinSeconds();

                if (patient.getWaitedTimeinSeconds() > maxRegularWaitTime)
                    maxRegularWaitTime = patient.getWaitedTimeinSeconds();

            } else {
                priorityPatients++;
                totalPriorityWaitTime += patient.getWaitedTimeinSeconds();

                if (patient.getWaitedTimeinSeconds() > maxPriorityWaitTime) {
                    maxPriorityWaitTime = patient.getWaitedTimeinSeconds();
                }
            }
        }

        LocalTime latestFinished = openingTime;
        for (Nurse nurse : nurses) {
            if (nurse.getAvailableAt().isAfter(latestFinished)) {
                latestFinished = nurse.getAvailableAt();
            }
        }
        setWorkFinished(latestFinished);
        for (Nurse nurse : nurses) {
            nurse.setAvailableAt(workFinished);
        }

        averagePriorityWaitTime = ((double) totalPriorityWaitTime) / priorityPatients;
        averageRegularWaitTime = ((double) totalRegularWaitTime) / regularPatients;


    }

    /**
     * Report the statistics of the simulation
     */
    public void printSimulationResults() {
        System.out.println("Simulation results per nurse:");
        System.out.println("  Name: #Patients:    Avg. sample time: Workload:");

        for (Nurse nurse : nurses) {
            System.out.printf("   %s     %s        %.2f                %s%%\n",
                    nurse.getName(),
                    nurse.getNumPatientsSampled(),
                    nurse.getAverageSamplingTime(),
                    nurse.getWorkload(openingTime, closingTime));

        }

        System.out.printf("Work finished at %s\n", getWorkFinished());
        System.out.printf("Maximum patient queue length %s\n", getMaxQueueLength());
        System.out.print("Wait times:        Average:  Maximum:\n");
        System.out.printf("   Regular patients    %.2f       %s\n", getAverageRegularWaitTime(), getMaxRegularWaitTime());
        if (this.priorityPatients > 0) {
            System.out.printf("   Priority patients   %.2f       %s\n", getAveragePriorityWaitTime(), getMaxPriorityWaitTime());
        }
        System.out.println("\n\n");
    }

    /**
     * Report the statistics of the patients
     */
    public void printPatientStatistics() {

        System.out.println("\nPatient counts by zip area:");
        Map<String, Integer> patientCounts = patientsByZipArea();
        System.out.println(patientsByZipArea());

        System.out.println("\nZip area with highest patient percentage per complaint:");
        Map<Patient.Symptom, String> zipAreasPerSymptom =
                zipAreasWithHighestPatientPercentageBySymptom(patientCounts);
        System.out.println(zipAreasPerSymptom);
    }

    /**
     * Calculate the number of patients per zip-area code (i.e. the digits of a zipcode)
     *
     * @return a map of patient counts per zip-area code
     */
    public Map<String, Integer> patientsByZipArea() {

        Map<String, Integer> map = new TreeMap<>();
        for (Patient patient : patients) {
            int count = map.getOrDefault(patient.getZipArea(), 0);
            map.put(patient.getZipArea(), count + 1);
        }

        return map;
    }

    public Map<Patient.Symptom, String> zipAreasWithHighestPatientPercentageBySymptom(Map<String, Integer> patientsByZipArea) {

        // TODO create, populate and return the result map
//        Map<Patient.Symptom, Map<String, Integer>> map = new TreeMap<>();
//        for (Patient.Symptom symptom : Patient.Symptom.values()) {
//            for (Patient patient: patients) {
//                int count = map.keySet(symptom, patientsByZipArea);
//                map.put(symptom, patient.getZipArea(), count)
//            }
//        }
//
//
        return null;
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public List<Nurse> getNurses() {
        return nurses;
    }

    public int getMaxQueueLength() {
        return maxQueueLength;
    }

    public int getMaxRegularWaitTime() {
        return maxRegularWaitTime;
    }

    public int getMaxPriorityWaitTime() {
        return maxPriorityWaitTime;
    }

    public double getAverageRegularWaitTime() {
        return averageRegularWaitTime;
    }

    public double getAveragePriorityWaitTime() {
        return averagePriorityWaitTime;
    }

    public LocalTime getWorkFinished() {
        return workFinished;
    }

    public void setWorkFinished(LocalTime workFinished) {
        this.workFinished = workFinished;
    }

    public int getTotalRegularWaitTime() {
        return totalRegularWaitTime;
    }

    public void setTotalRegularWaitTime(int totalRegularWaitTime) {
        this.totalRegularWaitTime = totalRegularWaitTime;
    }

    public int getTotalPriorityWaitTime() {
        return totalPriorityWaitTime;
    }

    public void setTotalPriorityWaitTime(int totalPriorityWaitTime) {
        this.totalPriorityWaitTime = totalPriorityWaitTime;
    }


    public void setPriorityPatients(int priorityPatients) {
        this.priorityPatients = priorityPatients;
    }


    public void setRegularPatients(int regularPatients) {
        this.regularPatients = regularPatients;
    }

    public void setMaxRegularWaitTime(int maxRegularWaitTime) {
        this.maxRegularWaitTime = maxRegularWaitTime;
    }

    public void setMaxPriorityWaitTime(int maxPriorityWaitTime) {
        this.maxPriorityWaitTime = maxPriorityWaitTime;
    }
}
