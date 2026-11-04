import java.util.Comparator;

public class VehicleEfficiencyComparator implements Comparator<Vehicle> {
    @Override
    public int compare(Vehicle v1, Vehicle v2) {
        return Double.compare(v1.calculateFuelEfficiency(), v2.calculateFuelEfficiency());
    }
}