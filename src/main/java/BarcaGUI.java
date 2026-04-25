import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.CategoryAxis;

public class BarcaGUI extends Application {

    private Pane graphPane;
    private BarcaGraph graph;
    private Label statusLabel;
    private ListView<String> playerList;
    private TextField searchBox1;
    private TextField searchBox2;
    private Player startPlayer = null;
    private Player endPlayer = null;
    private TextArea resultArea;

    private Map<Player, double[]> playerPositions = new HashMap<>();
    private Map<Player, List<Line>> playerEdges = new HashMap<>();

    @Override
    public void start(Stage stage) {
        graph = new BarcaGraph(); //initialize empty graph

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #004d98;"); //blue

        // Setup Toolbar
        HBox toolbar = new HBox(10);
        toolbar.setStyle("-fx-padding: 10px; -fx-background-color: #333;");
        Button btnGen = new Button("Generate Universe");
        Button btnClear = new Button("Clear Path");
        Button btnDegreeDistribution = new Button("Degree Distribution");

        btnGen.setOnAction(e -> generateAndDraw());
        btnClear.setOnAction(e -> {
            startNode = null; endNode = null;
            drawGraph();
        });
        btnDegreeDistribution.setOnAction(e -> degreeDistribution());
        toolbar.getChildren().addAll(btnGen, btnClear, btnDegreeDistribution);

        // Setup Canvas
        spacePane = new Pane();
        statusLabel = new Label("Click 'Generate Universe'. If screen stays black, Graph.java is empty!");
        statusLabel.setStyle("-fx-text-fill: white; -fx-padding: 10px;");

        root.setCenter(spacePane);
        root.setTop(toolbar);
        root.setBottom(statusLabel);

        Scene scene = new Scene(root, 1000, 700);
        stage.setTitle("Galactic Graph");
        stage.setScene(scene);
        stage.show();
    }

    private void generateAndDraw() {
        // This calls the Generator (which currently returns Earth/Mars)
        universe = UniverseGenerator.generate(1000, 650);
        startNode = null;
        endNode = null;
        drawGraph();
    }

    private void drawGraph() {
        spacePane.getChildren().clear();

        if (universe == null || universe.getVertices().isEmpty()) {
            statusLabel.setText("Error: Universe is empty. Did Student A implement Graph.addVertex?");
            return;
        }

        // --- STUDENT B TASK: DRAW THE GRAPH ---

        // 1. Draw Edges (Lines)
        // TODO: Loop through every planet in universe.getVertices()
        // TODO: Loop through every neighbor in universe.getNeighbors(planet)
        // TODO: Create a new Line(p.x, p.y, neighbor.x, neighbor.y)
        // TODO: spacePane.getChildren().add(line);
        for (Planet eachPlanet : universe.getVertices()) {
            for (Edge<Planet> thisEdge : universe.getNeighbors(eachPlanet)) {
                Line line = new Line();
                line.startXProperty().bind(eachPlanet.xProperty());
                line.startYProperty().bind(eachPlanet.yProperty());
                line.endXProperty().bind(thisEdge.destination.xProperty());
                line.endYProperty().bind(thisEdge.destination.yProperty());
                line.setStroke(Color.WHITE);
                spacePane.getChildren().add(line);
            }
        }

        // 2. Draw Nodes (Planets)
        // TODO: Loop through every planet again.
        // TODO: Create a Circle(p.x, p.y, 10)
        // TODO: Set color Color.CYAN
        // TODO: spacePane.getChildren().add(circle);
        for (Planet eachPlanet : universe.getVertices()) {
            Circle circle = new Circle();
            circle.setRadius(8);
            circle.setFill(Color.GOLD);
            circle.centerXProperty().bindBidirectional(eachPlanet.xProperty());
            circle.centerYProperty().bindBidirectional(eachPlanet.yProperty());
            circle.setOnMouseClicked(e -> handlePlanetClick(eachPlanet));
            circle.setOnMouseDragged(e -> {
                circle.setCenterX(e.getX());
                circle.setCenterY(e.getY());
            });
            spacePane.getChildren().add(circle);
            planetCircles.put(eachPlanet, circle);
        }

        // 3. Add Click Events
    }

    private void handlePlanetClick(Planet p) {
        if (startNode == null) {
            startNode = p;
            statusLabel.setText("Start: " + p.getName());
        } else if (endNode == null && p != startNode) {
            endNode = p;
            statusLabel.setText("Calculating path...");
            drawPath();
        }
    }

    private void drawPath() {
        List<Planet> path = Pathfinder.findShortestPath(universe, startNode, endNode);

        // --- STUDENT B TASK: DRAW THE PATH ---
        // TODO: Loop through the 'path' list.
        // TODO: Draw a thick GOLD line connecting the planets.
        for (int i = 0; i < path.size(); i ++) {
            Line eachLine = new Line();
            eachLine.startXProperty().bind(path.get(i).previous.xProperty());
            eachLine.startYProperty().bind(path.get(i).previous.yProperty());
            eachLine.endXProperty().bind(path.get(i).xProperty());
            eachLine.endYProperty().bind(path.get(i).yProperty());
            if (Math.hypot(path.get(i).previous.getX() - path.get(i).getX(), path.get(i).previous.getY() - path.get(i).getY()) > 200) {
                eachLine.setStrokeWidth(5);
                eachLine.setStroke(Color.RED);
            }
            else if (Math.hypot(path.get(i).previous.getX() - path.get(i).getX(), path.get(i).previous.getY() - path.get(i).getY()) < 150) {
                eachLine.setStrokeWidth(10);
                eachLine.setStroke(Color.GREEN);
            }
            else {
                eachLine.setStrokeWidth(7);
                eachLine.setStroke(Color.WHITE);
            }
            spacePane.getChildren().add(eachLine);
        }

        if (path.isEmpty()) {
            statusLabel.setText("No path found.");
        } else {
            statusLabel.setText("Path found! Steps: " + path.size());
        }
    }

    private void degreeDistribution(){
        if(universe == null){
            statusLabel.setText("Generate a universe first.");
            return;
        }

        System.out.println("Degree distribution");

        Map<Integer, Integer> degreeCount = new HashMap<>();

        for(Planet planet: universe.getVertices()){
            int degree = universe.getNeighbors(planet).size();
            degreeCount.put(degree, degreeCount.getOrDefault(degree, 0) + 1);
            //we used getOrDefault(degree, 0) instead of get(degree) in case of that planet being
            //the first planet to have that many degrees instead of it returning null.
            System.out.println(planet.getName() +" has "+degree+" connections.");
        }

        if(degreeCount.isEmpty()){
            System.out.println("degree count is empty");
            return;
        }

        Stage chartStage = new Stage();
        chartStage.setTitle("Degree Distribution");

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Number of degrees");
        yAxis.setLabel("Number of planets");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Degree Distribution");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Planets");

        for(Map.Entry<Integer, Integer> entry : degreeCount.entrySet()){
            series.getData().add(new XYChart.Data<>(String.valueOf(entry.getKey()), entry.getValue()));
        }

        barChart.getData().add(series);

        Scene scene = new Scene(barChart, 600, 400);
        chartStage.setScene(scene);
        chartStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}