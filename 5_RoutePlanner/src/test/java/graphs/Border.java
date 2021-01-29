package graphs;

import java.util.Objects;

public class Border implements DGEdge<Country> {

    private Country from;
    private Country to;

    @Override
    public Country getFrom() {
        return this.from;
    }

    @Override
    public Country getTo() {
        return this.to;
    }

    public Border(Country from, Country to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        Border border = (Border) o;
        return this.from.equals(border.from) &&
               this.to.equals(border.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }
}
