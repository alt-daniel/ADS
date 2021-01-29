package graphs;

import java.util.Set;

public interface DGVertex<E> {
    // List<E> getEdges();
    String getId();
    Set<E> getEdges();
}
