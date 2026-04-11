package myjerseyy.psbf_jersey.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Label tidak boleh kosong")
    @Size(min = 2, max = 50, message = "Label harus 2-50 karakter")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Alamat tidak boleh kosong")
    @Column(name = "full_address", nullable = false, columnDefinition = "TEXT")
    private String fullAddress;

    @NotBlank(message = "Kota tidak boleh kosong")
    @Size(max = 100, message = "Kota maksimal 100 karakter")
    @Column(nullable = false)
    private String city;

    @Size(max = 10, message = "Kode pos maksimal 10 karakter")
    @Column(name = "postal_code", length = 10)
    private String postalCode;

    @Column(name = "is_default")
    private Boolean isDefault = false;

    public Address() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
}
