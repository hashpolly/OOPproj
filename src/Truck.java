public class Truck extends Vehicle implements Maintainable, Drivable {
    private double payloadCapacity;

    public Truck(String vin, String brand, String model, int year, double price, String engineType, int horsepower, double payloadCapacity) {
        super(vin, brand, model, year, price, engineType, horsepower);
        this.payloadCapacity = payloadCapacity;
    }

    public double getPayloadCapacity() { return payloadCapacity; }

    @Override
    public double calculateFuelEfficiency() {
        return 28.5 + (payloadCapacity * 0.5);
    }

    @Override
    public void refuel() {
        System.out.println(getBrand() + " " + getModel() + " refueling diesel...");
    }

    @Override
    public void performMaintenance() {
        System.out.println("Truck maintenance: Checking engine, oil and hydraulics.");
    }

    @Override
    public double calculateMaintenanceCost() {
        return 800.0;
    }

    @Override
    public void startEngine() {
        System.out.println(getBrand() + " " + getModel() + ": Diesel engine started.");
    }

    @Override
    public void stopEngine() {
        System.out.println(getBrand() + " " + getModel() + ": Diesel engine stopped.");
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("  Type: Truck | Payload: " + payloadCapacity + " tons");
    }
}