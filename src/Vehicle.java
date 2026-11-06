import java.util.Objects;

public abstract class Vehicle implements Comparable<Vehicle> {
    private final String vin;
    private String brand;
    private String model;
    private int year;
    private double price;
    private Engine engine;
    private GPSNavigator gpsNavigator;

    public Vehicle(String vin, String brand, String model, int year, double price, EngineType engineType, int horsepower) {
        this.vin = vin.trim();
        setBrand(brand);
        setModel(model);
        setYear(year);
        setPrice(price);
        this.engine = new Engine(engineType, horsepower);
    }


    public double getPayloadCapacity() { return 0; }

    public int getSeatingCapacity() { return 0; }

    public boolean isElectric() { return false; }

    public String getVin() { return vin; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) {
        this.brand = (brand != null && !brand.trim().isEmpty()) ? brand : "Unknown";
    }

    public String getModel() { return model; }
    public void setModel(String model) {
        this.model = (model != null && !model.trim().isEmpty()) ? model : "Unknown";
    }

    public int getYear() { return year; }
    public void setYear(int year) {
        this.year = (year >= 1886) ? year : 2000;
    }

    public double getPrice() { return price; }
    public void setPrice(double price) {
        this.price = (price >= 0) ? price : 0.0;
    }

    public Engine getEngine() { return engine; }

    public GPSNavigator getGpsNavigator() { return gpsNavigator; }
    public void setGpsNavigator(GPSNavigator gpsNavigator) {
        this.gpsNavigator = gpsNavigator;
    }

    public void displayInfo() {
        System.out.println("Vehicle [VIN: " + vin + "]: " + brand + " " + model + ", Year: " + year + ", Price: $" + price);
        engine.start();
        System.out.println("  GPS: " + (gpsNavigator != null ? gpsNavigator.getModel() : "No GPS"));
    }

    public void runDiagnostics(DiagnosticTool tool) {
        tool.scanVehicle(brand + " " + model + " (" + vin + ")");
    }

    public abstract double calculateFuelEfficiency();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vehicle)) return false;
        return Objects.equals(vin, ((Vehicle) o).vin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vin);
    }

    @Override
    public int compareTo(Vehicle other) {
        return vin.compareTo(other.vin);
    }
}
