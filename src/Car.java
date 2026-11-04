public class Car extends Vehicle implements Maintainable, Drivable {
    private int seatingCapacity;
    private boolean isElectric;

    public Car(String vin, String brand, String model, int year, double price, String engineType, int horsepower, int seatingCapacity, boolean isElectric) {
        super(vin, brand, model, year, price, engineType, horsepower);
        this.seatingCapacity = seatingCapacity;
        this.isElectric = isElectric;
    }

    public int getSeatingCapacity() { return seatingCapacity; }
    public boolean isElectric() { return isElectric; }

    @Override
    public double calculateFuelEfficiency() {
        return isElectric ? 0.0 : 7.5;
    }

    @Override
    public void refuel() {
        if (isElectric) {
            System.out.println(getBrand() + " " + getModel() + " charging EV battery...");
        } else {
            System.out.println(getBrand() + " " + getModel() + " refueling gasoline...");
        }
    }

    @Override
    public void performMaintenance() {
        System.out.println("Car maintenance: Checking brakes, tire pressure and battery.");
    }

    @Override
    public double calculateMaintenanceCost() {
        return isElectric ? 150.0 : 300.0;
    }

    @Override
    public void startEngine() {
        System.out.println(getBrand() + " " + getModel() + ": Engine started.");
    }

    @Override
    public void stopEngine() {
        System.out.println(getBrand() + " " + getModel() + ": Engine stopped.");
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("  Type: Car | Seats: " + seatingCapacity + " | Electric: " + isElectric);
    }
}