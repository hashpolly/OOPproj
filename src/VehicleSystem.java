import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.Scanner;

public class VehicleSystem {
    private static final String CSV_FILE = "fleet_report.csv";

    public static void main(String[] args) {
        VehicleService service = new VehicleService();
        service.readFromCsv(CSV_FILE);

        DiagnosticTool tool = new DiagnosticTool("Bosch Scanner");
        Scanner sc = new Scanner(System.in);
        int requestCounter = 1;

        while (true) {
            printMenu();
            String choice = ask(sc, "> ");
            if (choice.equals("0")) {
                service.exportToCsv(CSV_FILE);
                break;
            }

            try {
                switch (choice) {
                    case "1":
                        addVehicle(sc, service);
                        next(sc);
                        break;
                    case "2": {
                        Vehicle v = service.findByVin(askVin(sc));
                        if (v == null) {
                            System.out.println("Машина не найдена");
                        } else {
                            v.displayInfo();
                            v.runDiagnostics(tool);
                        }
                        next(sc);
                        break;
                    }
                    case "3": {
                        double max = Double.parseDouble(ask(sc, "Макс. цена: "));
                        service.printList("Price <= " + max, service.filterByMaxPrice(max));
                        next(sc);
                        break;
                    }
                    case "4":
                        updateVehicle(sc, service);
                        next(sc);
                        break;
                    case "5":
                        System.out.println(service.removeVehicle(askVin(sc)) ? "Удалено" : "VIN не найден");
                        next(sc);
                        break;
                    case "6": {
                        String vin = askVin(sc).toUpperCase();
                        String issue = ask(sc, "Проблема: ");
                        service.submitRequest(new MaintenanceRequest("REQ-" + requestCounter++, vin, "Иван", issue));
                        next(sc);
                        break;
                    }
                    case "7":
                        service.processNextRequest("Алексей");
                        next(sc);
                        break;
                    case "8":
                        service.printSorted("Natural order (VIN)", Comparator.naturalOrder());
                        service.printStats();
                        next(sc);
                        break;
                    case "9":
                        service.exportToCsv(CSV_FILE);
                        next(sc);
                        break;
                    case "10":
                        sortedRead(sc, service);
                        next(sc);
                    default:
                        System.out.println("Неверный пункт меню");
                }
            } catch (NumberFormatException e) {
                System.out.println("Нужно ввести число");
            }
        }
        sc.close();
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("1. Добавить машину (Add)");
        System.out.println("2. Найти по VIN (Map)");
        System.out.println("3. Фильтр по цене");
        System.out.println("4. Обновить информацию о машине (Update)");
        System.out.println("5. Удалить по VIN (Delete)");
        System.out.println("6. Клиент: подать заявку (Queue.offer)");
        System.out.println("7. Механик: взять заявку (Queue.poll)");
        System.out.println("8. Показать все машины ");
        System.out.println("9. Сохранить в CSV и прочитать");
        System.out.println("10. Отсортировать машины по типу");
        System.out.println("0. Выход");
    }

