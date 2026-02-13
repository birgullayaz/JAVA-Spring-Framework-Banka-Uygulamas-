package com.banka.service;

import com.banka.entity.Hesap;
import com.banka.entity.Islem;
import com.banka.entity.Musteri;
import com.banka.repository.HesapRepository;
import com.banka.repository.IslemRepository;
import com.banka.repository.MusteriRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BankaService {
    
    @Autowired
    private MusteriRepository musteriRepository;
    
    @Autowired
    private HesapRepository hesapRepository;
    
    @Autowired
    private IslemRepository islemRepository;
    
    @Autowired
    private EmailQueueService emailQueueService;
    
    // Müşteri İşlemleri
    public Musteri musteriOlustur(Musteri musteri) {
        if (musteriRepository.existsByTcKimlikNo(musteri.getTcKimlikNo())) {
            throw new RuntimeException("Bu TC Kimlik No ile kayıtlı müşteri zaten var!");
        }
        if (musteriRepository.existsByEmail(musteri.getEmail())) {
            throw new RuntimeException("Bu email ile kayıtlı müşteri zaten var!");
        }
        
        Musteri kaydedilenMusteri = musteriRepository.save(musteri);
        
        // Email bildirimi kuyruğa ekle (3 dakika sonra gönderilecek)
        String emailSubject = "Yeni Müşteri Kaydı";
        String emailBody = String.format(
            "Yeni bir müşteri sisteme kaydedildi:\n\n" +
            "Ad Soyad: %s\n" +
            "TC Kimlik No: %s\n" +
            "Email: %s\n" +
            "Telefon: %s\n" +
            "Kayıt Tarihi: %s\n\n" +
            "Bu email 3 dakika gecikme ile gönderilmiştir.",
            kaydedilenMusteri.getAdSoyad(),
            kaydedilenMusteri.getTcKimlikNo(),
            kaydedilenMusteri.getEmail(),
            kaydedilenMusteri.getTelefon(),
            kaydedilenMusteri.getKayitTarihi()
        );
        
        emailQueueService.enqueueEmail("engineer.birgul@gmail.com", emailSubject, emailBody, 3);
        
        return kaydedilenMusteri;
    }
    
    public List<Musteri> tumMusterileriGetir() {
        return musteriRepository.findAll();
    }
    
    public Optional<Musteri> musteriGetir(Long id) {
        return musteriRepository.findById(id);
    }
    
    // Hesap İşlemleri
    public Hesap hesapOlustur(Long musteriId, String hesapTipi) {
        Musteri musteri = musteriRepository.findById(musteriId)
            .orElseThrow(() -> new RuntimeException("Müşteri bulunamadı!"));
        
        Hesap hesap = new Hesap(musteri, hesapTipi);
        return hesapRepository.save(hesap);
    }
    
    public List<Hesap> musterininHesaplariniGetir(Long musteriId) {
        Musteri musteri = musteriRepository.findById(musteriId)
            .orElseThrow(() -> new RuntimeException("Müşteri bulunamadı!"));
        return hesapRepository.findByMusteriAndAktif(musteri, true);
    }
    
    public Optional<Hesap> hesapGetir(String hesapNo) {
        return hesapRepository.findByHesapNo(hesapNo);
    }
    
    // Para İşlemleri (Synchronized)
    public synchronized boolean paraCek(String hesapNo, BigDecimal miktar, String aciklama) {
        Hesap hesap = hesapRepository.findByHesapNo(hesapNo)
            .orElseThrow(() -> new RuntimeException("Hesap bulunamadı!"));
        
        if (!hesap.getAktif()) {
            throw new RuntimeException("Hesap aktif değil!");
        }
        
        if (hesap.paraCek(miktar)) {
            hesapRepository.save(hesap);
            
            Islem islem = new Islem(hesap, "PARA_CEK", miktar, aciklama);
            islem.setBakiyeSonrasi(hesap.getBakiye());
            islemRepository.save(islem);
            
            return true;
        }
        return false;
    }
    
    public synchronized void paraYatir(String hesapNo, BigDecimal miktar, String aciklama) {
        Hesap hesap = hesapRepository.findByHesapNo(hesapNo)
            .orElseThrow(() -> new RuntimeException("Hesap bulunamadı!"));
        
        if (!hesap.getAktif()) {
            throw new RuntimeException("Hesap aktif değil!");
        }
        
        hesap.paraYatir(miktar);
        hesapRepository.save(hesap);
        
        Islem islem = new Islem(hesap, "PARA_YATIR", miktar, aciklama);
        islem.setBakiyeSonrasi(hesap.getBakiye());
        islemRepository.save(islem);
    }
    
    public synchronized boolean transfer(String gonderenHesapNo, String alanHesapNo, BigDecimal miktar, String aciklama) {
        Hesap gonderenHesap = hesapRepository.findByHesapNo(gonderenHesapNo)
            .orElseThrow(() -> new RuntimeException("Gönderen hesap bulunamadı!"));
        
        Hesap alanHesap = hesapRepository.findByHesapNo(alanHesapNo)
            .orElseThrow(() -> new RuntimeException("Alan hesap bulunamadı!"));
        
        if (!gonderenHesap.getAktif() || !alanHesap.getAktif()) {
            throw new RuntimeException("Hesaplardan biri aktif değil!");
        }
        
        if (gonderenHesap.paraCek(miktar)) {
            hesapRepository.save(gonderenHesap);
            
            alanHesap.paraYatir(miktar);
            hesapRepository.save(alanHesap);
            
            // İşlem kayıtları
            Islem cekmeIslemi = new Islem(gonderenHesap, "TRANSFER", miktar, 
                "Transfer - " + alanHesapNo + " - " + aciklama);
            cekmeIslemi.setBakiyeSonrasi(gonderenHesap.getBakiye());
            islemRepository.save(cekmeIslemi);
            
            Islem yatirmaIslemi = new Islem(alanHesap, "TRANSFER", miktar, 
                "Transfer - " + gonderenHesapNo + " - " + aciklama);
            yatirmaIslemi.setBakiyeSonrasi(alanHesap.getBakiye());
            islemRepository.save(yatirmaIslemi);
            
            return true;
        }
        return false;
    }
    
    // İşlem Geçmişi
    public List<Islem> hesapIslemGecmisi(String hesapNo) {
        Hesap hesap = hesapRepository.findByHesapNo(hesapNo)
            .orElseThrow(() -> new RuntimeException("Hesap bulunamadı!"));
        return islemRepository.findByHesapOrderByIslemTarihiDesc(hesap);
    }
}
