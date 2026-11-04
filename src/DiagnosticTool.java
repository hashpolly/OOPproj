public class DiagnosticTool {
    private String scannerName;

    public DiagnosticTool(String scannerName) {
        this.scannerName = scannerName;
    }

    public void scanVehicle(String vehicleName) {
        System.out.println("Diagnostic Scanner [" + scannerName + "]: Scanning " + vehicleName + " -> All systems OK.");
    }
}