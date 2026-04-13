package myjerseyy.psbf_jersey.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "couriers")
public class Courier {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Nama kurir tidak boleh kosong")
    @Size(max = 100, message = "Nama kurir maksimal 100 karakter")
    @Column(nullable = false, length = 100)
    private String name;
    
    @NotBlank(message = "Kode kurir tidak boleh kosong")
    @Size(max = 20, message = "Kode kurir maksimal 20 karakter")
    @Column(nullable = false, unique = true, length = 20)
    private String code;
    
    @Column(name = "logo_image", columnDefinition = "MEDIUMTEXT")
    private String logoImage;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    public Courier() {
    }
    
    public Courier(String name, String code) {
        this.name = name;
        this.code = code;
        this.isActive = true;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getLogoImage() {
        return logoImage;
    }
    
    public void setLogoImage(String logoImage) {
        this.logoImage = logoImage;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
