public class Player {
    int id;
    String name;
    String position;

    public Player(int id, String name, String position){
        this.id = id;
        this.name = name;
        this.position = position;
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String position(){
        return position;
    }
}
