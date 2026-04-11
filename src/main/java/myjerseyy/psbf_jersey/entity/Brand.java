package myjerseyy.psbf_jersey.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Base64;

@Entity
@Table(name = "brands")
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nama brand tidak boleh kosong")
    @Size(min = 2, max = 100, message = "Nama brand harus 2-100 karakter")
    @Column(nullable = false, unique = true)
    private String name;

    @Size(max = 500, message = "Deskripsi maksimal 500 karakter")
    @Column(length = 500)
    private String description;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] logo;

    public Brand() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getLogo() {
        return logo;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }

    public String getLogoBase64() {
        if (logo == null || logo.length == 0) {
            return "";
        }
        return Base64.getEncoder().encodeToString(logo);
    }
}
