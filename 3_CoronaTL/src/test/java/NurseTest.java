import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalTime;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class NurseTest {
    private static final LocalTime START_TIME = LocalTime.of(10, 0);
    private static final LocalTime END_TIME = LocalTime.of(10, 15);

    Random randomizer;

    Patient[] patients = new Patient[3];
    Nurse nurse;

    @BeforeEach
    void setup() {
        randomizer = new Random(1L);

        for (int p = 0; p < patients.length; p++) {
            patients[p] = new Patient(START_TIME, END_TIME, 0.2, randomizer);
            System.out.println(patients[p]);
        }
        nurse = new Nurse("Suzy", START_TIME, randomizer);
    }

    @Test
    void T00_checkTestDataGenerator() {
        // double check setup; should be fine following the seed of the random number generator
        // if this fails, many other may fail as well
        assertEquals("10:00:35", patients[0].getArrivedAt().toString(),
                "Random number generator seed is not working properly, contact your teacher");
        assertEquals("10:00:48", patients[1].getArrivedAt().toString(),
                "Random number generator seed is not working properly, contact your teacher");
        assertEquals("10:06:48", patients[2].getArrivedAt().toString(),
                "Random number generator seed is not working properly, contact your teacher");
    }

    @Test
    void T11_checkNurseCanSamplePatients() {
        // check sampling
        nurse.samplePatient(patients[0], LocalTime.of(10, 1));
        assertEquals(1, nurse.getNumPatientsSampled());
        assertEquals(145, nurse.getTotalSamplingTime());
        // nurse started the sampling only at 10:01
        assertEquals("10:03:25", nurse.getAvailableAt().toString());
    }
}
