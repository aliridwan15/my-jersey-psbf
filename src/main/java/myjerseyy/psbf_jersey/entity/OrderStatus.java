package myjerseyy.psbf_jersey.entity;

public enum OrderStatus {
    PENDING("Menunggu Pembayaran", "bg-gray-500 text-white"),
    CONFIRMED("Dikonfirmasi", "bg-blue-500 text-white"),
    PROCESSING("Sedang Diproses", "bg-yellow-500 text-gray-800"),
    SHIPPED("Sedang Dikirim", "bg-orange-500 text-white"),
    COMPLETED("Selesai", "bg-green-500 text-white"),
    CANCELLED("Dibatalkan", "bg-red-500 text-white"),
    RETURNED("Dikembalikan", "bg-pink-500 text-white");

    private final String displayName;
    private final String colorClass;

    OrderStatus(String displayName, String colorClass) {
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
