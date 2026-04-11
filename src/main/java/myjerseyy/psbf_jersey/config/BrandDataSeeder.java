package myjerseyy.psbf_jersey.config;

import myjerseyy.psbf_jersey.entity.Brand;
import myjerseyy.psbf_jersey.repository.BrandRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BrandDataSeeder implements CommandLineRunner {

    private final BrandRepository brandRepository;

    public BrandDataSeeder(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Override
    public void run(String... args) {
        String[] brandNames = {
            "Nike", "Adidas", "Puma", "Castore", "Macron", "Kappa", "Umbro",
            "Mizuno", "New Balance", "Under Armour", "Hummel", "Lotto", "Erreà", "Joma"
        };
        String[] brandDescriptions = {
            "Merek perlengkapan olahraga raksasaasal Amerika Serikat.",
            "Perusahaan multinasional asal Jerman dengan desain tiga garis paralel.",
            "Merek pakaian olahraga globalasal Jerman berlogo macan kumba.",
            "Produsen pakaian olahraga premium asal Inggris.",
            "Perusahaan perlengkapan olahragaasal Italia.",
            "Merek pakaian olahraga Italia dengan logo ikonik Omini.",
            "Pemasok perlengkapan olahraga legendaris asal Inggris.",
            "Didirikan oleh Rihachi Mizuno di Osaka, Jepang pada tahun 1906.",
            "Berdiri tahun 1906 di Boston, AS.",
            "Didirikan tahun 1996 oleh Kevin Plank di Maryland, AS.",
            "Brand asal Denmark yang didirikan di Hamburg pada 1923.",
            "Berdiri tahun 1973 di Trevignano, Italia.",
            "Didirikan pada 1988 di Parma, Italia.",
            "Merek asal Spanyol yang didirikan tahun 1965."
        };
        
        for (int i = 0; i < brandNames.length; i++) {
            if (brandRepository.findByName(brandNames[i]).isEmpty()) {
                Brand brand = new Brand();
                brand.setName(brandNames[i]);
                brand.setDescription(brandDescriptions[i]);
                brand.setLogo(null);
                brandRepository.save(brand);
            }
        }
        
        System.out.println("=== BrandDataSeeder: Brands seeded completed ===");
    }
}