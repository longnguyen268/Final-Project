import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.*;


public class BarcaGUI extends Application {

    private Pane graphPane;
    private BarcaGraph graph;
    private Label statusLabel;
    private ListView<String> playerList;
    private TextField player1field;
    private TextField player2field;
    private Player startPlayer = null;
    private Player endPlayer = null;
    private TextArea resultArea;

    private Map<Player, double[]> playerPositions = new HashMap<>();

    @Override
    public void start(Stage stage) {
        graph = new BarcaGraph(); //initialize empty graph

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #004d98;"); //blue

        // Setup Toolbar
        HBox toolbar = new HBox(10);
        toolbar.setStyle("-fx-padding: 10px; -fx-background-color: #003366;");
        toolbar.setAlignment(Pos.CENTER_LEFT);

        Button btnLoadPlayers = new Button("Load Players");
        Button btnClear = new Button("Clear");

        String buttonStyle = "-fx-background-color: #edbb00; -fx-text-fill: #004d98; -fx-font-weight: bold; -fx-cursor: hand; -fx-font-size: 14px;";
        btnLoadPlayers.setStyle(buttonStyle);
        btnClear.setStyle(buttonStyle);

        btnLoadPlayers.setOnAction(e -> {
                graph = new BarcaGraph();
                Data.loadData(graph);
                updatePlayerList();
                drawGraph();
                statusLabel.setText("Loaded " + graph.getPlayerCount() + " Barca legends with " +
                    graph.getEdgeCount() + " connections");
                resultArea.setText("Ready! Enter two player names to find degrees of separation.");
        });

        btnClear.setOnAction(e -> {
            startPlayer = null;
            endPlayer = null;
            player1field.clear();
            player2field.clear();
            resultArea.clear();
            drawGraph();
            statusLabel.setText("Cleared. Click 'Load Players' to reload data.");
        });

        toolbar.getChildren().addAll(btnLoadPlayers, btnClear);

        //Setup left panel for controls
        VBox leftPanel = new VBox(10);
        leftPanel.setStyle("-fx-padding: 15px; -fx-background-color: #003366;");
        leftPanel.setPrefWidth(320);

        // Title
        Label title = new Label("FC Barcelona Network");
        title.setStyle("-fx-text-fill: #edbb00; -fx-font-size: 20px; -fx-font-weight: bold;");

        // Player list
        Label listLabel = new Label("All Players:");
        listLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        playerList = new ListView<>();
        playerList.setPrefHeight(200);
        playerList.setStyle("-fx-control-inner-background: #002b5c; -fx-text-fill: white;");
        playerList.setOnMouseClicked(e -> {
            String selected = playerList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                statusLabel.setText("Selected: " + selected);
            }
        });

        Separator separator1 = new Separator();
        separator1.setStyle("-fx-background-color: #edbb00;");

        // Degrees of separation section
        Label degreesTitle = new Label("Degrees of Separation");
        degreesTitle.setStyle("-fx-text-fill: #edbb00; -fx-font-size: 16px; -fx-font-weight: bold;");

        player1field = new TextField();
        player1field.setPromptText("Enter Player 1 (e.g., Lionel Messi)");
        player1field.setStyle("-fx-prompt-text-fill: #888;");

        player2field = new TextField();
        player2field.setPromptText("Enter Player 2 (e.g., Pedri Gonzalez)");
        player2field.setStyle("-fx-prompt-text-fill: #888;");

        Button btnFind = new Button("Find Degrees of Separation");
        btnFind.setStyle("-fx-background-color: #edbb00; -fx-text-fill: #004d98; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand;");
        btnFind.setOnAction(e -> findDegrees());

        Separator separator2 = new Separator();
        separator2.setStyle("-fx-background-color: #edbb00;");

        // Results area
        Label resultsLabel = new Label("Results:");
        resultsLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefHeight(180);
        resultArea.setStyle("-fx-control-inner-background: #002b5c; -fx-text-fill: #edbb00; -fx-font-family: monospace;");
        resultArea.setWrapText(true);

        leftPanel.getChildren().addAll(
                title, listLabel, playerList, separator1,
                degreesTitle, player1field, player2field, btnFind, separator2,
                resultsLabel, resultArea
        );
        // Setup Canvas
        graphPane = new Pane();
        graphPane.setStyle("-fx-background-color: #002b5c;");
        graphPane.setMinSize(800, 600);
        statusLabel = new Label("Click 'Load Players'.");
        statusLabel.setStyle("-fx-text-fill: #edbb00; -fx-padding: 10px; -fx-background-color: #003366;");

        root.setCenter(graphPane);
        root.setTop(toolbar);
        root.setBottom(statusLabel);
        root.setLeft(leftPanel);

