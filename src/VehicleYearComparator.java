import java.util.Comparator;

public class VehicleYearComparator implements Comparator<Vehicle> {
    @Override
    public int compare(Vehicle v1, Vehicle v2) {
        return Integer.compare(v2.getYear(), v1.getYear());
    }
}
