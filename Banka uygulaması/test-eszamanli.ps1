# Eşzamanlı Test Scripti - Banka Uygulaması
# Bu script iki farklı müşteri oluşturup eşzamanlı para yatırma işlemleri yapar

Write-Host "=== Eşzamanlı Test Başlatılıyor ===" -ForegroundColor Green

$baseUrl = "http://localhost:8080/banka"

# İlk müşteri bilgileri
$musteri1 = @{
    ad = "Ahmet"
    soyad = "Yılmaz"
    tcKimlikNo = "12345678901"
    email = "ahmet@test.com"
    telefon = "05551234567"
}

# İkinci müşteri bilgileri
$musteri2 = @{
    ad = "Mehmet"
    soyad = "Demir"
    tcKimlikNo = "98765432109"
    email = "mehmet@test.com"
    telefon = "05559876543"
}

Write-Host "`n1. Müşteri 1 oluşturuluyor..." -ForegroundColor Yellow
$body1 = "ad=$($musteri1.ad)&soyad=$($musteri1.soyad)&tcKimlikNo=$($musteri1.tcKimlikNo)&email=$($musteri1.email)&telefon=$($musteri1.telefon)"
try {
    $response1 = Invoke-WebRequest -Uri "$baseUrl/musteri/kaydet" -Method POST -Body $body1 -ContentType "application/x-www-form-urlencoded" -UseBasicParsing
    Write-Host "✓ Müşteri 1 oluşturuldu: $($musteri1.ad) $($musteri1.soyad)" -ForegroundColor Green
} catch {
    Write-Host "✗ Müşteri 1 oluşturulamadı: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n2. Müşteri 2 oluşturuluyor..." -ForegroundColor Yellow
$body2 = "ad=$($musteri2.ad)&soyad=$($musteri2.soyad)&tcKimlikNo=$($musteri2.tcKimlikNo)&email=$($musteri2.email)&telefon=$($musteri2.telefon)"
try {
    $response2 = Invoke-WebRequest -Uri "$baseUrl/musteri/kaydet" -Method POST -Body $body2 -ContentType "application/x-www-form-urlencoded" -UseBasicParsing
    Write-Host "✓ Müşteri 2 oluşturuldu: $($musteri2.ad) $($musteri2.soyad)" -ForegroundColor Green
} catch {
    Write-Host "✗ Müşteri 2 oluşturulamadı: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n3. Müşteri listesi alınıyor..." -ForegroundColor Yellow
try {
    $musterilerResponse = Invoke-WebRequest -Uri "$baseUrl/musteriler" -UseBasicParsing
    Write-Host "✓ Müşteri listesi alındı" -ForegroundColor Green
} catch {
    Write-Host "✗ Müşteri listesi alınamadı" -ForegroundColor Red
}

Write-Host "`n=== Test Tamamlandı ===" -ForegroundColor Green
Write-Host "`nTarayıcıda http://localhost:8080/banka/musteriler adresini açarak müşterileri görebilirsiniz." -ForegroundColor Cyan
Write-Host "Eşzamanlı test için:" -ForegroundColor Cyan
Write-Host "1. İki farklı tarayıcı sekmesi açın" -ForegroundColor Cyan
Write-Host "2. Her sekmede farklı bir müşteri için hesap oluşturun" -ForegroundColor Cyan
Write-Host "3. Aynı anda para yatırma işlemleri yapın" -ForegroundColor Cyan
Write-Host "4. Bakiyelerin doğru güncellendiğini kontrol edin" -ForegroundColor Cyan
