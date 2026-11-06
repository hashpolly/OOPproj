public enum EngineType {
    DIESEL,
    GASOLINE,
    ELECTRIC,
    UNKNOWN;

    public static EngineType fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return UNKNOWN;
        }

        String cleanText = text.trim().toUpperCase();

        for (EngineType type : EngineType.values()) {
            if (type.name().equals(cleanText)) {
                return type;
            }
        }

        if (cleanText.contains("DIESEL")) return DIESEL;
        if (cleanText.contains("ELEC")) return ELECTRIC;
        if (cleanText.contains("GAS") || cleanText.contains("PETROL")) return GASOLINE;

        return UNKNOWN;
    }
}


