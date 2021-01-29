import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalTime;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class PatientTest {
    Random randomizer;

    Patient patient0, patient1;

    @BeforeEach
    void setup() {
        randomizer = new Random(1L);

        patient0 = new Patient(LocalTime.of(10,0), LocalTime.of(11,0), 0.2, randomizer);
        patient1 = new Patient(LocalTime.of(10,0), LocalTime.of(11,0), 0.2, randomizer);
    }

    @Test
    void T01_checkPatientCanBePrinted() {
        assertEquals("1003AM(1976-03-21)@10:10:35[DRY_COUGH,FEVER]", patient0.toString());
        assertEquals("1000OM(1938-01-11)@10:00:48[DRY_COUGH,TIREDNESS,FEVER,SORE_MUSCLES,DIARRHOEA]", patient1.toString());
    }
}
