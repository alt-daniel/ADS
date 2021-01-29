package models;

import java.util.Iterator;

public class Train implements Iterable<Wagon> {
    private final String origin;
    private final String destination;
    private final Locomotive engine;
    private Wagon firstWagon;


    public Train(Locomotive engine, String origin, String destination) {
        this.engine = engine;
        this.destination = destination;
        this.origin = origin;
    }

    public String toString() {
        return " from " + origin + " to " + destination;
    }

    /* three helper methods that are useful in other methods */
    public boolean hasWagons() {
        return this.getFirstWagon() != null;
    }

    public boolean isPassengerTrain() {
        return this.getFirstWagon() instanceof PassengerWagon;
    }

    public boolean isFreightTrain() {
        return this.getFirstWagon() instanceof FreightWagon;
    }

    public Locomotive getEngine() {
        return engine;
    }

    public Wagon getFirstWagon() {
        return firstWagon;
    }

    /**
     * Replaces the current sequence of wagons (if any) in the train
     * by the given new sequence of wagons (if any)
     * (sustaining all representation invariants)
     *
     * @param newSequence the new sequence of wagons (can be null)
     */
    public void setFirstWagon(Wagon newSequence) {
        this.firstWagon = newSequence;
    }

    /**
     * @return the number of Wagons connected to the train
     */
    public int getNumberOfWagons() {
        if (this.hasWagons()) {
            return firstWagon.getSequenceLength();
        } else return 0;
    }

    /**
     * @return the last wagon attached to the train
     */
    public Wagon getLastWagonAttached() {
        if (this.hasWagons()) {
            if (firstWagon.hasNextWagon()) {
                return firstWagon.getLastWagonAttached();
            } else return getFirstWagon();
        } else return null;
    }

    /**
     * @return the total number of seats on a passenger train
     * (return 0 for a freight train)
     */
    public int getTotalNumberOfSeats() {
        int totalSeats = 0;

        if (this.isPassengerTrain()) {
            PassengerWagon currentWagon;
            for (Wagon w : this) {
                // Cast to PassengerWagon so we can use getNumberOfSeats()
                currentWagon = (PassengerWagon) w;
                totalSeats += currentWagon.getNumberOfSeats();
            }
            return totalSeats;
        }
        return totalSeats;
    }

    /**
     * calculates the total maximum weight of a freight train
     *
     * @return the total maximum weight of a freight train
     * (return 0 for a passenger train)
     */
    public int getTotalMaxWeight() {
        int totalMaxWeight = 0;

        if (this.isFreightTrain()) {
            FreightWagon currentWagon;
            for (Wagon w : this) {
                // Cast to FreightWagon so we can use getMaxWeight()
                currentWagon = (FreightWagon) w;
                totalMaxWeight += currentWagon.getMaxWeight();
            }
            return totalMaxWeight;
        }
        return totalMaxWeight;
    }

    /**
     * Finds the wagon at the given position (starting at 1 for the first wagon of the train)
     *
     * @param position where to search for the wagon
     * @return the wagon found at the given position
     * (return null if the position is not valid for this train)
     */
    public Wagon findWagonAtPosition(int position) {
        if (position <= 0) {
            return null;
        }
        if (this.hasWagons()) {
            Wagon currentWagon = getFirstWagon();
            for (int i = 1; i < position; i++) {
                currentWagon = currentWagon.getNextWagon();
            }
            return currentWagon;
        }
        return null;
    }

    /**
     * Finds the wagon with a given wagonId
     *
     * @param wagonId wagon id code
     * @return the wagon found
     * (return null if no wagon was found with the given wagonId)
     */
    public Wagon findWagonById(int wagonId) {
        if (this.hasWagons()) {
            Wagon currentWagon = getFirstWagon();
            Wagon correctWagon = null;
            while (currentWagon.hasNextWagon()) {
                if (currentWagon.getId() == wagonId) {
                    correctWagon = currentWagon;
                    break;
                } else {
                    currentWagon = currentWagon.getNextWagon();
                }
            }
            if (currentWagon.getId() == wagonId) {
                correctWagon = currentWagon;
            }
            return correctWagon;
        }
        return null;
    }

    /**
     * Determines if the given sequence of wagons can be attached to the train
     * Verfies of the type of wagons match the type of train (Passenger or Freight)
     * Verfies that the capacity of the engine is sufficient to pull the additional wagons
     *
     * @param sequence the sequence of wagons
     * @return boolean
     */
    public boolean canAttach(Wagon sequence) {
        if (getFirstWagon() != null) {
            return (sequence.getClass().equals(firstWagon.getClass())
                    && this.getEngine().getMaxWagons() >= (this.getNumberOfWagons() + sequence.getSequenceLength()));
        } else return this.getEngine().getMaxWagons() > sequence.getSequenceLength();
    }

    /**
     * Tries to attach the given sequence of wagons to the rear of the train
     * No change is made if the attachment cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity)
     *
     * @param sequence the sequence of wagons
     * @return whether the attachment could be completed successfully
     */
    public boolean attachToRear(Wagon sequence) {
        if (canAttach(sequence)) {
            if (this.hasWagons()) {
                sequence.setPreviousWagon(this.getLastWagonAttached());
                this.getLastWagonAttached().setNextWagon(sequence);
            } else {
                setFirstWagon(sequence);
            }
            return true;
        }
        return false;
    }

