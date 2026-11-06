public class Engine {
    private EngineType type;
    private int horsepower;

    public Engine(EngineType type, int horsepower) {
        this.type = type;
        this.horsepower = horsepower;
    }

    public EngineType getType() { return type; }
    public int getHorsepower() { return horsepower; }

    public void start() {
        System.out.println("Engine (" + type + ", " + horsepower + " HP) is running.");
    }
}
