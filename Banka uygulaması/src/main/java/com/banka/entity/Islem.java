package com.banka.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "islemler")
public class Islem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Hesap boş olamaz")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hesap_id", nullable = false)
    private Hesap hesap;
    
    @NotNull(message = "İşlem tipi boş olamaz")
    @Column(nullable = false)
    private String islemTipi; // PARA_CEK, PARA_YATIR, TRANSFER
    
    @NotNull(message = "Miktar boş olamaz")
    @DecimalMin(value = "0.01", message = "Miktar 0'dan büyük olmalıdır")
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal miktar;
    
    @Column(precision = 19, scale = 2)
    private BigDecimal bakiyeSonrasi;
    
    @Column(length = 500)
    private String aciklama;
    
    @Column(nullable = false)
    private LocalDateTime islemTarihi;
    
    @PrePersist
    protected void onCreate() {
        islemTarihi = LocalDateTime.now();
        if (bakiyeSonrasi == null && hesap != null) {
            bakiyeSonrasi = hesap.getBakiye();
        }
    }
    
    // Constructors
    public Islem() {}
    
    public Islem(Hesap hesap, String islemTipi, BigDecimal miktar, String aciklama) {
        this.hesap = hesap;
        this.islemTipi = islemTipi;
        this.miktar = miktar;
        this.aciklama = aciklama;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Hesap getHesap() {
        return hesap;
    }
    
    public void setHesap(Hesap hesap) {
        this.hesap = hesap;
    }
    
    public String getIslemTipi() {
        return islemTipi;
    }
    
    public void setIslemTipi(String islemTipi) {
        this.islemTipi = islemTipi;
    }
    
    public BigDecimal getMiktar() {
        return miktar;
    }
    
    public void setMiktar(BigDecimal miktar) {
        this.miktar = miktar;
    }
    
    public BigDecimal getBakiyeSonrasi() {
        return bakiyeSonrasi;
    }
    
    public void setBakiyeSonrasi(BigDecimal bakiyeSonrasi) {
        this.bakiyeSonrasi = bakiyeSonrasi;
    }
    
    public String getAciklama() {
        return aciklama;
    }
    
    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }
    
    public LocalDateTime getIslemTarihi() {
        return islemTarihi;
    }
    
    public void setIslemTarihi(LocalDateTime islemTarihi) {
        this.islemTarihi = islemTarihi;
    }
}