    /**
     * Tries to insert the given sequence of wagons at the front of the train
     * No change is made if the insertion cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity)
     *
     * @param sequence the sequence of wagons
     * @return whether the insertion could be completed successfully
     */
    public boolean insertAtFront(Wagon sequence) {
        if (hasWagons()) {
            // Check if wagon is not already present and can even attach on this train
            // If already had wagons change, the previous value of the old firstWagon and attach to new sequence
            if (canAttach(sequence) && this.getFirstWagon() != sequence) {
                Wagon tempWagon = this.firstWagon;
                this.setFirstWagon(sequence);
                sequence.getLastWagonAttached().setNextWagon(tempWagon);
                tempWagon.setPreviousWagon(sequence.getLastWagonAttached());
                return true;
            } else return false;
        } else if (this.getEngine().getMaxWagons() > sequence.getSequenceLength()) {
            this.setFirstWagon(sequence);
            return true;
        }
        return false;
    }

    /**
     * Tries to insert the given sequence of wagons at the given wagon position in the train
     * No change is made if the insertion cannot be made.
     * (when the sequence is not compatible of the engine has insufficient capacity
     * or the given position is not valid in this train)
     *
     * @param sequence the sequence of wagons
     * @return whether the insertion could be completed successfully
     */
    public boolean insertAtPosition(int position, Wagon sequence) {
        // If has no wagons, can only insert at position 1
        if (!hasWagons()) {
            if (canAttach(sequence) && position == 1) {
                this.setFirstWagon(sequence);
                return true;
            }
        } else if (canAttach(sequence) && position <= this.getNumberOfWagons()) {
            Wagon tempWagon = findWagonAtPosition(position).getNextWagon();
            findWagonAtPosition(position).setNextWagon(sequence);
            this.getLastWagonAttached().setNextWagon(tempWagon);
            tempWagon.setPreviousWagon(this.getLastWagonAttached());
            return true;
        }
        return false;
    }

    /**
     * Tries to remove one Wagon with the given wagonId from this train
     * and attach it at the rear of the given toTrain
     * No change is made if the removal or attachment cannot be made
     * (when the wagon cannot be found, or the trains are not compatible
     * or the engine of toTrain has insufficient capacity)
     *
     * @param wagonId wagon id code
     * @param toTrain train where the wagon will be attached to
     * @return whether the move could be completed successfully
     */
    public boolean moveOneWagon(int wagonId, Train toTrain) {
        // Check if the wagon is even present at the train
        if (this.findWagonById(wagonId) != null) {
            Wagon targetWagon = this.findWagonById(wagonId);
            Wagon tempPreviousWagon = targetWagon.getPreviousWagon();
            Wagon tempNextWagon = targetWagon.getNextWagon();
            targetWagon.setPreviousWagon(null);
            targetWagon.setNextWagon(null);
            // Check if the wagon can even attach on the next train
            // If not, reset the wagon order
            if (toTrain.canAttach(targetWagon)) {
                tempPreviousWagon.setNextWagon(tempNextWagon);
                tempNextWagon.setPreviousWagon(tempPreviousWagon);
                return toTrain.attachToRear(targetWagon);
            } else {
                targetWagon.setPreviousWagon(tempPreviousWagon);
                targetWagon.setNextWagon(tempNextWagon);
                return false;
            }
        }
        return false;
    }

    /**
     * Tries to split this train and move the complete sequence of wagons from the given position
     * to the rear of toTrain
     * No change is made if the split or re-attachment cannot be made
     * (when the position is not valid for this train, or the trains are not compatible
     * or the engine of toTrain has insufficient capacity)
     *
     * @param position where to split
     * @param toTrain train where split sequence will be attached to
     * @return whether the move could be completed successfully
     */
    public boolean splitAtPosition(int position, Train toTrain) {
        // Check if the position does not exceed the length
        if (this.findWagonAtPosition(position) != null) {
            Wagon targetWagons = this.findWagonAtPosition(position);
            // Check if the resting sequence can even attach on next train
            if (toTrain.canAttach(targetWagons)) {
                targetWagons.getPreviousWagon().setNextWagon(null);
                targetWagons.setPreviousWagon(null);
                toTrain.attachToRear(targetWagons);
                return true;
            }
        }
        return false;
    }

    /**
     * Reverses the sequence of wagons in this train (if any)
     * i.e. the last wagon becomes the first wagon
     * the previous wagon of the last wagon becomes the second wagon
     * etc.
     * (No change if the train has no wagons or only one wagon)
     */
    public void reverse() {
        if (this.hasWagons() && this.getNumberOfWagons() > 1) {
            // Unset firstWagon before reversing
            Wagon currentHead = this.firstWagon;
            this.setFirstWagon(null);
            Wagon newHead = currentHead.reverseWagons();
            this.setFirstWagon(newHead);
        }
    }

    @Override
    public Iterator<Wagon> iterator() {
        return new WagonIterator(this);
    }
}

class WagonIterator implements Iterator<models.Wagon> {
    Wagon current;
    boolean first = true;

    public WagonIterator(Train train) {
        current = train.getFirstWagon();
    }

    @Override
    public boolean hasNext() {
        if (current != null) {
            return current.hasNextWagon();
        }
        return false;
    }

    @Override
    public Wagon next() {
        if (!first) {
            return current = current.getNextWagon();
        } else {
            first = false;
            return current;
        }
    }
}
