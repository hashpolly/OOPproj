import java.util.Comparator;
import java.util.Scanner;

public class VehicleSystem {
    private static final String CSV_FILE = "fleet_report.csv";

    public static void main(String[] args) {
        VehicleService service = new VehicleService();
        DiagnosticTool tool = new DiagnosticTool("Bosch Scanner");
        Scanner sc = new Scanner(System.in);
        int requestCounter = 1;

        while (true) {
            printMenu();
            String choice = ask(sc, "> ");
            if (choice.equals("0")) break;

            try {
                switch (choice) {
                    case "1":
                        loadSampleData(service);
                        break;
                    case "2": {
                        Vehicle v = service.findByVin(askVin(sc));
                        if (v == null) {
                            System.out.println("Машина не найдена");
                        } else {
                            v.displayInfo();
                            v.runDiagnostics(tool);
                        }
                        break;
                    }
                    case "3": {
                        double max = Double.parseDouble(ask(sc, "Макс. цена: "));
                        service.printList("Price <= " + max, service.filterByMaxPrice(max));
                        break;
                    }
                    case "4": {
                        String vin = askVin(sc);
                        double price = Double.parseDouble(ask(sc, "Новая цена: "));
                        System.out.println(service.updatePrice(vin, price) ? "Обновлено" : "VIN не найден");
                        break;
                    }
                    case "5":
                        System.out.println(service.removeVehicle(askVin(sc)) ? "Удалено" : "VIN не найден");
                        break;
                    case "6": {
                        String vin = askVin(sc);
                        String issue = ask(sc, "Проблема: ");
                        service.submitRequest(new MaintenanceRequest("REQ-" + requestCounter++, vin, "Иван", issue));
                        break;
                    }
                    case "7":
                        service.processNextRequest("Алексей");
                        break;
                    case "8":
                        service.printSorted("Natural order (VIN)", Comparator.naturalOrder());
                        service.printSorted("By price", new VehiclePriceComparator());
                        service.printSorted("By year (newest first)", new VehicleYearComparator());
                        service.printSorted("By fuel efficiency", new VehicleEfficiencyComparator());
                        service.printStats();
                        break;
                    case "9":
                        service.exportToCsv(CSV_FILE);
                        service.readFromCsv(CSV_FILE);
                        break;
                    default:
                        System.out.println("Неверный пункт меню");
                }
            } catch (NumberFormatException e) {
                System.out.println("Нужно ввести число");
            }
        }
        sc.close();
    }

    private static void loadSampleData(VehicleService service) {
        Car tesla = new Car("VIN-4", "Tesla", "Model 3", 2023, 42000, "Electric", 283, 5, true);
        tesla.setGpsNavigator(new GPSNavigator("Garmin"));
        service.addVehicle(tesla);
        service.addVehicle(new Truck("VIN-2", "Volvo", "FH16", 2022, 120000, "Diesel", 750, 25.0));
        service.addVehicle(new Car("VIN-3", "Toyota", "Camry", 2015, 15000, "2.5L", 180, 5, false));
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("1. Загрузить машины (Add)");
        System.out.println("2. Найти по VIN (Map)");
        System.out.println("3. Фильтр по цене");
        System.out.println("4. Обновить цену (Update)");
        System.out.println("5. Удалить по VIN (Delete)");
        System.out.println("6. Клиент: подать заявку (Queue.offer)");
        System.out.println("7. Механик: взять заявку (Queue.poll)");
        System.out.println("8. Сортировки и статистика");
        System.out.println("9. Сохранить в CSV и прочитать");
        System.out.println("0. Выход");
    }

    private static String ask(Scanner sc, String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private static String askVin(Scanner sc) {
        return ask(sc, "VIN: ").toUpperCase();
    }
}
