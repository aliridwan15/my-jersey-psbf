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
        String[] brandNames = {"Nike", "Adidas", "Puma"};
        String[] brandDescriptions = {
            "Merek perlengkapan olahraga raksasa asal Amerika Serikat.",
            "Perusahaan multinasional asal Jerman dengan desain tiga garis paralel.",
            "Merek pakaian olahraga global asal Jerman berlogo macan kumba."
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