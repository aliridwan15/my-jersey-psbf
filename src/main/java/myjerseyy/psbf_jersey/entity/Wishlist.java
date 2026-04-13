package myjerseyy.psbf_jersey.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "wishlists")
public class Wishlist {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "jersey_id", nullable = false)
    private Jersey jersey;
    
    @Column(name = "added_date")
    private LocalDateTime addedDate;
    
    public Wishlist() {
    }
    
    public Wishlist(User user, Jersey jersey) {
        this.user = user;
        this.jersey = jersey;
        this.addedDate = LocalDateTime.now();
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
    
    public Jersey getJersey() {
        return jersey;
    }
    
    public void setJersey(Jersey jersey) {
        this.jersey = jersey;
    }
    
    public LocalDateTime getAddedDate() {
        return addedDate;
    }
    
    public void setAddedDate(LocalDateTime addedDate) {
        this.addedDate = addedDate;
    }
}
