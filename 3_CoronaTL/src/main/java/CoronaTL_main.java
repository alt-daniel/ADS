import java.time.LocalTime;

public class CoronaTL_main {
    public static void main(String args[]) {

        // instantiate the test lane for a day between 8am and 4pm

        CoronaTestLane coronaTestLane = new CoronaTestLane( LocalTime.of(8,0), LocalTime.of(16,0));

        // configurations and simulations use a random number generator for selection of test data and some simulation parameters
        // simulation results with identical parameters and the same seed should be reproducible
        // simulation results with different parameters but the same seed should be comparable
        // different seeds can be tried to use different test data configurations

        // just some arbitrary seed for these example runs
        long randomizerSeed = 19670427L;

        // 1a. configure and simulate the base case of one nurse and 100 patients without priority
        coronaTestLane.configure(1, 100, 0.0, randomizerSeed);
        coronaTestLane.simulate();
        coronaTestLane.printSimulationResults();

        // 1b. configure and simulate the base case of one nurse and 200 patients without priority
        coronaTestLane.configure(1, 200, 0.0, randomizerSeed);
        coronaTestLane.simulate();
        coronaTestLane.printSimulationResults();

        // 2. configure and simulate the test lane with 2 nurses and 200 patients without priority
        coronaTestLane.configure(2, 200, 0.0, randomizerSeed);
        coronaTestLane.simulate();
        coronaTestLane.printSimulationResults();

        // 3. configure and simulate the scalable test lane with 4 nurses and 500 patients without priority
        coronaTestLane.configure(4, 500, 0.0, randomizerSeed);
        coronaTestLane.simulate();
        coronaTestLane.printSimulationResults();

        // 4a. configure and simulate a day at the test lane with 4 nurses and 500 patients of which 10% have priority
        coronaTestLane.configure(4, 500, 0.1, randomizerSeed);
        coronaTestLane.simulate();
        coronaTestLane.printSimulationResults();

        // 4a. configure and simulate a day at the test lane with 4 nurses and 500 patients of which 20% have priority
        coronaTestLane.configure(4, 500, 0.2, randomizerSeed);
        coronaTestLane.simulate();
        coronaTestLane.printSimulationResults();

        // 5+6. report patient statistics of a day of 500 patients without simulation
        coronaTestLane.configure(4, 500, 0.0, randomizerSeed);
        coronaTestLane.printPatientStatistics();
    }
}
