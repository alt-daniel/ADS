import models.FreightWagon;
import models.PassengerWagon;
import models.Wagon;
import org.junit.jupiter.api.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class WagonTest {

    Wagon passengerWagon1, passengerWagon2, passengerWagon3, passengerWagon4;
    Wagon freightWagon1, freightWagon2;

    @BeforeEach
    private void setup() {
        passengerWagon1 = new PassengerWagon(8001, 36);
        passengerWagon2 = new PassengerWagon(8002, 18);
        passengerWagon3 = new PassengerWagon(8003, 48);
        passengerWagon4 = new PassengerWagon(8004, 44);
        freightWagon1 = new FreightWagon(9001, 50000);
        freightWagon2 = new FreightWagon(9002, 60000);
    }

    @AfterEach
    private void representationInvariant() {
        assertTrue(!passengerWagon1.hasNextWagon() || passengerWagon1 == passengerWagon1.getNextWagon().getPreviousWagon());
        assertTrue(!passengerWagon2.hasNextWagon() || passengerWagon2 == passengerWagon2.getNextWagon().getPreviousWagon());
        assertTrue(!passengerWagon3.hasNextWagon() || passengerWagon3 == passengerWagon3.getNextWagon().getPreviousWagon());
        assertTrue(!passengerWagon4.hasNextWagon() || passengerWagon4 == passengerWagon4.getNextWagon().getPreviousWagon());
    }

    @Test
    public void T01_AWagonCannotBeInstantiated() {
        // Dig deep ;-)
        assertTrue((Wagon.class.getModifiers() & 0x00000400) != 0);
    }

    @Test
    public void T02_APassengerWagonShouldReportCorrectProperties() {
        // check subclasses
        assertFalse(passengerWagon1 instanceof FreightWagon);

        // check properties
        assertEquals(8001, passengerWagon1.getId());
        assertEquals(36, ((PassengerWagon) passengerWagon1).getNumberOfSeats());

        // check printed information
        assertEquals("[Wagon-8001]", passengerWagon1.toString());
    }

    @Test
    public void T02_AFreightWagonShouldReportCorrectProperties() {
        // check subclasses
        assertFalse(freightWagon1 instanceof PassengerWagon);

        // check properties
        assertEquals(9001, freightWagon1.getId());
        assertEquals(50000, ((FreightWagon) freightWagon1).getMaxWeight());

        // check printed information
        assertEquals("[Wagon-9001]", freightWagon1.toString());
    }

    @Test
    public void T03_ASingleWagonShouldHaveASequenceLengthOfOne() {
        assertEquals(1, passengerWagon1.getSequenceLength(),
                "A single wagon should represent a sequence of length=1");
    }

    @Test
    public void T03_ASingleWagonIsTheLastWagonOfASequence() {
        assertEquals(passengerWagon1, passengerWagon1.getLastWagonAttached(),
                "A single wagon should be the last wagon of its own sequence");
    }

    @Test
    public void T03_TheFirstOfFourWagonsShouldReportASequenceLengthOfFour() {
        passengerWagon2.attachTo(passengerWagon1);
        passengerWagon3.attachTo(passengerWagon2);
        passengerWagon4.attachTo(passengerWagon3);

        assertEquals(4, passengerWagon1.getSequenceLength(),
                "After three attachments a wagon's sequence should have length=4");
    }

    @Test
    public void T03_TheFristWagonOfFourWagonsShouldReturnTheLastWagonOfTheSequence() {
        passengerWagon2.attachTo(passengerWagon1);
        passengerWagon3.attachTo(passengerWagon2);
        passengerWagon4.attachTo(passengerWagon3);

        assertEquals(passengerWagon4, passengerWagon1.getLastWagonAttached(),
                "The last attachment should become the last wagon in a sequence");
    }

    @Test
    public void T03_TheSecondLastWagonShouldReportASequenceLengthOf2() {
        passengerWagon2.attachTo(passengerWagon1);
        passengerWagon3.attachTo(passengerWagon2);
        passengerWagon4.attachTo(passengerWagon3);

        assertEquals(2, passengerWagon3.getSequenceLength());
    }

    @Test
    public void T03_TheSecondLastWagonShouldReturnTheLastOfTheSequence() {
        passengerWagon2.attachTo(passengerWagon1);
        passengerWagon3.attachTo(passengerWagon2);
        passengerWagon4.attachTo(passengerWagon3);

        assertEquals(passengerWagon4, passengerWagon3.getLastWagonAttached());
    }

    @Test
    public void T03_TheLastWagonShouldReportASequenceLengthOf2() {
        passengerWagon2.attachTo(passengerWagon1);
        passengerWagon3.attachTo(passengerWagon2);
        passengerWagon4.attachTo(passengerWagon3);

        assertEquals(1, passengerWagon4.getSequenceLength());
    }

    @Test
    public void T03_TheLastWagonShouldReturnTheLastOfTheSequence() {
        passengerWagon2.attachTo(passengerWagon1);
        passengerWagon3.attachTo(passengerWagon2);
        passengerWagon4.attachTo(passengerWagon3);

        assertEquals(passengerWagon4, passengerWagon4.getLastWagonAttached());
    }

    @Test
    public void T04_AttachingFourWagonsShouldResultInSequenceOfFour() {
        passengerWagon2.attachTo(passengerWagon1);
        passengerWagon3.attachTo(passengerWagon2);
        passengerWagon4.attachTo(passengerWagon3);

        assertEquals(passengerWagon2, passengerWagon1.getNextWagon());
        assertFalse(passengerWagon1.hasPreviousWagon());

        assertEquals(passengerWagon3, passengerWagon2.getNextWagon());
        assertEquals(passengerWagon1, passengerWagon2.getPreviousWagon());

        assertEquals(passengerWagon4, passengerWagon3.getNextWagon());
        assertEquals(passengerWagon2, passengerWagon3.getPreviousWagon());

        assertFalse(passengerWagon4.hasNextWagon());
        assertEquals(passengerWagon3, passengerWagon4.getPreviousWagon());
    }

    @Test
    public void T04_DetachingThirdWagonFromSequenceOfFourShouldResultInTwoSequences() {
        passengerWagon2.attachTo(passengerWagon1);
        passengerWagon3.attachTo(passengerWagon2);
        passengerWagon4.attachTo(passengerWagon3);

        passengerWagon3.detachFromPrevious();

        assertEquals(passengerWagon2, passengerWagon1.getNextWagon());
        assertFalse(passengerWagon1.hasPreviousWagon());

        assertFalse(passengerWagon2.hasNextWagon());
        assertEquals(passengerWagon1, passengerWagon2.getPreviousWagon());

        assertEquals(passengerWagon4, passengerWagon3.getNextWagon());
        assertFalse(passengerWagon3.hasPreviousWagon());

        assertFalse(passengerWagon4.hasNextWagon());
        assertEquals(passengerWagon3, passengerWagon4.getPreviousWagon());
    }

    @Test
    public void T04_ReattachShouldMoveWagonToNewSequence() {
        // Two separate sequences!
        passengerWagon2.attachTo(passengerWagon1);
        passengerWagon4.attachTo(passengerWagon3);

        passengerWagon4.reAttachTo(passengerWagon2);

        assertFalse(passengerWagon3.hasNextWagon());
        assertFalse(passengerWagon3.hasPreviousWagon());

        assertEquals(passengerWagon2, passengerWagon1.getNextWagon());
        assertFalse(passengerWagon1.hasPreviousWagon());

        assertEquals(passengerWagon4, passengerWagon2.getNextWagon());
        assertEquals(passengerWagon1, passengerWagon2.getPreviousWagon());

        assertFalse(passengerWagon4.hasNextWagon());
        assertEquals(passengerWagon2, passengerWagon4.getPreviousWagon());
    }

    @Test
    public void T04_DetatchInMiddleOfSequenceShouldResultInTwoSequences() {
        passengerWagon2.attachTo(passengerWagon1);
        passengerWagon3.attachTo(passengerWagon2);
        passengerWagon4.attachTo(passengerWagon3);

        passengerWagon2.detachTail();

        assertEquals(passengerWagon2, passengerWagon1.getNextWagon());
        assertFalse(passengerWagon1.hasPreviousWagon());

        assertFalse(passengerWagon2.hasNextWagon());
        assertEquals(passengerWagon1, passengerWagon2.getPreviousWagon());

        assertEquals(passengerWagon4, passengerWagon3.getNextWagon());
        assertFalse(passengerWagon3.hasPreviousWagon());

        assertFalse(passengerWagon4.hasNextWagon());
        assertEquals(passengerWagon3, passengerWagon4.getPreviousWagon());
    }

    @Test
    public void T04_RemoveFirstWagonFromThreeShouldResultInSequenceOfTwo() {
        passengerWagon2.attachTo(passengerWagon1);
        passengerWagon3.attachTo(passengerWagon2);

        // remove middle wagon
        passengerWagon1.removeFromSequence();

        assertFalse(passengerWagon1.hasNextWagon());
        assertFalse(passengerWagon1.hasPreviousWagon());

        assertEquals(passengerWagon3, passengerWagon2.getNextWagon());
        assertFalse(passengerWagon2.hasPreviousWagon());

        assertFalse(passengerWagon3.hasNextWagon());
        assertEquals(passengerWagon2, passengerWagon3.getPreviousWagon());
    }

    @Test
    public void T04_RemoveMiddleWagonFromThreeShouldResultInSequenceOfTwo() {
        passengerWagon2.attachTo(passengerWagon1);
        passengerWagon3.attachTo(passengerWagon2);

        // remove middle wagon
        passengerWagon2.removeFromSequence();

        assertFalse(passengerWagon2.hasNextWagon());
        assertFalse(passengerWagon2.hasPreviousWagon());

        assertEquals(passengerWagon3, passengerWagon1.getNextWagon());
        assertFalse(passengerWagon1.hasPreviousWagon());

        assertFalse(passengerWagon3.hasNextWagon());
        assertEquals(passengerWagon1, passengerWagon3.getPreviousWagon());
    }

    @Test
    public void T04_RemoveLastWagonFromThreeShouldResultInSequenceOfTwo() {
        passengerWagon2.attachTo(passengerWagon1);
        passengerWagon3.attachTo(passengerWagon2);

        // remove final wagon
        passengerWagon3.removeFromSequence();

        assertFalse(passengerWagon3.hasNextWagon());
        assertFalse(passengerWagon3.hasPreviousWagon());

        assertEquals(passengerWagon2, passengerWagon1.getNextWagon());
        assertFalse(passengerWagon1.hasPreviousWagon());

        assertFalse(passengerWagon2.hasNextWagon());
        assertEquals(passengerWagon1, passengerWagon2.getPreviousWagon());
    }

    @Test
    public void T05_WholeSequenceOfFourShouldBeReversed() {
        passengerWagon2.attachTo(passengerWagon1);
        passengerWagon3.attachTo(passengerWagon2);
        passengerWagon4.attachTo(passengerWagon3);

        // reverse full sequence
        Wagon rev = passengerWagon1.reverseSequence();

        assertEquals(4, rev.getSequenceLength());
        assertEquals(passengerWagon4, rev);
        assertEquals(passengerWagon3, rev.getNextWagon());
        assertFalse(rev.hasPreviousWagon());

        assertEquals(passengerWagon2, passengerWagon3.getNextWagon());
        assertEquals(passengerWagon4, passengerWagon3.getPreviousWagon());

        assertEquals(passengerWagon1, passengerWagon2.getNextWagon());
        assertEquals(passengerWagon3, passengerWagon2.getPreviousWagon());

        assertFalse(passengerWagon1.hasNextWagon());
        assertEquals(passengerWagon2, passengerWagon1.getPreviousWagon());
    }

    @Test
    public void T05_PartiallyReverseASequenceOfFour() {
        passengerWagon2.attachTo(passengerWagon1);
        passengerWagon3.attachTo(passengerWagon2);
        passengerWagon4.attachTo(passengerWagon3);

        // reverse part of the sequence
        Wagon rev = passengerWagon3.reverseSequence();
        assertEquals(2, rev.getSequenceLength());
        assertEquals(passengerWagon4, rev);

        assertEquals(passengerWagon3, rev.getNextWagon());
        assertEquals(passengerWagon2, rev.getPreviousWagon());

        assertFalse(passengerWagon3.hasNextWagon());
        assertEquals(passengerWagon4, passengerWagon3.getPreviousWagon());

        assertEquals(4, passengerWagon1.getSequenceLength());
        assertFalse(passengerWagon1.hasPreviousWagon());
        assertEquals(passengerWagon2, passengerWagon1.getNextWagon());

        assertEquals(passengerWagon1, passengerWagon2.getPreviousWagon());
        assertEquals(passengerWagon4, passengerWagon2.getNextWagon());
    }
}
