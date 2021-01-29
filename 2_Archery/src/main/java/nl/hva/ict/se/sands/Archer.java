package nl.hva.ict.se.sands;

import java.util.*;

/**
 * Holds the name, archer-id and the points scored for 30 arrows.
 *
 * Archers MUST be created by using one of the generator methods. That is way the constructor is private and should stay
 * private. You are also not allowed to add any constructor with an access modifier other then private unless it is for
 * testing purposes in which case the reason why you need that constructor must be contained in a very clear manner
 * in your report.
 */
public class Archer {
    public final static int MAX_ARROWS = 3;
    public final static int MAX_ROUNDS = 10;
    private static Random randomizer = new Random(5);
    private final int id; // Once assigned a value this attribute is not allowed to change.
    private String firstName;
    private String lastName;
    private static int nextId = 135788;
    private int[][] scoreSheet;

    /**
     * Constructs a new instance of Archer and assigns a unique ID to the instance. The ID is not allowed to ever
     * change during the lifetime of the instance! For this you need to use the correct Java keyword. Each new instance
     * is a assigned a number that is 1 higher than the last one assigned. The first instance created should have
     * ID 135788;
     *
     * @param firstName the archers first name.
     * @param lastName the archers surname.
     */
    protected Archer(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = nextId;
        nextId++;
        this.scoreSheet = new int[MAX_ROUNDS][MAX_ARROWS];
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int[][] getScoreSheet() {
        return scoreSheet;
    }

    public void setScoreSheet(int[][] scoreSheet) {
        this.scoreSheet = scoreSheet;
    }

    /**
     * Registers the point for each of the three arrows that have been shot during a round. The <code>points</code>
     * parameter should hold the three points, one per arrow.
     *
     * @param round the round for which to register the points, zero based.
     * @param points the points shot during the round.
     */
    public void registerScoreForRound(int round, int[] points) {
        scoreSheet[round] = points;
    }

    public int getTotalScore() {
        int totalScore = 0;

        for (int round = 0; round<getScoreSheet().length; round++) {
            for (int shot = 0; shot < scoreSheet[round].length; shot++) {
                totalScore = totalScore + scoreSheet[round][shot];
            }
        }

        return totalScore;
    }

    /**
     * Returns the number of 10's scored by this archer.
     * @return the number of 10's for this archer.
     */
    public int getTens() {
        int totalTens = 0;

        for (int round = 0; round < getScoreSheet().length; round++){
            for (int shot = 0; shot < scoreSheet[round].length; shot++) {
                if (scoreSheet[round][shot] == 10) {
                    totalTens++;
                }
            }
        }

        return totalTens;
    }

    /**
     * Returns the number of 9's scored by this archer.
     * @return the number of 9's for this archer.
     */
    public int getNines() {
        int totalNines = 0;

        for (int round = 0; round < getScoreSheet().length; round++){
            for (int shot = 0; shot < scoreSheet[round].length; shot++) {
                if (scoreSheet[round][shot] == 9) {
                    totalNines++;
                }
            }
        }

        return totalNines;
    }

    public int getId() {
        return id;
    }

    /*
    The code below is their for your own convenience. You don't have include it in your report.
     */

    /**
     * This methods creates a List of archers. This method takes care of assigning each arhcher
     * a first name, surname and lets them should 30 arrows.
     *
     * @param nrOfArchers the number of archers in the list.
     * @return
     */
    public static List<Archer> generateArchers(int nrOfArchers) {
        List<Archer> archers = new ArrayList<>(nrOfArchers);
        for (int i = 0; i < nrOfArchers; i++) {
            Archer archer = new Archer(Names.nextFirstName(), Names.nextSurname());
            letArcherShoot(archer);
            archers.add(archer);
        }
        return archers;

    }

    private static void letArcherShoot(Archer archer) {
        for (int round = 0; round < MAX_ROUNDS; round++) {
            archer.registerScoreForRound(round, shootOneRound());
        }
    }

    private static int[] shootOneRound() {
        int[] points = new int[MAX_ARROWS];
        for (int arrow = 0; arrow < MAX_ARROWS; arrow++) {
            points[arrow] = shootArrow();
        }
        return points;
    }

    private static int shootArrow() {
        return 1 + randomizer.nextInt(10);
    }

    public String toString() {
        return id + " (" + getTotalScore() + ") " + getFirstName() + " " + getLastName();
    }
}