    private static void updateVehicle(Scanner sc, VehicleService service) {
        String vin = askVin(sc);
        Vehicle oldVehicle = service.findByVin(vin);
        if (oldVehicle == null) {
            System.out.println("Машина не найдена");
            return;
        }

        System.out.println("Текущее авто: " + oldVehicle.getBrand() + " " + oldVehicle.getModel());
        System.out.println("Выберите ТИП для обновленной машины:");
        System.out.println("1. Легковой автомобиль");
        System.out.println("2. Грузовик");
        String typeChoice = ask(sc, "> ");

        if (!typeChoice.equals("1") && !typeChoice.equals("2")) {
            System.out.println("Неправильное действие");
            return;
        }

        try {
            String brand = ask(sc, "Новый бренд: ");
            String model = ask(sc, "Новая модель: ");
            int year;
            while (true) {
                year = Integer.parseInt(ask(sc, "Новый год выпуска: "));
                if (year < 1889 || year > 2026) {
                    continue;
                }
                break;
            }
            double price = Double.parseDouble(ask(sc, "Новая цена ($): "));
            String engineType = ask(sc, "Новый тип двигателя: " + "\n>ELectric" + "\n>Gasoline" + "\n>Diesel" + "\n>");
            int horsepower = Integer.parseInt(ask(sc, "Новая мощность (л.с.): "));

            Vehicle updatedVehicle = null;

            if (typeChoice.equals("1")) {
                int seats;
                while (true) {
                    seats = Integer.parseInt(ask(sc, "Количество мест: "));
                    if (seats < 0 || seats > 7) {
                        System.out.println("Неверная вместимость машины");
                        continue;
                    }
                    break;
                }
                EngineType type = EngineType.fromString(engineType);
                boolean isElectric = type == EngineType.ELECTRIC;
                updatedVehicle = new Car(vin, brand, model, year, price, EngineType.fromString(engineType), horsepower, seats, isElectric);
            } else if (typeChoice.equals("2")) {
                double payload = Double.parseDouble(ask(sc, "Грузоподъемность (тонн): "));
                updatedVehicle = new Truck(vin, brand, model, year, price, EngineType.fromString(engineType), horsepower, payload);
            }

            if (updatedVehicle != null) {
                System.out.println(service.updateVehicle(updatedVehicle) ? "Обновлено успешно" : "Ошибка обновления");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода: числа зафиксированы некорректно.");
        }
    }

    private static void next(Scanner sc) {
        System.out.print("\n> Нажмите любую кнопку что-бы продолжить... ");
        sc.nextLine();
    }

    private static void addVehicle(Scanner sc, VehicleService service) {
        System.out.println("Выберите тип машины:");
        System.out.println("1. Легковой автомобиль");
        System.out.println("2. Грузовик");
        String typeChoice = ask(sc, "> ");

        if (!typeChoice.equals("1") && !typeChoice.equals("2")) {
            System.out.println("Неправильное действие");
            return;
        }

        try {
            String vin = askVin(sc);
            String brand = ask(sc, "Бренд: ");
            String model = ask(sc, "Модель: ");
            int year = Integer.parseInt(ask(sc, "Год выпуска: "));
            double price = Double.parseDouble(ask(sc, "Цена ($): "));
            EngineType engineType = EngineType.fromString(ask(sc, "Тип двигателя: " + "\n>ELectric" + "\n>Gasoline" + "\nDiesel" + "\n>"));
            int horsepower = Integer.parseInt(ask(sc, "Мощность (л.с.): "));

            Vehicle vehicle;

            if (typeChoice.equals("1")) {
                int seats = Integer.parseInt(ask(sc, "Количество мест: "));
                boolean isElectric = engineType == EngineType.ELECTRIC;
                vehicle = new Car(vin, brand, model, year, price, engineType, horsepower, seats, isElectric);
            } else {
                double payload = Double.parseDouble(ask(sc, "Грузоподъемность (тонн): "));
                vehicle = new Truck(vin, brand, model, year, price, engineType, horsepower, payload);
            }

            if (vehicle != null) {
                service.addVehicle(vehicle);
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода: год, цена, мощность и спецификации должны быть числовыми значениями.");
        }
    }

    private static void sortedRead(Scanner sc, VehicleService service) {
        String s = ask(sc, "Введите тип сортировки: " + "\nЦена - 1" + "\nГод выпуска - 2" + "\nРасход топлива - 3" + "\n>");

        if (s.equalsIgnoreCase("1")) {
            service.printSorted("By price", new VehiclePriceComparator());
        } else if (s.equalsIgnoreCase("2"))
            service.printSorted("By year", new VehicleYearComparator());
        else if ((s.equalsIgnoreCase("3"))) {
            service.printSorted("By fuel", new VehicleEfficiencyComparator());
        }
    }

    private static String ask(Scanner sc, String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private static String askVin(Scanner sc) {
        return ask(sc, "VIN: ").toUpperCase();
    }
}
