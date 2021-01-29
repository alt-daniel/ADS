package models;

public abstract class Wagon {
    protected int id;                 // some unique ID of a Wagon
    private Wagon nextWagon;        // another wagon that is appended at the tail of this wagon
    // a.k.a. the successor of this wagon in a sequence
    // set to null if no successor is connected
    private Wagon previousWagon;    // another wagon that is prepended at the front of this wagon
    // a.k.a. the predecessor of this wagon in a sequence
    // set to null if no predecessor is connected


    // representation invariant propositions:
    // tail-connection-invariant:   wagon.nextWagon == null or wagon == wagon.nextWagon.previousWagon
    // front-connection-invariant:  wagon.previousWagon == null or wagon = wagon.previousWagon.nextWagon

    public Wagon(int wagonId) {
        this.id = wagonId;
    }

    public int getId() {
        return id;
    }

    public Wagon getNextWagon() {
        return nextWagon;
    }

    public Wagon getPreviousWagon() {
        return previousWagon;
    }

    public void setNextWagon(Wagon nextWagon) {
        this.nextWagon = nextWagon;
    }

    public void setNextNextWagon(Wagon nextWagon) {
        this.nextWagon.nextWagon = nextWagon;
    }

    public void setPreviousWagon(Wagon previousWagon) {
        this.previousWagon = previousWagon;
    }

    public String toString() {
        return "[Wagon-" + getId() + "]";
    }

    /**
     * @return whether this wagon has a wagon appended at the tail
     */
    public boolean hasNextWagon() {
        return this.nextWagon != null;
    }

    /**
     * @return whether this wagon has a wagon prepended at the front
     */
    public boolean hasPreviousWagon() {
        return this.previousWagon != null;
    }

    /**
     * finds the last wagon of the sequence of wagons attached to this wagon
     * if no wagons are attached return this wagon itself
     *
     * @return the wagon found
     */
    public Wagon getLastWagonAttached() {
        Wagon lastWagon = this;
        while (lastWagon.hasNextWagon()) {
            lastWagon = lastWagon.getNextWagon();
        }
        return lastWagon;
    }

    /**
     * @return the number of wagons appended to this wagon
     * return 1 if no wagons have been appended.
     */
    public int getSequenceLength() {
        if (!this.hasNextWagon()) {
            return 1;
        } else {
            return this.getNextWagon().getSequenceLength() + 1;
        }
    }

    /**
     * attaches this wagon at the tail of a given prevWagon.
     *
     * @param newPreviousWagon the wagon you'll be attaching the current wagon to
     * @throws RuntimeException if this wagon already has been appended to a wagon.
     * @throws RuntimeException if prevWagon already has got a wagon appended.
     */
    public void attachTo(Wagon newPreviousWagon) {
        if (this.hasPreviousWagon() || newPreviousWagon.hasNextWagon()) {
            throw new RuntimeException();
        } else {
            newPreviousWagon.setNextWagon(this);
            this.setPreviousWagon(newPreviousWagon);
        }
    }

    /**
     * detaches this wagon from its previous wagons.
     * no action if this wagon has no previous wagon attached.
     */
    public void detachFromPrevious() {
        this.getPreviousWagon().setNextWagon(null);
        this.setPreviousWagon(null);
    }

    /**
     * detaches this wagon from its tail wagons.
     * no action if this wagon has no succeeding next wagon attached.
     */
    public void detachTail() {
        this.getNextWagon().setPreviousWagon(null);
        this.setNextWagon(null);
    }

    /**
     * attaches this wagon at the tail of a given newPreviousWagon.
     * if required, first detaches this wagon from its current predecessor
     * and/or detaches the newPreviousWagon from its current successor
     *
     * @param newPreviousWagon the wagon you'll be attaching the current wagon to
     */
    public void reAttachTo(Wagon newPreviousWagon) {
        // Detach existing connections
        if (this.hasNextWagon()) {
            this.detachTail();
        }
        if (this.hasPreviousWagon()) {
            this.detachFromPrevious();
        }

        this.attachTo(newPreviousWagon);
    }

    /**
     * Removes this wagon from the sequence that it is part of, if any by setting the references of the previous
     * and next wagon to each other
     */
    public void removeFromSequence() {
        if (this.hasNextWagon()) {
            this.getNextWagon().setPreviousWagon(this.getPreviousWagon());
        }
        if (this.hasPreviousWagon()) {
            this.getPreviousWagon().setNextWagon(this.getNextWagon());
        }
        this.setNextWagon(null);
        this.setPreviousWagon(null);
    }


    /**
     * Function which reverses a part of the wagon sequence. We allow partial reverses, so in case that we start from
     * anywhere else except the head, we temporarily detach the part that we don't want to reverse, only to attach it
     * back later when the reversing is finished.
     *
     * @return the new start Wagon of the reversed sequence (with is the former last Wagon of the original sequence)
     */
    public Wagon reverseSequence() {
        if (this.hasPreviousWagon()) {
            Wagon splitPoint = this.getPreviousWagon();
            splitPoint.setNextWagon(null);
            this.setPreviousWagon(null);

            Wagon newHead = this.reverseWagons();

            splitPoint.setNextWagon(newHead);
            newHead.setPreviousWagon(splitPoint);

            return newHead;
        } else {
            return this.reverseWagons();
        }
    }

    /**
     * reverses the order in the sequence of wagons from this Wagon until its final successor.
     * The reversed sequence is attached again to the predecessor of this Wagon, if any.
     * no action if this Wagon has no succeeding next wagon attached.
     *
     * @return the new start Wagon of the reversed sequence (with is the former last Wagon of the original sequence)
     */
    public Wagon reverseWagons() {
        // Stop recursing if at the end of the sequence
        if (!this.hasNextWagon()) {
            this.setPreviousWagon(null);
            return this;
        }

        Wagon newHead = this.getNextWagon().reverseWagons();
        // Swap references of wagons
        this.setNextNextWagon(this);
        this.setPreviousWagon(this.getNextWagon());
        this.setNextWagon(null);

        return newHead;
    }
}
