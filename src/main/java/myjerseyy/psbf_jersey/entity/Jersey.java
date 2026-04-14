package myjerseyy.psbf_jersey.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import myjerseyy.psbf_jersey.entity.Brand;
import myjerseyy.psbf_jersey.entity.League;
import myjerseyy.psbf_jersey.entity.ProductImage;
import myjerseyy.psbf_jersey.entity.Team;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "jerseys")
public class Jersey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nama jersey tidak boleh kosong")
    @Size(min = 2, max = 100, message = "Nama jersey harus 2-100 karakter")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Harga tidak boleh kosong")
    @Min(value = 0, message = "Harga tidak boleh kurang dari 0")
    @Column(nullable = false)
    private Double price;

    @Min(value = 0, message = "Stok tidak boleh kurang dari 0")
    @Column(nullable = false)
    private Integer stockS = 0;

    @Min(value = 0, message = "Stok tidak boleh kurang dari 0")
    @Column(nullable = false)
    private Integer stockM = 0;

    @Min(value = 0, message = "Stok tidak boleh kurang dari 0")
    @Column(nullable = false)
    private Integer stockL = 0;

    @Min(value = 0, message = "Stok tidak boleh kurang dari 0")
    @Column(nullable = false)
    private Integer stockXL = 0;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "league_id")
    private League league;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @OneToMany(mappedBy = "jersey", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("isPrimary DESC, createdAt ASC")
    private List<ProductImage> images = new ArrayList<>();

    public Jersey() {
    }

    public List<ProductImage> getImages() {
        return images;
    }

    public void setImages(List<ProductImage> images) {
        this.images = images;
    }

    public void addImage(ProductImage image) {
        images.add(image);
        image.setJersey(this);
    }

    public void removeImage(ProductImage image) {
        images.remove(image);
        image.setJersey(null);
    }

    public boolean hasImages() {
        return images != null && !images.isEmpty();
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStockS() {
        return stockS;
    }

    public void setStockS(Integer stockS) {
        this.stockS = stockS;
    }

    public Integer getStockM() {
        return stockM;
    }

    public void setStockM(Integer stockM) {
        this.stockM = stockM;
    }

    public Integer getStockL() {
        return stockL;
    }

    public void setStockL(Integer stockL) {
        this.stockL = stockL;
    }

    public Integer getStockXL() {
        return stockXL;
    }

    public void setStockXL(Integer stockXL) {
        this.stockXL = stockXL;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }
}
