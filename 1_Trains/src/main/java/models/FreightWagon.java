package models;


public class FreightWagon extends Wagon {
    private int maxWeight;
    private FreightWagon nextWagon;
    private FreightWagon previousWagon;

    public FreightWagon(int wagonId, int maxWeight) {
        super(wagonId);
        this.maxWeight = maxWeight;
    }

    public int getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(int maxWeight) {
        this.maxWeight = maxWeight;
    }
}
