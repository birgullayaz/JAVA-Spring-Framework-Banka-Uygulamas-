package com.banka.repository;

import com.banka.entity.Hesap;
import com.banka.entity.Musteri;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HesapRepository extends JpaRepository<Hesap, Long> {
    Optional<Hesap> findByHesapNo(String hesapNo);
    List<Hesap> findByMusteri(Musteri musteri);
    List<Hesap> findByMusteriAndAktif(Musteri musteri, Boolean aktif);
    boolean existsByHesapNo(String hesapNo);
}
