public class GPSNavigator {
    private String model;

    public GPSNavigator(String model) {
        this.model = model;
    }

    public String getModel() { return model; }

    public void buildRoute(String destination) {
        System.out.println("GPS (" + model + "): Routing to " + destination);
    }
}