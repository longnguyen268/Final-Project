import java.util.*;

public class Data {
    public static void loadData(BarcaGraph graph){
        String[][] studs = {
                {"Lionel Messi"}, {"Xavi Hernandez"}, {"Andres Iniesta"},
                {"Carles Puyol"}, {"Gerard Pique"}, {"Sergio Busquets"},
                {"Jordi Alba"}, {"Pedri Gonzalez"}, {"Gavi Paez"},
                {"Ronaldinho"}, {"Samuel Eto'o"}, {"Johan Cruyff"},
                {"Rivaldo"}, {"Ronaldo Nazario"}, {"Neymar Jr"}
        };

        for (String[] stud : studs) {
            graph.addPlayer(stud[0]);
        }

        String[][] connections = {
                {"Lionel Messi", "Xavi Hernandez"}, {"Lionel Messi", "Andres Iniesta"},
                {"Lionel Messi", "Ronaldinho"}, {"Lionel Messi", "Samuel Eto'o"},
                {"Lionel Messi", "Neymar Jr"}, {"Lionel Messi", "Pedri Gonzalez"},
                {"Lionel Messi", "Carles Puyol"},
                {"Xavi Hernandez", "Andres Iniesta"}, {"Xavi Hernandez", "Sergio Busquets"},
                {"Andres Iniesta", "Sergio Busquets"},
                {"Carles Puyol", "Gerard Pique"}, {"Gerard Pique", "Sergio Busquets"},
                {"Jordi Alba", "Gerard Pique"}, {"Jordi Alba", "Lionel Messi"},
                {"Pedri Gonzalez", "Gavi Paez"}, {"Pedri Gonzalez", "Sergio Busquets"},
                {"Pedri Gonzalez", "Jordi Alba"},
                {"Ronaldinho", "Samuel Eto'o"}, {"Ronaldinho", "Carles Puyol"},
                {"Johan Cruyff", "Carles Puyol"},
                {"Rivaldo", "Ronaldo Nazario"}, {"Rivaldo", "Lionel Messi"}, {"Rivaldo", "Andres Iniesta"},
                {"Neymar Jr", "Gerard Pique"}, {"Neymar Jr", "Jordi Alba"},
                {"Neymar Jr", "Lionel Messi"}, {"Neymar Jr", "Andres Iniesta"}
        };

        for (String[] conn : connections) {
            Player p1 = graph.nameHash.get(conn[0]);
            Player p2 = graph.nameHash.get(conn[1]);
            if (p1 != null && p2 != null) {
                graph.addEdge(p1, p2);
            }
        }
    }
}
