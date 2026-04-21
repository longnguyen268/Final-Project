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




}
