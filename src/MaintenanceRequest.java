public class MaintenanceRequest {
    private String requestId;
    private String vehicleVin;
    private String clientName;
    private String issueDescription;

    public MaintenanceRequest(String requestId, String vehicleVin, String clientName, String issueDescription) {
        this.requestId = requestId;
        this.vehicleVin = vehicleVin;
        this.clientName = clientName;
        this.issueDescription = issueDescription;
    }

    public String getVehicleVin() { return vehicleVin; }

    @Override
    public String toString() {
        return "Req #" + requestId + " (" + clientName + "): " + issueDescription + " [VIN: " + vehicleVin + "]";
    }
}
