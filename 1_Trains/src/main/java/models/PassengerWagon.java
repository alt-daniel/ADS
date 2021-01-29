package models;

public class PassengerWagon extends Wagon {
    private PassengerWagon nextWagon;
    private PassengerWagon previousWagon;
    private int numberOfSeats;

    public PassengerWagon(int wagonId, int numberOfSeats) {
        super(wagonId);
        this.numberOfSeats = numberOfSeats;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public String toString() {
        return super.toString();
    }
}
