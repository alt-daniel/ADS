package route_planner;

import graphs.DGEdge;

import java.io.PrintStream;
import java.util.Locale;
import java.util.Objects;

public class Road implements DGEdge<Junction> {
    private String name;        // the name of the road segment
    private Junction from;      // the junction where this road segment starts
    private Junction to;        // the junction where this road segment ends
    private double length;      // the fysical length of the segment in km
    private int maxSpeed;       // the maximum driving speed on the segment in km/h

    /*
        Roadsegments shall be considered uni-directional.
        i.e. if a road is bi-directional, there shall be two segments created in the map with opposite from and to indicators
        length and maxSpeed could be different for both segments of a bidirectional road.

        Roadsegments are uniquely identified by the combination of their name, from junction and to junction
        i.e. there can be multiple road segments between two junctions e.g. with different length or maxSpeed.
     */

    public Road(String name, Junction from, Junction to) {
        this.name = name;
        this.from = from;
        this.to = to;
    }

    public Road(String name, Junction from, Junction to, int maxSpeed) {
        this(name, from, to);
        this.maxSpeed = maxSpeed;
        // fix the actual length of this road segment with some random variability
        // compensating for lack of truely authentic data
        this.length = from.getDistance(to) *
                (1.05 + 0.1 * (from.getVariability() + to.getVariability()));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Junction getFrom() {
        return from;
    }

    public void setFrom(Junction from) {
        this.from = from;
    }

    public Junction getTo() {
        return to;
    }

    public void setTo(Junction to) {
        this.to = to;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public double getTravelTime() {
        return this.getLength() / maxSpeed;
    }

    public double getTravelTimeAtFixedSpeed() {
        return this.getLength() / 120;
    }


    /**
     * Draws the road segment onto a .svg image with the specified colour
     * If no colour is provided, a default will be calculated on the basis of the maxSpeed
     *
     * @param svgWriter
     * @param colour
     */
    public void svgDraw(PrintStream svgWriter, String colour) {
        if (colour == null) {
            if (this.maxSpeed >= 100) colour = "darkorange";
            else if (this.maxSpeed >= 80) colour = "gold";
            else colour = "lightskyblue";
        }
        // width of the road is dirived on the basis of max speed
        double width = 0.2 + this.maxSpeed * 0.008;

        // accounts for the reversed y-direction of the svg coordinate system relative to RD-coordinates
        svgWriter.printf(Locale.ENGLISH, "<line x1='%.3f' y1='%.3f' x2='%.3f' y2='%.3f' stroke-width='%.3f' stroke='%s'/>\n",
                this.getFrom().getLocationX(), -this.getFrom().getLocationY(),
                this.getTo().getLocationX(), -this.getTo().getLocationY(),
                width, colour);
    }

    @Override
    public String toString() {
        return this.to.getName() + "(" + this.name + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        Road road = (Road) o;
        return this.getFrom().equals(road.from) &&
                this.getTo().equals(road.to) && this.getName().equals(road.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }
}
