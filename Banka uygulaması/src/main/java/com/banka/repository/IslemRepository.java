package com.banka.repository;

import com.banka.entity.Hesap;
import com.banka.entity.Islem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IslemRepository extends JpaRepository<Islem, Long> {
    List<Islem> findByHesapOrderByIslemTarihiDesc(Hesap hesap);
    List<Islem> findByHesapAndIslemTipiOrderByIslemTarihiDesc(Hesap hesap, String islemTipi);
}
