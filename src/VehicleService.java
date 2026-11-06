import java.io.*;
import java.util.*;

public class VehicleService {
    private final Set<Vehicle> vehicles = new LinkedHashSet<>();
    private final Map<String, Vehicle> byVin = new HashMap<>();
    private final Map<String, List<Vehicle>> byType = new HashMap<>();
    private final Queue<MaintenanceRequest> requests = new ArrayDeque<>();

    public boolean addVehicle(Vehicle v) {
        if (v == null || !vehicles.add(v)) {
            System.out.println("Cannot add: null or duplicate VIN");
            return false;
        }
        byVin.put(v.getVin(), v);
        byType.computeIfAbsent(v.getClass().getSimpleName(), k -> new ArrayList<>()).add(v);
        System.out.println("Added: " + v.getVin());
        return true;
    }

    public boolean updateVehicle(Vehicle updatedVehicle) {
        if (updatedVehicle == null) return false;

        String vin = updatedVehicle.getVin();
        Vehicle existing = byVin.get(vin);
        if (existing == null) {
            System.out.println("Update failed: VIN not found");
            return false;
        }

        vehicles.remove(existing);
        String oldType = existing.getClass().getSimpleName();
        List<Vehicle> group = byType.get(oldType);
        if (group != null) {
            group.remove(existing);
            if (group.isEmpty()) {
                byType.remove(oldType);
            }
        }

        vehicles.add(updatedVehicle);
        byVin.put(vin, updatedVehicle);
        byType.computeIfAbsent(updatedVehicle.getClass().getSimpleName(), k -> new ArrayList<>()).add(updatedVehicle);

        System.out.println("Vehicle updated: " + vin);
        return true;
    }

    public Vehicle findByVin(String vin) {
        return byVin.get(vin);
    }

    public boolean removeVehicle(String vin) {
        Vehicle v = byVin.remove(vin);
        if (v == null) return false;

        vehicles.remove(v);
        String type = v.getClass().getSimpleName();
        List<Vehicle> group = byType.get(type);
        group.remove(v);
        if (group.isEmpty()) {
            byType.remove(type);
        }
        requests.removeIf(r -> r.getVehicleVin().equals(vin));
        return true;
    }

    public List<Vehicle> filterByMaxPrice(double maxPrice) {
        List<Vehicle> result = new ArrayList<>();
        for (Vehicle v : vehicles) {
            if (v.getPrice() <= maxPrice) {
                result.add(v);
            }
        }
        return result;
    }

    public boolean submitRequest(MaintenanceRequest request) {
        if (request == null || !byVin.containsKey(request.getVehicleVin().toUpperCase())) {
            System.out.println("Request rejected: unknown VIN");
            return false;
        }
        requests.offer(request);
        System.out.println("Request submitted: " + request + " (in queue: " + requests.size() + ")");
        return true;
    }

    public void processNextRequest(String mechanicName) {
        MaintenanceRequest request = requests.poll();
        if (request == null) {
            System.out.println("Queue is empty");
            return;
        }
        System.out.println(mechanicName + " processing: " + request);
        Vehicle v = byVin.get(request.getVehicleVin());
        if (v instanceof Maintainable) {
            Maintainable m = (Maintainable) v;
            m.performMaintenance();
            System.out.println("Maintenance cost: $" + m.calculateMaintenanceCost());
        } else {
            System.out.println("Vehicle is not maintainable");
        }
    }

    public void printSorted(String title, Comparator<Vehicle> comparator) {
        List<Vehicle> sorted = new ArrayList<>(vehicles);
        sorted.sort(comparator);
        printList(title, sorted);
    }

    public void printList(String title, List<Vehicle> list) {
        System.out.println("--- " + title + " ---");
        if (list.isEmpty()) {
            System.out.println("(empty)");
            return;
        }
        for (Vehicle v : list) {
            System.out.println(v.getVin() + " | " + v.getBrand() + " " + v.getModel() + " | " + v.getYear()
                    + " | $" + v.getPrice() + " | " + v.calculateFuelEfficiency() + " L/100km");
        }
    }

    public void printStats() {
        double total = 0;
        for (Vehicle v : vehicles) {
            total += v.getPrice();
        }
        System.out.println("--- Stats ---");
        System.out.println("Vehicles: " + vehicles.size() + ", fleet value: $" + total + ", pending requests: " + requests.size());
        for (Map.Entry<String, List<Vehicle>> e : byType.entrySet()) {
            System.out.println(e.getKey() + ": " + e.getValue().size());
        }
    }

    public void exportToCsv(String filepath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            writer.write("VIN,Type,Brand,Model,Year,Price,EngineType,Horsepower,PayloadCapacity,SeatingCapacity,IsElectric");
            writer.newLine();
            for (Vehicle v : vehicles) {
                writer.write(String.format(Locale.US, "%s,%s,%s,%s,%d,%.2f,%s,%d,%.2f,%d,%b",
                        v.getVin(), v.getClass().getSimpleName(), v.getBrand(), v.getModel(),
                        v.getYear(), v.getPrice(), v.getEngine().getType(), v.getEngine().getHorsepower(), v.getPayloadCapacity(), v.getSeatingCapacity(), v.isElectric()));
                writer.newLine();
            }
            System.out.println("Saved to " + new File(filepath).getAbsolutePath());
        } catch (IOException e) {
            System.out.println("CSV write error: " + e.getMessage());
        }
    }

    public void readFromCsv(String filepath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] v = line.split(",");
                Vehicle vehicle;
                if (v[1].equalsIgnoreCase("truck")) {
                    vehicle = new Truck(v[0], v[2], v[3], Integer.parseInt(v[4]),
                            Double.parseDouble(v[5]), changeDataType(v[6]), Integer.parseInt(v[7]), Double.parseDouble(v[8]));
                }
                else if (v[1].equalsIgnoreCase("type")) {
                    continue;
                }
                else {
                    vehicle = new Car(v[0], v[2], v[3], Integer.parseInt(v[4]),
                            Double.parseDouble(v[5]), changeDataType(v[6]), Integer.parseInt(v[7]),
                            Integer.parseInt(v[9]), Boolean.getBoolean(v[10]));
                }
                addVehicle(vehicle);
            }
        } catch (IOException e) {
            System.out.println("CSV read error: " + e.getMessage());
        }
    }
    private static EngineType changeDataType(String type) {
        return EngineType.fromString(type.toUpperCase());
    }
}
