package graphs;

import java.util.HashSet;
import java.util.Set;

public class Country implements DGVertex<Border> {

    private String name;
    Set<Border> borders = new HashSet<>();

    public Country(String name) {
        this.name = name;
    }

    @Override
    public Set<Border> getEdges() {
        return this.borders;
    }

    @Override
    public String getId() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        Country country = (Country) o;
        return name.equals(country.name);
    }
}
