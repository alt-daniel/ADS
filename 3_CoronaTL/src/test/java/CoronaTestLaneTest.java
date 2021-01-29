import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalTime;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class CoronaTestLaneTest {
    private static final LocalTime START_TIME = LocalTime.of(10, 0);
    private static final LocalTime END_TIME = LocalTime.of(10, 30);
    private static final long RANDOM_SEED = 1L;

    CoronaTestLane lane;

    @BeforeEach
    void setup() {
        lane = new CoronaTestLane(START_TIME, END_TIME);
    }

    private static LocalTime maxLocalTime(LocalTime t1, LocalTime t2) {
        if (t1.isAfter(t2)) {
            return t1;
        } else {
            return t2;
        }
    }

    @Test
    void T21_checkConfigurationOfOnePatientAndOneNurse() {
        // one nurse, one patient
        lane.configure(1, 1, 0.0, RANDOM_SEED);

        // check number of nurses and patients generated
        assertEquals(1, lane.getNurses().size());
        assertEquals(1, lane.getPatients().size());

        // check arrival times
        assertTrue(lane.getPatients().get(0).getArrivedAt().isAfter(START_TIME));
        assertTrue(lane.getPatients().get(0).getArrivedAt().isBefore(END_TIME));


    }

    @Test
    void T22_checkSimulationOfOnePatientAndOneNurse() {
        // one nurse, one patient
        lane.configure(1, 1, 0.0, RANDOM_SEED);
        lane.simulate();

        // check number of patients sampled
        assertEquals(1, lane.getNurses().get(0).getNumPatientsSampled());

        // check the startTime of the sampling
        assertEquals(lane.getPatients().get(0).getArrivedAt(), lane.getPatients().get(0).getSampledAt());

        // check the total time spent by the nurse in sampling
        assertEquals(lane.getNurses().get(0).getAvailableAt(),
                lane.getPatients().get(0).getSampledAt().plusSeconds(lane.getNurses().get(0).getTotalSamplingTime()));

    }

    @Test
    void T23_checkSimulationStatisticsOfOnePatientAndOneNurse() {
        // one nurse, one patient
        lane.configure(1, 1, 0.0, RANDOM_SEED);
        lane.simulate();

        // check the maximum queue length
        assertEquals(1, lane.getMaxQueueLength(), "A simulation with one patient always has had one patient in the waiting queue");

        // check the actual end-time of all work
        assertEquals(lane.getWorkFinished(), lane.getNurses().get(0).getAvailableAt());

        // check the statistics of the wait times
        assertEquals(lane.getPatients().get(0).getSampledAt(),
                lane.getPatients().get(0).getArrivedAt().plusSeconds(lane.getMaxRegularWaitTime()));
        assertEquals(lane.getMaxRegularWaitTime(), lane.getAverageRegularWaitTime());
    }

    @Test
    void T31_checkConfigurationOfFivePatientsAndTwoNurses() {
        // two nurses, five patients
        lane.configure(2, 5, 0.0, RANDOM_SEED);

        // check number of nurses and patients generated
        assertEquals(2, lane.getNurses().size());
        assertEquals(5, lane.getPatients().size());

        // check arrival times
        assertTrue(lane.getPatients().get(4).getArrivedAt().isAfter(START_TIME));
        assertTrue(lane.getPatients().get(4).getArrivedAt().isBefore(END_TIME));
    }

    @Test
    void T32_checkSimulationOfFivePatientsAndTwoNurses() {
        // two nurses, five patients
        lane.configure(2, 5, 0.0, RANDOM_SEED);
        lane.simulate();

        // check number of patients sampled
        assertEquals(5, lane.getNurses().get(0).getNumPatientsSampled() + lane.getNurses().get(1).getNumPatientsSampled(),
                "This cross-check should always hold for 2 nurses");

        // check the total time spent by the nurses in sampling
        assertEquals(552, lane.getNurses().get(0).getTotalSamplingTime() + lane.getNurses().get(1).getTotalSamplingTime(),
                "This cross-check depends on the random seed");
    }

    @Test
    void T33_checkSimulationStatisticsOfFivePatientsAndTwoNurses() {
        // two nurses, five patients
        lane.configure(2, 5, 0.0, RANDOM_SEED);
        lane.simulate();

        // check the maximum queue length
        assertEquals(1, lane.getMaxQueueLength(), "This result depends on random seed = 1L");

        // check the actual end-time of all work
        assertEquals(lane.getWorkFinished(),
                maxLocalTime(lane.getNurses().get(0).getAvailableAt(), lane.getNurses().get(1).getAvailableAt()),
                "This cross-check should always hold for 2 nurses");

        // check number of patients sampled
        assertEquals(5, lane.getNurses().get(0).getNumPatientsSampled() + lane.getNurses().get(1).getNumPatientsSampled(),
                "This cross-check should always hold for 2 nurses");
    }

    @Test
    void T41_checkPatientsByZipAreaOfFivePatientsAndOneNurse() {
        // one nurses, five patients
        lane.configure(1, 5, 0.0, RANDOM_SEED);;

        // check the counts per zip-code
        assertEquals("{1000=1, 1003=1, 1010=1, 1012=1, 1017=1}", lane.patientsByZipArea().toString(),
                "This result depends on random seed = 1L");
    }
}
