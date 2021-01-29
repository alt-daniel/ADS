package graphs;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DirectedGraphTest {

    Country nl, be, de, lux, fr, uk, ro, hu;
    DirectedGraph<Country, Border> europe = new DirectedGraph<>();
    DirectedGraph<Country, Border> africa = new DirectedGraph<>();

    @BeforeEach
    void setUp() {
        nl = europe.addOrGetVertex(new Country("NL"));
        be = europe.addOrGetVertex(new Country("BE"));
        assertEquals(2, europe.addEdges(new Border(be,nl), new Border(nl,be)));

        de = europe.addOrGetVertex(new Country("DE"));
        assertEquals(4, europe.addEdges(new Border(de,nl), new Border(nl,de),
                new Border(de,be), new Border(be,de)));

        lux = this.europe.addOrGetVertex(new Country("LUX"));
        assertEquals(4, europe.addEdges(new Border(lux,be), new Border(be,lux),
                        new Border(lux,de), new Border(de,lux)));

        fr = this.europe.addOrGetVertex(new Country("FR"));
        assertEquals(6, europe.addEdges(new Border(fr,be), new Border(be,fr),
                        new Border(fr,lux), new Border(lux,fr),
                        new Border(fr,de), new Border(de,fr)));

        uk = this.europe.addOrGetVertex(new Country("UK"));
        assertEquals(6, europe.addEdges(new Border(uk,be), new Border(be,uk),
                new Border(uk,fr), new Border(fr,uk),
                new Border(uk,nl), new Border(nl,uk)));

        ro = this.europe.addOrGetVertex(new Country("RO"));
        hu = this.europe.addOrGetVertex(new Country("HU"));
        assertEquals(2, europe.addEdges(new Border(ro,hu), new Border(hu,ro)));
    }

    @AfterEach
    void checkRepresentationInvariants() {
        assertEquals(8, europe.getNumVertices());
        assertEquals(24, europe.getNumEdges());
        for (Country c: europe.getVertices()) {
            for (Border b: c.getEdges()) {
                assertEquals(c, b.getFrom());
            }
        }
    }

    @Test
    void checkGetVertexById() {
        assertEquals(nl, europe.getVertexById("NL"));
        assertEquals(be, europe.getVertexById("BE"));
        assertNull(europe.getVertexById("XX"));
        assertNull(africa.getVertexById("XX"));
    }

    @Test
    void checkAddOrGetVertex() {
        int oldNumV = europe.getNumVertices();
        int oldNumE = europe.getNumEdges();
        assertEquals(nl, europe.addOrGetVertex(new Country("NL")));
        assertEquals(lux, europe.addOrGetVertex(new Country("LUX")));

        assertEquals(oldNumV, europe.getNumVertices());
        assertEquals(oldNumE, europe.getNumEdges());
    }

    @Test
    void checkAddOrGetEdges() {
        assertEquals(0, africa.getNumVertices());
        assertEquals(0, africa.getNumEdges());

        assertEquals(1, africa.addEdges(new Border(new Country("MO"), new Country("AL"))));
        assertEquals(0, africa.addEdges(new Border(africa.getVertexById("MO"), africa.getVertexById("AL"))));
        assertThrows(IllegalArgumentException.class, () ->
                    { africa.addEdges(new Border(new Country("MO"), new Country("AL"))); });

        assertEquals(2, africa.getNumVertices());
        assertEquals(1, africa.getNumEdges());
    }

    @Test
    void checkDFSearch() {
        DirectedGraph.DGPath path = europe.depthFirstSearch("UK","LUX");
        assertNotNull(path);
        assertEquals(europe.getVertexById("UK"), path.getStart());
        assertTrue(path.getEdges().size() >= 2);
        assertTrue(path.getVisited().size() > path.getEdges().size());
    }

    @Test
    void checkDFSearchStartIsTarget() {
        DirectedGraph.DGPath path = europe.depthFirstSearch("HU","HU");
        assertNotNull(path);
        assertEquals(europe.getVertexById("HU"), path.getStart());
        assertEquals(0, path.getEdges().size());
        assertEquals(1, path.getVisited().size());
    }

    @Test
    void checkDFSearchUnconnected() {
        DirectedGraph.DGPath path = europe.depthFirstSearch("UK","HU");
        assertNull(path);
    }

    @Test
    void checkBFSearch() {
        DirectedGraph.DGPath path = europe.breadthFirstSearch("UK","LUX");
        assertNotNull(path);
        assertEquals(europe.getVertexById("UK"), path.getStart());
        assertEquals(2, path.getEdges().size());
        assertTrue(path.getVisited().size() > path.getEdges().size());
    }

    @Test
    void checkBFSearchStartIsTarget() {
        DirectedGraph.DGPath path = europe.breadthFirstSearch("HU","HU");
        assertNotNull(path);
        assertEquals(europe.getVertexById("HU"), path.getStart());
        assertEquals(0, path.getEdges().size());
        assertEquals(1, path.getVisited().size());
    }

    @Test
    void checkBFSearchUnconnected() {
        DirectedGraph.DGPath path = europe.breadthFirstSearch("UK","HU");
        assertNull(path);
    }

    @Test
    void checkDSPSearch() {
        DirectedGraph.DGPath path = europe.dijkstraShortestPath("UK", "LUX", b -> 2.0);
        assertNotNull(path);
        assertEquals(europe.getVertexById("UK"), path.getStart());
        assertEquals(4.0, path.getTotalWeight(), 0.0001);
        assertEquals(path.getTotalWeight(), 2.0 * path.getEdges().size(), 0.0001);
        assertTrue(path.getVisited().size() > path.getEdges().size());
    }

    @Test
    void checkDSPSearchStartIsTarget() {
        DirectedGraph.DGPath path = europe.dijkstraShortestPath("HU", "HU", b -> 2.0);
        assertNotNull(path);
        assertEquals(europe.getVertexById("HU"), path.getStart());
        assertEquals(0.0, path.getTotalWeight(), 0.0001);
        assertEquals(0, path.getEdges().size());
        assertEquals(1, path.getVisited().size());
    }

    @Test
    void checkDSPSearchUnconnected() {
        DirectedGraph.DGPath path = europe.dijkstraShortestPath("UK", "HU", b -> 2.0);
        assertNull(path);
    }

    @Test
    void checkASSearch() {
        DirectedGraph.DGPath path = europe.aStarShortestPath("UK", "LUX", b -> 3.0, (v1,v2) -> 3.0);
        assertNotNull(path);
        assertEquals(europe.getVertexById("UK"), path.getStart());
        assertEquals(6.0, path.getTotalWeight(), 0.0001);
        assertEquals(path.getTotalWeight(), 3.0 * path.getEdges().size(), 0.0001);
        assertTrue(path.getVisited().size() > path.getEdges().size());
    }

    @Test
    void checkASSearchStartIsTarget() {
        DirectedGraph.DGPath path = europe.aStarShortestPath("HU", "HU", b -> 3.0, (v1,v2) -> 3.0);
        assertNotNull(path);
        assertEquals(europe.getVertexById("HU"), path.getStart());
        assertEquals(0.0, path.getTotalWeight(), 0.0001);
        assertEquals(0, path.getEdges().size());
        assertEquals(1, path.getVisited().size());
    }

    @Test
    void checkASSearchUnconnected() {
        DirectedGraph.DGPath path = europe.aStarShortestPath("UK", "HU", b -> 3.0, (v1,v2) -> 3.0);
        assertNull(path);
    }

    @Test
    void checkDSPBASSearch() {
        DirectedGraph.DGPath path = europe.dijkstraShortestPathByAStar("UK", "LUX", b -> 4.0);
        assertNotNull(path);
        assertEquals(europe.getVertexById("UK"), path.getStart());
        assertEquals(8.0, path.getTotalWeight(), 0.0001);
        assertEquals(path.getTotalWeight(), 4.0 * path.getEdges().size(), 0.0001);
        assertTrue(path.getVisited().size() > path.getEdges().size());
    }
}