package com.banka.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "hesaplar")
public class Hesap {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Hesap numarası boş olamaz")
    @Column(unique = true, nullable = false, length = 20)
    private String hesapNo;
    
    @NotNull(message = "Müşteri boş olamaz")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "musteri_id", nullable = false)
    private Musteri musteri;
    
    @NotNull(message = "Bakiye boş olamaz")
    @DecimalMin(value = "0.0", message = "Bakiye negatif olamaz")
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal bakiye = BigDecimal.ZERO;
    
    @NotBlank(message = "Hesap tipi boş olamaz")
    @Column(nullable = false)
    private String hesapTipi; // VADELI, VADESIZ, TASARRUF
    
    @Column(nullable = false)
    private LocalDateTime olusturmaTarihi;
    
    @Column(nullable = false)
    private Boolean aktif = true;
    
    @OneToMany(mappedBy = "hesap", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Islem> islemler;
    
    @PrePersist
    protected void onCreate() {
        olusturmaTarihi = LocalDateTime.now();
        if (hesapNo == null || hesapNo.isEmpty()) {
            hesapNo = generateHesapNo();
        }
    }
    
    private String generateHesapNo() {
        return "TR" + System.currentTimeMillis();
    }
    
    // Constructors
    public Hesap() {}
    
    public Hesap(Musteri musteri, String hesapTipi) {
        this.musteri = musteri;
        this.hesapTipi = hesapTipi;
        this.bakiye = BigDecimal.ZERO;
        this.aktif = true;
    }
    
    // Para çekme metodu (synchronized)
    public synchronized boolean paraCek(BigDecimal miktar) {
        if (miktar.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        if (bakiye.compareTo(miktar) >= 0) {
            bakiye = bakiye.subtract(miktar);
            return true;
        }
        return false;
    }
    
    // Para yatırma metodu (synchronized)
    public synchronized void paraYatir(BigDecimal miktar) {
        if (miktar.compareTo(BigDecimal.ZERO) > 0) {
            bakiye = bakiye.add(miktar);
        }
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getHesapNo() {
        return hesapNo;
    }
    
    public void setHesapNo(String hesapNo) {
        this.hesapNo = hesapNo;
    }
    
    public Musteri getMusteri() {
        return musteri;
    }
    
    public void setMusteri(Musteri musteri) {
        this.musteri = musteri;
    }
    
    public BigDecimal getBakiye() {
        return bakiye;
    }
    
    public void setBakiye(BigDecimal bakiye) {
        this.bakiye = bakiye;
    }
    
    public String getHesapTipi() {
        return hesapTipi;
    }
    
    public void setHesapTipi(String hesapTipi) {
        this.hesapTipi = hesapTipi;
    }
    
    public LocalDateTime getOlusturmaTarihi() {
        return olusturmaTarihi;
    }
    
    public void setOlusturmaTarihi(LocalDateTime olusturmaTarihi) {
        this.olusturmaTarihi = olusturmaTarihi;
    }
    
    public Boolean getAktif() {
        return aktif;
    }
    
    public void setAktif(Boolean aktif) {
        this.aktif = aktif;
    }
    
    public List<Islem> getIslemler() {
        return islemler;
    }
    
    public void setIslemler(List<Islem> islemler) {
        this.islemler = islemler;
    }
}
