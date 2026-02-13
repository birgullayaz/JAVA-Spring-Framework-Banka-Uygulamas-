# 📧 Email Queue Sistemi

Bu projede yeni müşteri eklendiğinde otomatik olarak email bildirimi gönderen bir queue sistemi kurulmuştur.

## 🎯 Özellikler

- ✅ Yeni müşteri eklendiğinde otomatik email bildirimi
- ✅ 3 dakika gecikme ile email gönderimi
- ✅ Başarısız olursa 3'er dakika aralıklarla 5 kez tekrar deneme
- ✅ DelayQueue kullanarak gecikmeli işlem yönetimi
- ✅ Thread-safe queue işlemleri

## 📋 Nasıl Çalışır?

1. **Müşteri Ekleme**: Yeni bir müşteri eklendiğinde `BankaService.musteriOlustur()` metodu çağrılır
2. **Queue'ya Ekleme**: Email görevi `EmailQueueService` tarafından 3 dakika gecikme ile kuyruğa eklenir
3. **Email Gönderimi**: 3 dakika sonra `EmailService` email gönderir
4. **Retry Mekanizması**: Email gönderilemezse, 3'er dakika aralıklarla maksimum 5 kez tekrar denenir

## ⚙️ Yapılandırma

### Email Ayarları

`application.properties` dosyasında email ayarlarını yapılandırın:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### Gmail için App Password

Gmail kullanıyorsanız, normal şifre yerine **App Password** kullanmanız gerekir:

1. Google Hesabınıza giriş yapın
2. [Google Account Security](https://myaccount.google.com/security) sayfasına gidin
3. "2-Step Verification" aktif olmalı
4. "App passwords" bölümünden yeni bir app password oluşturun
5. Bu password'ü `application.properties` dosyasına ekleyin

## 📧 Email İçeriği

Email şu bilgileri içerir:
- Müşteri Ad Soyad
- TC Kimlik No
- Email
- Telefon
- Kayıt Tarihi

## 🔄 Retry Mekanizması

- İlk deneme: 3 dakika sonra
- Retry 1: Başarısız olursa +3 dakika (toplam 6 dakika)
- Retry 2: Başarısız olursa +3 dakika (toplam 9 dakika)
- Retry 3: Başarısız olursa +3 dakika (toplam 12 dakika)
- Retry 4: Başarısız olursa +3 dakika (toplam 15 dakika)
- Retry 5: Başarısız olursa +3 dakika (toplam 18 dakika)
- Maksimum 5 retry sonrası işlem durdurulur

## 📝 Loglar

Tüm email işlemleri loglanır:
- Email kuyruğa eklendiğinde
- Email gönderildiğinde
- Email gönderilemediğinde
- Retry işlemleri

Logları konsolda görebilirsiniz.

## 🧪 Test Etme

1. Uygulamayı başlatın
2. Yeni bir müşteri ekleyin
3. 3 dakika sonra `engineer.birgul@gmail.com` adresine email gelecektir
4. Email gönderilemezse (örneğin SMTP ayarları yanlışsa), 3'er dakika aralıklarla tekrar denenecektir

## ⚠️ Notlar

- Email gönderimi için SMTP sunucusu yapılandırması gereklidir
- Gmail için App Password kullanılmalıdır
- Queue servisi uygulama başladığında otomatik olarak başlar
- Queue servisi uygulama kapanırken düzgün şekilde durdurulur
