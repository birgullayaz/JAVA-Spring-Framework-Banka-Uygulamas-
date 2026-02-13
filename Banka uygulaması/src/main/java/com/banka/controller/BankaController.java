package com.banka.controller;

import com.banka.entity.Hesap;
import com.banka.entity.Musteri;
import com.banka.service.BankaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/banka")
public class BankaController {
    
    @Autowired
    private BankaService bankaService;
    
    @GetMapping
    public String anaSayfa(Model model) {
        return "index";
    }
    
    @GetMapping("/musteriler")
    public String musteriler(Model model) {
        List<Musteri> musteriler = bankaService.tumMusterileriGetir();
        model.addAttribute("musteriler", musteriler);
        return "musteriler";
    }
    
    @GetMapping("/musteri/yeni")
    public String yeniMusteriForm(Model model) {
        model.addAttribute("musteri", new Musteri());
        return "musteri-form";
    }
    
    @PostMapping("/musteri/kaydet")
    public String musteriKaydet(@ModelAttribute Musteri musteri, RedirectAttributes redirectAttributes) {
        try {
            bankaService.musteriOlustur(musteri);
            redirectAttributes.addFlashAttribute("success", "Müşteri başarıyla kaydedildi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/banka/musteriler";
    }
    
    @GetMapping("/musteri/{id}/hesaplar")
    public String musterininHesaplari(@PathVariable Long id, Model model) {
        try {
            Musteri musteri = bankaService.musteriGetir(id)
                .orElseThrow(() -> new RuntimeException("Müşteri bulunamadı!"));
            List<Hesap> hesaplar = bankaService.musterininHesaplariniGetir(id);
            model.addAttribute("musteri", musteri);
            model.addAttribute("hesaplar", hesaplar);
            return "hesaplar";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/banka/musteriler";
        }
    }
    
    @PostMapping("/hesap/olustur")
    public String hesapOlustur(@RequestParam Long musteriId, 
                                @RequestParam String hesapTipi,
                                RedirectAttributes redirectAttributes) {
        try {
            bankaService.hesapOlustur(musteriId, hesapTipi);
            redirectAttributes.addFlashAttribute("success", "Hesap başarıyla oluşturuldu!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/banka/musteri/" + musteriId + "/hesaplar";
    }
    
    @GetMapping("/hesap/{hesapNo}")
    public String hesapDetay(@PathVariable String hesapNo, Model model) {
        try {
            Hesap hesap = bankaService.hesapGetir(hesapNo)
                .orElseThrow(() -> new RuntimeException("Hesap bulunamadı!"));
            model.addAttribute("hesap", hesap);
            model.addAttribute("islemler", bankaService.hesapIslemGecmisi(hesapNo));
            return "hesap-detay";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/banka/musteriler";
        }
    }
    
    @PostMapping("/hesap/para-yatir")
    public String paraYatir(@RequestParam String hesapNo,
                           @RequestParam BigDecimal miktar,
                           @RequestParam(required = false) String aciklama,
                           RedirectAttributes redirectAttributes) {
        try {
            bankaService.paraYatir(hesapNo, miktar, aciklama != null ? aciklama : "Para yatırma");
            redirectAttributes.addFlashAttribute("success", "Para başarıyla yatırıldı!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/banka/hesap/" + hesapNo;
    }
    
    @PostMapping("/hesap/para-cek")
    public String paraCek(@RequestParam String hesapNo,
                         @RequestParam BigDecimal miktar,
                         @RequestParam(required = false) String aciklama,
                         RedirectAttributes redirectAttributes) {
        try {
            if (bankaService.paraCek(hesapNo, miktar, aciklama != null ? aciklama : "Para çekme")) {
                redirectAttributes.addFlashAttribute("success", "Para başarıyla çekildi!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Yetersiz bakiye!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/banka/hesap/" + hesapNo;
    }
    
    @PostMapping("/hesap/transfer")
    public String transfer(@RequestParam String gonderenHesapNo,
                          @RequestParam String alanHesapNo,
                          @RequestParam BigDecimal miktar,
                          @RequestParam(required = false) String aciklama,
                          RedirectAttributes redirectAttributes) {
        try {
            if (bankaService.transfer(gonderenHesapNo, alanHesapNo, miktar, 
                aciklama != null ? aciklama : "Transfer")) {
                redirectAttributes.addFlashAttribute("success", "Transfer başarıyla tamamlandı!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Yetersiz bakiye!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/banka/hesap/" + gonderenHesapNo;
    }
}
