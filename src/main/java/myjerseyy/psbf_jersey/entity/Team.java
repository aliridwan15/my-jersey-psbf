package myjerseyy.psbf_jersey.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "teams")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nama tim tidak boleh kosong")
    @Size(min = 2, max = 100, message = "Nama tim harus 2-100 karakter")
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String logo;

    @ManyToOne
    @JoinColumn(name = "league_id")
    private League league;

    public Team() {
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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }
}