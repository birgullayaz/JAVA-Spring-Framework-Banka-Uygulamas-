package com.banka.repository;

import com.banka.entity.Musteri;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MusteriRepository extends JpaRepository<Musteri, Long> {
    Optional<Musteri> findByTcKimlikNo(String tcKimlikNo);
    Optional<Musteri> findByEmail(String email);
    boolean existsByTcKimlikNo(String tcKimlikNo);
    boolean existsByEmail(String email);
}
