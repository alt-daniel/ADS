package route_planner;

import graphs.DirectedGraph;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

public class RoadMap extends DirectedGraph<Junction, Road> {

    public RoadMap() {
        super();
    }

    public RoadMap(String junctionsResource, String roadsResource) {
        this();
        System.out.printf("\nImporting junctions and roads from %s and %s...\n",
                junctionsResource, roadsResource);;
        int nJunctions = importJunctions(junctionsResource);
        int nRoads = importRoads(roadsResource);
        this.removeUnconnectedVertices();
        System.out.printf("%d junctions and %d bi-directional roads have been imported.\n",
                nJunctions, nRoads);
        System.out.printf("%d junctions and %d one-way roads have been stored into the graph.\n",
                this.getNumVertices(), this.getNumEdges());
    }

    /**
     * the randomizor is used to generate missing authentic data about the fysical length of roads.
     * Repreducible results of calculations can be obtained by fixing the seed of the randomizer
     */
    public static Random randomizer = new Random();
    public static void reSeedRandomizer(long seed) {
        randomizer = new Random(seed);
    }

    private static final String DELIMITER = ";";

    /**
     * imports a list of junctions from a resource file in the project.
     * imports name, x-coordinate, y-coordinate and population at the junction
     * uses the Dutch RD-coordinate system measured in km (see https://nl.wikipedia.org/wiki/Rijksdriehoeksco%C3%B6rdinaten)
     * @param resourceName
     * @return
     */
    public int importJunctions(String resourceName) {
        if (resourceName == null) return 0;
        int numLoaded = 0;

        Scanner scanner = new Scanner(
                RoadMap.class.getClassLoader().getResourceAsStream(resourceName));

        scanner.useDelimiter(DELIMITER);
        scanner.useLocale(Locale.ENGLISH);

        // skip header line
        String header = scanner.nextLine();
        // System.out.println(header);

        while (scanner.hasNext()) {
            Junction junction = new Junction();
            scanner.nextInt(); // skip code
            junction.setName(scanner.next().trim());
            junction.setLocationX(scanner.nextDouble());
            junction.setLocationY(scanner.nextDouble());
            scanner.next(); // skip province
            junction.setPopulation(scanner.nextInt());

            // add the junction to the DirectedGraph data structure
            this.addOrGetVertex(junction);
            numLoaded++;
            scanner.nextLine();
        }

        return numLoaded;
    }

    /**
     * imports a list of road segments from a resource file in the project.
     * imports name, maxSpeed and junction names that are to be connected by road segments.
     * creates two road segments for each (bi-directional) junction pair
     * (The resource files do not provide specific directional information about the roads.
     * Junction names must be configured into the DirectedGraph before roads can be loaded.
     * @param resourceName
     * @return
     */
    public int importRoads(String resourceName) {
        if (resourceName == null) return 0;
        int numLoaded = 0;

        Scanner scanner = new Scanner(
                RoadMap.class.getClassLoader().getResourceAsStream(resourceName));

        scanner.useDelimiter(DELIMITER);
        scanner.useLocale(Locale.ENGLISH);

        // skip header line
        String header = scanner.nextLine();
        // System.out.println(header);

        while (scanner.hasNext()) {
            // road name
            String name = scanner.next().trim();
            int speedlimit = scanner.nextInt();

            // first junction name: the start of the road
            String jName = scanner.next().trim();
            // retrieve the associated junction from the DirectedGraph data structure
            Junction prevJunction = this.getVertexById(jName);

            // process the list of junctions connected by this road
            while (scanner.hasNext() && prevJunction != null) {
                // next junction
                jName = scanner.next().trim();
                if (jName == null || jName.isEmpty()) {
                    break;
                }
                Junction nextJunction = this.getVertexById(jName);

                // configure two road segments for this connection, in opposite directions
                if (nextJunction != null) {
                    // add the road segments to the DirectedGraph data structures
                    this.addOrGetEdge(new Road(name, prevJunction, nextJunction, speedlimit));
                    this.addOrGetEdge(new Road(name, nextJunction, prevJunction, speedlimit));
                    numLoaded++;
                    prevJunction = nextJunction;
                }
            }
            scanner.nextLine();
        }

        return numLoaded;
    }

    /**
     * produces an .svg file in the target classpath folder, which depicts the roadMap and the optional path
     * .svg files can be viewed with a regular browser
     * @param resourceName  name of the file to be generated
     * @param path          optional search path with visited vertices to be coloured into the map
     */
    public void svgDrawMap(String resourceName, DGPath path) {
        try {
            // Uncomment for windows
//            Path resources = Paths.get("5_RoutePlanner/target/classes");
            // Uncomment for other systems
            Path resources = Paths.get(this.getClass().getResource("/").getPath());
            String svgPath = resources.toAbsolutePath() + "/" + resourceName;
            PrintStream svgWriter = new PrintStream(svgPath);

            // header for an .svg file
            svgWriter.println("<?xml version='1.0' standalone='no'?>");
            // configure the viewBox to match the coordinate ranges of the Dutch RD-coordinate system
            svgWriter.printf("<svg width='20cm' height='30cm' viewBox='%d %d %d %d' preserveAspectRatio='xMidYMin'\n",
                    0, -625, 300, 300);
            svgWriter.println("     version='1.1' xmlns='http://www.w3.org/2000/svg'>");

            // first draw all road segments with their default colour
            for (Junction junction: this.getVertices()) {
                junction.svgDrawRoads(svgWriter);
            }

            // on top of that, draw all junctions with a colour depending on their status in the optional path
            for (Junction junction: this.getVertices()) {
                junction.svgDraw(svgWriter,
                        path != null && path.getVisited().contains(junction) ? "yellowgreen" : "black");
            }

            // on top of that, highlight the path, if any has been provided
            if (path != null) {
                for (Road r: path.getEdges()) {
                    r.svgDraw(svgWriter, "lime");
                }
            }

            svgWriter.println("</svg>");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Roadmap lay-out:\n" + super.toString();
    }
}