        Scene scene = new Scene(root, 1200, 700);
        stage.setTitle("Barca Graph");
        stage.setScene(scene);
        stage.show();
    }

    private void findDegrees() {
        String p1Name = player1field.getText().trim();
        String p2Name = player2field.getText().trim();

        if (p1Name.isEmpty() || p2Name.isEmpty()) {
            statusLabel.setText("Enter both player names");
            resultArea.setText("Error: Please enter both player names.");
            return;
        }

        // Check if players exist
        if (!graph.nameHash.containsKey(p1Name)) {
            statusLabel.setText("Player not found: " + p1Name);
            resultArea.setText("Error: Player '" + p1Name + "' not found in the database.");
            return;
        }

        if (!graph.nameHash.containsKey(p2Name)) {
            statusLabel.setText("Player not found: " + p2Name);
            resultArea.setText("Error: Player '" + p2Name + "' not found in the database.");
            return;
        }

        // Calculate degrees
        int degrees = graph.degreesOfConnection(p1Name, p2Name);
        List<Player> path = graph.findPath(p1Name, p2Name);

        // Build result text
        StringBuilder result = new StringBuilder();
        result.append("DEGREES OF SEPARATION\n");
        result.append("═".repeat(33)).append("\n\n");
        result.append("From ").append(p1Name).append("\n");
        result.append("to ").append(p2Name).append("\n\n");

        if (degrees == -1) {
            result.append("NO CONNECTION FOUND!\n\n");
            result.append("These players are not connected through any chain of teammates.\n");
            result.append("They may have played in different eras with no overlap.\n");
            statusLabel.setText("No connection found between " + p1Name + " and " + p2Name);
        } else {
            result.append("Results found: ").append(degrees).append(" DEGREE(S) OF SEPARATION\n\n");
            result.append("PATH:\n");
            for (int i = 0; i < path.size(); i++) {
                result.append("   ").append(i + 1).append(". ").append(path.get(i).getName());
                if (i < path.size() - 1) {
                    result.append("\n      ↓\n");
                }
            }
            result.append("\n");
            statusLabel.setText(degrees + " degree(s) between " + p1Name + " and " + p2Name);

            // Highlight the path in the graph
            drawPath(path);
        }

        resultArea.setText(result.toString());
    }

    private void updatePlayerList() {
        if (playerList != null && graph != null) {
            playerList.getItems().clear();
            List<String> names = new ArrayList<>(graph.nameHash.keySet());
            Collections.sort(names);
            playerList.getItems().addAll(names);
        }
    }

    private void drawPath(List<Player> path) {
       if (path == null || path.size() <2){
           return;
       }
       for (int i = 0; i < path.size()-1; i++) {
           Player p1 = path.get(i);
           Player p2 = path.get(i + 1);

           double[] pos1 = playerPositions.get(p1);
           double[] pos2 = playerPositions.get(p2);

           if (pos1 != null && pos2 != null) {
               Line highlightLine = new Line(pos1[0], pos1[1], pos2[0], pos2[1]);
               highlightLine.setStroke(Color.DARKRED);
               highlightLine.setStrokeWidth(5);
               graphPane.getChildren().add(highlightLine);
           }
       }
    }

    private void drawGraph() {
        graphPane.getChildren().clear();
        playerPositions.clear();

        if (graph == null || graph.adjacency.isEmpty()) {
            statusLabel.setText("No data loaded. Click 'Load Players' to begin");
            return;
        }

        double width = graphPane.getWidth();
        double height = graphPane.getHeight();

        if(width <= 0){
            width = 800;
        }

        if(height <= 0){
            height = 600;
        }

        List<Player> players = new ArrayList<>(graph.adjacency.keySet());
        int centerX = (int)(width / 2);
        int centerY = (int)(height / 2);
        int radius = (int)(Math.min(width, height) / 2.5);

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            double angle = 2 * Math.PI * i / players.size();
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            playerPositions.put(player, new double[]{x, y});
        }

        // 2. Draw Nodes (Players)
        // TODO: Loop through every planet again.
        // TODO: Create a Circle(p.x, p.y, 10)
        for (Player eachPlayer : players) {
            double[] pos = playerPositions.get(eachPlayer);
            if (pos == null) continue;

            // Circle
            Circle circle = new Circle(pos[0], pos[1], 12);
            circle.setFill(Color.GOLD);
            circle.setStroke(Color.DARKGOLDENROD);
            circle.setStrokeWidth(2);

            // Click handler
            final Player finalPlayer = eachPlayer;
            circle.setOnMouseClicked(e -> {
                if (player1field.getText().isEmpty()) {
                    player1field.setText(finalPlayer.getName());
                    statusLabel.setText("Player 1 set to: " + finalPlayer.getName());
                } else if (player2field.getText().isEmpty()) {
                    player2field.setText(finalPlayer.getName());
                    statusLabel.setText("Player 2 set to: " + finalPlayer.getName());
                } else {
                    player1field.clear();
                    player2field.clear();
                    player1field.setText(finalPlayer.getName());
                    statusLabel.setText("Fields cleared. Player 1 set to: " + finalPlayer.getName());
                }
            });

            //Player name label
            Text label = new Text(pos[0] - 15, pos[1] - 15, eachPlayer.getName());
            label.setFill(Color.WHITE);
            label.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");

            graphPane.getChildren().addAll(circle, label);
        }

        // 3. draw edges
        Set<String> drawnEdges = new HashSet<>();
        for (Player p1 : graph.adjacency.keySet()) {
            double[] pos1 = playerPositions.get(p1);
            if (pos1 == null) continue;

            for (Player p2 : graph.adjacency.get(p1)) {
                String edgeKey = Math.min(p1.getId(), p2.getId()) + "-" + Math.max(p1.getId(), p2.getId());
                if (drawnEdges.contains(edgeKey)) continue;
                drawnEdges.add(edgeKey);

                double[] pos2 = playerPositions.get(p2);
                if (pos2 == null) continue;

                Line line = new Line(pos1[0], pos1[1], pos2[0], pos2[1]);
                line.setStroke(Color.GRAY);
                line.setStrokeWidth(1.5);
                graphPane.getChildren().add(line);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}