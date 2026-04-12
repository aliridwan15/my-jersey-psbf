package myjerseyy.psbf_jersey.entity;

public enum PaymentStatus {
    PENDING("Menunggu Validasi", "bg-yellow-100 text-yellow-700 border-yellow-200"),
    SUCCESS("Diterima", "bg-green-100 text-green-700 border-green-200"),
    FAILED("Ditolak", "bg-red-100 text-red-700 border-red-200");

    private final String displayName;
    private final String colorClass;

    PaymentStatus(String displayName, String colorClass) {
        this.displayName = displayName;
        this.colorClass = colorClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColorClass() {
        return colorClass;
    }
}
