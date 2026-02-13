# 🏦 Banka Uygulaması

Spring Boot ile geliştirilmiş banka hesap yönetim sistemi.

## 📋 Özellikler

- ✅ Müşteri Yönetimi
- ✅ Hesap Oluşturma (Vadeli, Vadesiz, Tasarruf)
- ✅ Para Çekme/Yatırma (Thread-Safe)
- ✅ Para Transferi
- ✅ İşlem Geçmişi
- ✅ Web Arayüzü (Thymeleaf)

## 🚀 Kurulum ve Çalıştırma

### Gereksinimler
- Java 17+
- Maven 3.6+

### Adımlar

1. **Projeyi klonlayın veya indirin**

2. **Proje dizinine gidin**
```bash
cd "C:\Users\Asus\OneDrive\Masaüstü\Banka uygulaması"
```

3. **Maven bağımlılıklarını yükleyin**
```bash
mvn clean install
```

4. **Uygulamayı başlatın**
```bash
mvn spring-boot:run
```

5. **Tarayıcıda açın**
```
http://localhost:8080
```

## 🗄️ Veritabanı

Uygulama varsayılan olarak **H2 In-Memory Database** kullanır. 
Uygulama kapatıldığında veriler silinir.

### PostgreSQL'e Geçiş

`application.properties` dosyasında PostgreSQL ayarlarını aktif edin:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/banka_db
spring.datasource.username=postgres
spring.datasource.password=123456
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

## 📁 Proje Yapısı

```
Banka uygulaması/
├── src/
│   ├── main/
│   │   ├── java/com/banka/
│   │   │   ├── entity/          # Veritabanı entity'leri
│   │   │   ├── repository/      # JPA Repository'ler
│   │   │   ├── service/         # İş mantığı
│   │   │   ├── controller/      # Web Controller'lar
│   │   │   └── BankaApplication.java
│   │   └── resources/
│   │       ├── templates/       # Thymeleaf şablonları
│   │       └── application.properties
│   └── test/
├── pom.xml
└── README.md
```

## 🔒 Thread-Safe İşlemler

Para çekme/yatırma ve transfer işlemleri `synchronized` metodlar ile thread-safe yapılmıştır.

## 🌐 API Endpoint'leri

- `GET /banka` - Ana sayfa
- `GET /banka/musteriler` - Müşteri listesi
- `GET /banka/musteri/yeni` - Yeni müşteri formu
- `POST /banka/musteri/kaydet` - Müşteri kaydet
- `GET /banka/musteri/{id}/hesaplar` - Müşterinin hesapları
- `POST /banka/hesap/olustur` - Hesap oluştur
- `GET /banka/hesap/{hesapNo}` - Hesap detayı
- `POST /banka/hesap/para-yatir` - Para yatır
- `POST /banka/hesap/para-cek` - Para çek
- `POST /banka/hesap/transfer` - Transfer yap

## 📝 Lisans

Bu proje eğitim amaçlıdır.

## 👨‍💻 Geliştirici

Spring Boot Banka Uygulaması
