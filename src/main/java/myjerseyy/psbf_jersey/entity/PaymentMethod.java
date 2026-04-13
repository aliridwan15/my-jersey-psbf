package myjerseyy.psbf_jersey.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "payment_methods")
public class PaymentMethod {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Nama provider tidak boleh kosong")
    @Size(max = 100, message = "Nama provider maksimal 100 karakter")
    @Column(name = "provider_name", nullable = false, length = 100)
    private String providerName;
    
    @NotBlank(message = "Nomor rekening tidak boleh kosong")
    @Size(max = 50, message = "Nomor rekening maksimal 50 karakter")
    @Column(name = "account_number", nullable = false, length = 50)
    private String accountNumber;
    
    @NotBlank(message = "Atas nama tidak boleh kosong")
    @Size(max = 100, message = "Atas nama maksimal 100 karakter")
    @Column(name = "account_holder", nullable = false, length = 100)
    private String accountHolder;
    
    @Column(columnDefinition = "TEXT")
    private String instruction;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    public PaymentMethod() {
    }
    
    public PaymentMethod(String providerName, String accountNumber, String accountHolder) {
        this.providerName = providerName;
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.isActive = true;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getProviderName() {
        return providerName;
    }
    
    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    public String getAccountHolder() {
        return accountHolder;
    }
    
    public void setAccountHolder(String accountHolder) {
        this.accountHolder = accountHolder;
    }
    
    public String getInstruction() {
        return instruction;
    }
    
    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
