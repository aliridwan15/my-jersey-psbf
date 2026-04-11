package myjerseyy.psbf_jersey.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Table(name = "promo_codes")
public class PromoCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nama campaign tidak boleh kosong")
    @Size(min = 3, max = 100, message = "Nama campaign harus 3-100 karakter")
    @Column(name = "campaign_name", nullable = false)
    private String campaignName;

    @NotBlank(message = "Kode kupon tidak boleh kosong")
    @Size(min = 3, max = 50, message = "Kode kupon harus 3-50 karakter")
    @Column(nullable = false, unique = true)
    private String code;

    @NotNull(message = "Diskon tidak boleh kosong")
    @Positive(message = "Diskon harus lebih dari 0")
    @Max(value = 100, message = "Diskon maksimal 100%")
    @Column(name = "discount_percent", nullable = false)
    private Double discountPercent;

    @Column(name = "max_discount")
    private Double maxDiscount;

    @NotNull(message = "Tanggal mulai tidak boleh kosong")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull(message = "Tanggal akhir tidak boleh kosong")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "is_active")
    private Boolean isActive = true;

    public PromoCode() {
    }

    public PromoCode(String campaignName, String code, Double discountPercent, LocalDate startDate, LocalDate endDate, Boolean isActive) {
        this.campaignName = campaignName;
        this.code = code;
        this.discountPercent = discountPercent;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = isActive;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(Double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public Double getMaxDiscount() {
        return maxDiscount;
    }

    public void setMaxDiscount(Double maxDiscount) {
        this.maxDiscount = maxDiscount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isValid() {
        LocalDate today = LocalDate.now();
        return isActive != null && isActive 
            && startDate != null && endDate != null
            && !today.isBefore(startDate) 
            && !today.isAfter(endDate);
    }

    public Double calculateDiscount(Double totalPrice) {
        Double discountAmount = totalPrice * (discountPercent / 100);
        if (maxDiscount != null && discountAmount > maxDiscount) {
            discountAmount = maxDiscount;
        }
        return discountAmount;
    }
}
