package myjerseyy.psbf_jersey.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "store_profiles")
public class StoreProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Nama toko tidak boleh kosong")
    @Size(max = 100, message = "Nama toko maksimal 100 karakter")
    @Column(name = "store_name", length = 100)
    private String storeName;
    
    @Size(max = 20, message = "Nomor HP maksimal 20 karakter")
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;
    
    @Size(max = 100, message = "Email maksimal 100 karakter")
    @Column(length = 100)
    private String email;
    
    @Column(name = "full_address", columnDefinition = "TEXT")
    private String fullAddress;
    
    @Column(name = "logo_image", columnDefinition = "MEDIUMTEXT")
    private String logoImage;
    
    @Column(name = "about_us", columnDefinition = "TEXT")
    private String aboutUs;
    
    @Column
    private Double latitude = -7.2575;
    
    @Column
    private Double longitude = 112.7521;
    
    public StoreProfile() {
    }
    
    public StoreProfile(String storeName) {
        this.storeName = storeName;
        this.latitude = -7.2575;
        this.longitude = 112.7521;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getStoreName() {
        return storeName;
    }
    
    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFullAddress() {
        return fullAddress;
    }
    
    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }
    
    public String getLogoImage() {
        return logoImage;
    }
    
    public void setLogoImage(String logoImage) {
        this.logoImage = logoImage;
    }
    
    public String getAboutUs() {
        return aboutUs;
    }
    
    public void setAboutUs(String aboutUs) {
        this.aboutUs = aboutUs;
    }
    
    public Double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    public Double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
