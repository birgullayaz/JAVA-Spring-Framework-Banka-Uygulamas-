package com.banka.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "musteriler")
public class Musteri {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Ad boş olamaz")
    @Size(min = 2, max = 50, message = "Ad 2-50 karakter arasında olmalıdır")
    @Column(nullable = false)
    private String ad;
    
    @NotBlank(message = "Soyad boş olamaz")
    @Size(min = 2, max = 50, message = "Soyad 2-50 karakter arasında olmalıdır")
    @Column(nullable = false)
    private String soyad;
    
    @NotBlank(message = "TC Kimlik No boş olamaz")
    @Size(min = 11, max = 11, message = "TC Kimlik No 11 karakter olmalıdır")
    @Column(unique = true, nullable = false, length = 11)
    private String tcKimlikNo;
    
    @Email(message = "Geçerli bir email adresi giriniz")
    @NotBlank(message = "Email boş olamaz")
    @Column(unique = true, nullable = false)
    private String email;
    
    @NotBlank(message = "Telefon boş olamaz")
    @Column(nullable = false)
    private String telefon;
    
    @Column(nullable = false)
    private LocalDateTime kayitTarihi;
    
    @OneToMany(mappedBy = "musteri", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Hesap> hesaplar;
    
    @PrePersist
    protected void onCreate() {
        kayitTarihi = LocalDateTime.now();
    }
    
    // Constructors
    public Musteri() {}
    
    public Musteri(String ad, String soyad, String tcKimlikNo, String email, String telefon) {
        this.ad = ad;
        this.soyad = soyad;
        this.tcKimlikNo = tcKimlikNo;
        this.email = email;
        this.telefon = telefon;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getAd() {
        return ad;
    }
    
    public void setAd(String ad) {
        this.ad = ad;
    }
    
    public String getSoyad() {
        return soyad;
    }
    
    public void setSoyad(String soyad) {
        this.soyad = soyad;
    }
    
    public String getTcKimlikNo() {
        return tcKimlikNo;
    }
    
    public void setTcKimlikNo(String tcKimlikNo) {
        this.tcKimlikNo = tcKimlikNo;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getTelefon() {
        return telefon;
    }
    
    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }
    
    public LocalDateTime getKayitTarihi() {
        return kayitTarihi;
    }
    
    public void setKayitTarihi(LocalDateTime kayitTarihi) {
        this.kayitTarihi = kayitTarihi;
    }
    
    public List<Hesap> getHesaplar() {
        return hesaplar;
    }
    
    public void setHesaplar(List<Hesap> hesaplar) {
        this.hesaplar = hesaplar;
    }
    
    public String getAdSoyad() {
        return ad + " " + soyad;
    }
}
