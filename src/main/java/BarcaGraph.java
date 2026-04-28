import java.util.*;

public class BarcaGraph {
    public Map<Player, Set<Player>> adjacency;
    public Map<String, Player> nameHash;
    public Map<Integer, Player> idHash;
    int nextId;

    public BarcaGraph() {
        this.adjacency = new HashMap<>();
        this.nameHash = new HashMap<>();
        this.idHash = new HashMap<>();
        this.nextId = 1;
    }

    public Player addPlayer(String name, String position) {
        // Check if player already exists
        if (nameHash.containsKey(name)) {
            return nameHash.get(name);
        }

        // Create new player
        Player player = new Player(nextId++, name, position);

        // Add to HashMaps
        nameHash.put(name, player);
        idHash.put(player.getId(), player);

        // Add to adjacency list
        adjacency.put(player, new HashSet<>());

        return player;
    }

    public void addEdge(Player p1, Player p2) {
        adjacency.get(p1).add(p2);
        adjacency.get(p2).add(p1);
    }

    public int degreesOfConnection(String startName, String targetName){
        Player start = nameHash.get(startName);
        Player target = nameHash.get(targetName);

        if (start == null || target == null) {
            return -1;
        }

        if (start.equals(target)) {
            return 0;
        }

        Set<Player> visited = new HashSet<>();

        Queue<PlayerDistance> queue = new LinkedList<>();

        queue.offer(new PlayerDistance(start, 0));
        visited.add(start);

        while (!queue.isEmpty()) {
            PlayerDistance current = queue.poll();
            Player player = current.player;
            int distance = current.distance;

            for (Player neighbor : adjacency.get(player)) {
                if (!visited.contains(neighbor)) {
                    if (neighbor.equals(target)) {
                        return distance + 1;
                    }

                    visited.add(neighbor);
                    queue.offer(new PlayerDistance(neighbor, distance + 1));
                }
            }
        }

        return -1;  // no path found
    }

    public List<Player> findPath(String startName, String targetName) {
        Player start = nameHash.get(startName);
        Player target = nameHash.get(targetName);

        if (start == null || target == null) {
            return new ArrayList<>();
        }

        Queue<Player> queue = new LinkedList<>();
        Set<Player> visited = new HashSet<>();

        //temporary hashmap for parent
        Map<Player, Player> parent = new HashMap<>();

        queue.add(start);
        visited.add(start);
        parent.put(start, null);  //start doesn't have any parents

        while (!queue.isEmpty()) {
            Player current = queue.poll();

            if (current.equals(target)) {
                //reconstruct
                return reconstructPath(parent, target);
            }

            for (Player neighbor : adjacency.get(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parent.put(neighbor, current);  // Store who came before
                    queue.add(neighbor);
                }
            }

        }
        return new ArrayList<>(); //if not path is found
    }

    private static List<Player> reconstructPath(Map<Player, Player> parent, Player target) {
        List<Player> reversedPath = new ArrayList<>();
        Player current = target;

        while(current != null){
            reversedPath.add(current);  // and add each position to the 'path' list.
            current = parent.get(current);
        }

        List<Player> path = new ArrayList<>();
        for(int i = reversedPath.size() -1 ; i >= 0; i--){
            path.add(reversedPath.get(i));// Remember to reverse the list at the end!
        }
        return path;
    }

    public int getTotalPlayers() {
        return adjacency.size();
    }

    public int getEdgeCount() {
        int total = 0;
        for (Set<Player> neighbors : adjacency.values()) {
            total += neighbors.size();
        }
        return total / 2;
    }

    public int getPlayerCount() {
        return adjacency.size();
    }
}
