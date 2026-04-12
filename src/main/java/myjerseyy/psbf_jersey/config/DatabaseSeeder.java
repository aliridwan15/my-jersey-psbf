package myjerseyy.psbf_jersey.config;

import myjerseyy.psbf_jersey.entity.Address;
import myjerseyy.psbf_jersey.entity.Brand;
import myjerseyy.psbf_jersey.entity.Faq;
import myjerseyy.psbf_jersey.entity.Jersey;
import myjerseyy.psbf_jersey.entity.League;
import myjerseyy.psbf_jersey.entity.Order;
import myjerseyy.psbf_jersey.entity.OrderItem;
import myjerseyy.psbf_jersey.entity.OrderStatus;
import myjerseyy.psbf_jersey.entity.PromoCode;
import myjerseyy.psbf_jersey.entity.Shipment;
import myjerseyy.psbf_jersey.entity.Team;
import myjerseyy.psbf_jersey.entity.User;
import myjerseyy.psbf_jersey.repository.AddressRepository;
import myjerseyy.psbf_jersey.repository.BrandRepository;
import myjerseyy.psbf_jersey.repository.FaqRepository;
import myjerseyy.psbf_jersey.repository.JerseyRepository;
import myjerseyy.psbf_jersey.repository.LeagueRepository;
import myjerseyy.psbf_jersey.repository.OrderRepository;
import myjerseyy.psbf_jersey.repository.PromoCodeRepository;
import myjerseyy.psbf_jersey.repository.ShipmentRepository;
import myjerseyy.psbf_jersey.repository.TeamRepository;
import myjerseyy.psbf_jersey.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final JerseyRepository jerseyRepository;
    private final OrderRepository orderRepository;
    private final LeagueRepository leagueRepository;
    private final TeamRepository teamRepository;
    private final BrandRepository brandRepository;
    private final PromoCodeRepository promoCodeRepository;
    private final ShipmentRepository shipmentRepository;
    private final AddressRepository addressRepository;
    private final FaqRepository faqRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(UserRepository userRepository, 
                         JerseyRepository jerseyRepository, OrderRepository orderRepository,
                         LeagueRepository leagueRepository, TeamRepository teamRepository,
                         BrandRepository brandRepository, PromoCodeRepository promoCodeRepository,
                         ShipmentRepository shipmentRepository, AddressRepository addressRepository,
                         FaqRepository faqRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jerseyRepository = jerseyRepository;
        this.orderRepository = orderRepository;
        this.leagueRepository = leagueRepository;
        this.teamRepository = teamRepository;
        this.brandRepository = brandRepository;
        this.promoCodeRepository = promoCodeRepository;
        this.shipmentRepository = shipmentRepository;
        this.addressRepository = addressRepository;
        this.faqRepository = faqRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        migratePlainTextPasswords();
        
        if (promoCodeRepository.count() == 0) {
            seedPromoCodes();
        }
        
        if (faqRepository.count() == 0) {
            seedFaqs();
        }
        
        if (userRepository.count() == 0) {
            seedUsers();
            seedBrands();
            seedLeagues();
            seedTeams();
            seedJerseys();
            seedOrders();
        } else if (orderRepository.count() == 0) {
            seedOrders();
        }
    }

    private void migratePlainTextPasswords() {
        userRepository.findByUsername("admin").ifPresent(user -> {
            if (!user.getPassword().startsWith("$2a$")) {
                user.setPassword(passwordEncoder.encode("admin123"));
                userRepository.save(user);
                System.out.println("=== Database Seeder: Password admin berhasil di-hash ===");
            }
        });
    }

    private void seedUsers() {
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole("ADMIN");
        admin.setName("Administrator");
        admin.setAddress("Jakarta Selatan");
        userRepository.save(admin);

        User budi = new User();
        budi.setUsername("budi_sport");
        budi.setPassword(passwordEncoder.encode("password123"));
        budi.setRole("CUSTOMER");
        budi.setName("Budi Santoso");
        budi.setAddress("Bandung");
        userRepository.save(budi);

        User sarah = new User();
        sarah.setUsername("sarah_jersey");
        sarah.setPassword(passwordEncoder.encode("jersey2024"));
        sarah.setRole("CUSTOMER");
        sarah.setName("Sarah Putri");
        sarah.setAddress("Surabaya");
        userRepository.save(sarah);

        User andi = new User();
        andi.setUsername("andi_bola");
        andi.setPassword(passwordEncoder.encode("bola99"));
        andi.setRole("CUSTOMER");
        andi.setName("Andi Wijaya");
        andi.setAddress("Medan");
        userRepository.save(andi);

        User dewi = new User();
        dewi.setUsername("dewi_store");
        dewi.setPassword(passwordEncoder.encode("store123"));
        dewi.setRole("CUSTOMER");
        dewi.setName("Dewi Lestari");
        dewi.setAddress("Yogyakarta");
        userRepository.save(dewi);

        User rudi = new User();
        rudi.setUsername("rudi_futsal");
        rudi.setPassword(passwordEncoder.encode("futsal123"));
        rudi.setRole("CUSTOMER");
        rudi.setName("Rudi Hermawan");
        rudi.setAddress("Semarang");
        userRepository.save(rudi);

        User siti = new User();
        siti.setUsername("siti_jersey");
        siti.setPassword(passwordEncoder.encode("jersey123"));
        siti.setRole("CUSTOMER");
        siti.setName("Siti Aminah");
        siti.setAddress("Makassar");
        userRepository.save(siti);

        User ahmad = new User();
        ahmad.setUsername("ahmad_bola");
        ahmad.setPassword(passwordEncoder.encode("bola123"));
        ahmad.setRole("CUSTOMER");
        ahmad.setName("Ahmad Fauzi");
        ahmad.setAddress("Palembang");
        userRepository.save(ahmad);

        User lia = new User();
        lia.setUsername("lia_sport");
        lia.setPassword(passwordEncoder.encode("sport123"));
        lia.setRole("CUSTOMER");
        lia.setName("Lia Permata");
        lia.setAddress("Padang");
        userRepository.save(lia);

        User bayu = new User();
        bayu.setUsername("bayu_galery");
        bayu.setPassword(passwordEncoder.encode("galery123"));
        bayu.setRole("CUSTOMER");
        bayu.setName("Bayu Setiawan");
        bayu.setAddress("Denpasar");
        userRepository.save(bayu);

        User nisa = new User();
        nisa.setUsername("nisa_store");
        nisa.setPassword(passwordEncoder.encode("store456"));
        nisa.setRole("CUSTOMER");
        nisa.setName("Nisa Fauziah");
        nisa.setAddress("Bogor");
        userRepository.save(nisa);

        User dika = new User();
        dika.setUsername("dika_soccer");
        dika.setPassword(passwordEncoder.encode("soccer123"));
        dika.setRole("CUSTOMER");
        dika.setName("Dika Pratama");
        dika.setAddress("Malang");
        userRepository.save(dika);

        User vera = new User();
        vera.setUsername("vera_shop");
        vera.setPassword(passwordEncoder.encode("shop123"));
        vera.setRole("CUSTOMER");
        vera.setName("Vera Wulandari");
        vera.setAddress("Solo");
        userRepository.save(vera);

        User tono = new User();
        tono.setUsername("tono_bajuku");
        tono.setPassword(passwordEncoder.encode("baju123"));
        tono.setRole("CUSTOMER");
        tono.setName("Tono Susilo");
        tono.setAddress("Samarinda");
        userRepository.save(tono);

        User rina = new User();
        rina.setUsername("rina_grosir");
        rina.setPassword(passwordEncoder.encode("grosir123"));
        rina.setRole("CUSTOMER");
        rina.setName("Rina Marlina");
        rina.setAddress("Pekanbaru");
        userRepository.save(rina);

        System.out.println("=== Database Seeder: 16 Users berhasil diinsert ===");
    }

    private void seedBrands() {
        if (brandRepository.count() > 0) {
            return;
        }

        String[] brandNames = {"Nike", "Adidas", "Puma", "Castore", "Macron", "Kappa", "Umbro"};
        String[] brandDescriptions = {
            "Merek perlengkapan olahraga raksasa asal Amerika Serikat.",
            "Perusahaan multinasional asal Jerman dengan desain tiga garis paralel.",
            "Merek pakaian olahraga global asal Jerman berlogo macan kumba.",
            "Produsen pakaian olahraga premium asal Inggris.",
            "Perusahaan perlengkapan olahraga asal Italia.",
            "Merek pakaian olahraga Italia dengan logo ikonik Omini.",
            "Pemasok perlengkapan olahraga legendaris asal Inggris."
        };

        for (int i = 0; i < brandNames.length; i++) {
            Brand brand = new Brand();
            brand.setName(brandNames[i]);
            brand.setDescription(brandDescriptions[i]);
            brandRepository.save(brand);
        }

        System.out.println("=== Database Seeder: 7 Brands berhasil diinsert ===");
    }

    private void seedLeagues() {
        if (leagueRepository.count() > 0) {
            return;
        }

        League laLiga = new League();
        laLiga.setName("La Liga");
        laLiga.setCountry("Spanyol");
        leagueRepository.save(laLiga);

        League premierLeague = new League();
        premierLeague.setName("Premier League");
        premierLeague.setCountry("Inggris");
        leagueRepository.save(premierLeague);

        League bundesliga = new League();
        bundesliga.setName("Bundesliga");
        bundesliga.setCountry("Jerman");
        leagueRepository.save(bundesliga);

        League serieA = new League();
        serieA.setName("Serie A");
        serieA.setCountry("Italia");
        leagueRepository.save(serieA);

        League ligue1 = new League();
        ligue1.setName("Ligue 1");
        ligue1.setCountry("Prancis");
        leagueRepository.save(ligue1);

        System.out.println("=== Database Seeder: 5 Leagues berhasil diinsert ===");
    }

    private void seedTeams() {
        if (teamRepository.count() > 0) {
            return;
        }

        var leagues = leagueRepository.findAll();
        var laLiga = leagues.stream().filter(l -> "La Liga".equals(l.getName())).findFirst().orElse(null);
        var premierLeague = leagues.stream().filter(l -> "Premier League".equals(l.getName())).findFirst().orElse(null);
        var bundesliga = leagues.stream().filter(l -> "Bundesliga".equals(l.getName())).findFirst().orElse(null);
        var serieA = leagues.stream().filter(l -> "Serie A".equals(l.getName())).findFirst().orElse(null);
        var ligue1 = leagues.stream().filter(l -> "Ligue 1".equals(l.getName())).findFirst().orElse(null);

        if (laLiga != null) {
            saveTeam("Real Madrid", laLiga);
            saveTeam("Barcelona", laLiga);
            saveTeam("Atletico Madrid", laLiga);
        }
        if (premierLeague != null) {
            saveTeam("Manchester United", premierLeague);
            saveTeam("Liverpool", premierLeague);
            saveTeam("Chelsea", premierLeague);
            saveTeam("Arsenal", premierLeague);
            saveTeam("Manchester City", premierLeague);
            saveTeam("Tottenham Hotspur", premierLeague);
            saveTeam("Newcastle United", premierLeague);
        }
        if (bundesliga != null) {
            saveTeam("Bayern Munich", bundesliga);
            saveTeam("Borussia Dortmund", bundesliga);
            saveTeam("RB Leipzig", bundesliga);
            saveTeam("Bayer Leverkusen", bundesliga);
        }
        if (serieA != null) {
            saveTeam("AC Milan", serieA);
            saveTeam("Inter Milan", serieA);
            saveTeam("Juventus", serieA);
            saveTeam("AS Roma", serieA);
            saveTeam("Napoli", serieA);
        }
        if (ligue1 != null) {
            saveTeam("Paris Saint-Germain", ligue1);
            saveTeam("Olympique Marseille", ligue1);
            saveTeam("Olympique Lyon", ligue1);
            saveTeam("AS Monaco", ligue1);
        }

        System.out.println("=== Database Seeder: 25 Teams berhasil diinsert ===");
    }

    private void saveTeam(String name, League league) {
        Team team = new Team();
        team.setName(name);
        team.setLeague(league);
        teamRepository.save(team);
    }

    private void seedJerseys() {
        if (jerseyRepository.count() > 0) {
            return;
        }

        var brands = brandRepository.findAll();
        var teams = teamRepository.findAll();
        var leagues = leagueRepository.findAll();

        var nike = brands.stream().filter(b -> "Nike".equals(b.getName())).findFirst().orElse(null);
        var adidas = brands.stream().filter(b -> "Adidas".equals(b.getName())).findFirst().orElse(null);
        var puma = brands.stream().filter(b -> "Puma".equals(b.getName())).findFirst().orElse(null);

        var realMadrid = teams.stream().filter(t -> "Real Madrid".equals(t.getName())).findFirst().orElse(null);
        var barcelona = teams.stream().filter(t -> "Barcelona".equals(t.getName())).findFirst().orElse(null);
        var manUnited = teams.stream().filter(t -> "Manchester United".equals(t.getName())).findFirst().orElse(null);
        var liverpool = teams.stream().filter(t -> "Liverpool".equals(t.getName())).findFirst().orElse(null);
        var chelsea = teams.stream().filter(t -> "Chelsea".equals(t.getName())).findFirst().orElse(null);
        var arsenal = teams.stream().filter(t -> "Arsenal".equals(t.getName())).findFirst().orElse(null);
        var manCity = teams.stream().filter(t -> "Manchester City".equals(t.getName())).findFirst().orElse(null);
        var tottenham = teams.stream().filter(t -> "Tottenham Hotspur".equals(t.getName())).findFirst().orElse(null);
        var bayern = teams.stream().filter(t -> "Bayern Munich".equals(t.getName())).findFirst().orElse(null);
        var dortmund = teams.stream().filter(t -> "Borussia Dortmund".equals(t.getName())).findFirst().orElse(null);
        var acMilan = teams.stream().filter(t -> "AC Milan".equals(t.getName())).findFirst().orElse(null);
        var interMilan = teams.stream().filter(t -> "Inter Milan".equals(t.getName())).findFirst().orElse(null);
        var juve = teams.stream().filter(t -> "Juventus".equals(t.getName())).findFirst().orElse(null);
        var psg = teams.stream().filter(t -> "Paris Saint-Germain".equals(t.getName())).findFirst().orElse(null);

        var laLiga = leagues.stream().filter(l -> "La Liga".equals(l.getName())).findFirst().orElse(null);
        var premierLeague = leagues.stream().filter(l -> "Premier League".equals(l.getName())).findFirst().orElse(null);
        var bundesliga = leagues.stream().filter(l -> "Bundesliga".equals(l.getName())).findFirst().orElse(null);
        var serieA = leagues.stream().filter(l -> "Serie A".equals(l.getName())).findFirst().orElse(null);
        var ligue1 = leagues.stream().filter(l -> "Ligue 1".equals(l.getName())).findFirst().orElse(null);

        if (realMadrid != null && laLiga != null && adidas != null) {
            saveJersey("Real Madrid Home 2024/2025", 899000.0, 10, 8, 5, 2, adidas, realMadrid, laLiga);
            saveJersey("Real Madrid Away 2024/2025", 899000.0, 8, 6, 4, 2, adidas, realMadrid, laLiga);
        }
        if (barcelona != null && laLiga != null && nike != null) {
            saveJersey("Barcelona Home 2024/2025", 875000.0, 12, 10, 5, 3, nike, barcelona, laLiga);
            saveJersey("Barcelona Away 2024/2025", 875000.0, 7, 5, 4, 2, nike, barcelona, laLiga);
        }
        if (manUnited != null && premierLeague != null && adidas != null) {
            saveJersey("Man United Home 2024/2025", 950000.0, 9, 7, 4, 2, adidas, manUnited, premierLeague);
            saveJersey("Man United Third 2024/2025", 950000.0, 6, 4, 3, 2, adidas, manUnited, premierLeague);
        }
        if (liverpool != null && premierLeague != null && nike != null) {
            saveJersey("Liverpool Home 2024/2025", 925000.0, 11, 9, 5, 3, nike, liverpool, premierLeague);
            saveJersey("Liverpool Away 2024/2025", 925000.0, 8, 6, 4, 2, nike, liverpool, premierLeague);
        }
        if (chelsea != null && premierLeague != null && nike != null) {
            saveJersey("Chelsea Home 2024/2025", 899000.0, 10, 7, 5, 2, nike, chelsea, premierLeague);
        }
        if (arsenal != null && premierLeague != null && adidas != null) {
            saveJersey("Arsenal Home 2024/2025", 875000.0, 10, 8, 5, 3, adidas, arsenal, premierLeague);
            saveJersey("Arsenal Away 2024/2025", 875000.0, 6, 5, 3, 2, adidas, arsenal, premierLeague);
        }
        if (manCity != null && premierLeague != null && puma != null) {
            saveJersey("Man City Home 2024/2025", 950000.0, 11, 9, 5, 3, puma, manCity, premierLeague);
        }
        if (tottenham != null && premierLeague != null && nike != null) {
            saveJersey("Tottenham Home 2024/2025", 875000.0, 8, 6, 4, 2, nike, tottenham, premierLeague);
        }
        if (bayern != null && bundesliga != null && adidas != null) {
            saveJersey("Bayern Munich Home 2024/2025", 950000.0, 9, 7, 4, 2, adidas, bayern, bundesliga);
        }
        if (dortmund != null && bundesliga != null && puma != null) {
            saveJersey("Dortmund Home 2024/2025", 850000.0, 8, 6, 4, 2, puma, dortmund, bundesliga);
        }
        if (acMilan != null && serieA != null && puma != null) {
            saveJersey("AC Milan Home 2024/2025", 925000.0, 7, 5, 4, 2, puma, acMilan, serieA);
        }
        if (interMilan != null && serieA != null && nike != null) {
            saveJersey("Inter Milan Home 2024/2025", 899000.0, 8, 6, 4, 2, nike, interMilan, serieA);
        }
        if (juve != null && serieA != null && adidas != null) {
            saveJersey("Juventus Home 2024/2025", 950000.0, 9, 7, 4, 2, adidas, juve, serieA);
        }
        if (psg != null && ligue1 != null && nike != null) {
            saveJersey("PSG Home 2024/2025", 875000.0, 10, 8, 5, 2, nike, psg, ligue1);
            saveJersey("PSG Away 2024/2025", 875000.0, 7, 5, 4, 2, nike, psg, ligue1);
        }

        System.out.println("=== Database Seeder: 18 Jerseys berhasil diinsert ===");
    }

    private void saveJersey(String name, Double price, Integer stockS, Integer stockM, Integer stockL, Integer stockXL, Brand brand, Team team, League league) {
        Jersey jersey = new Jersey();
        jersey.setName(name);
        jersey.setPrice(price);
        jersey.setStockS(stockS);
        jersey.setStockM(stockM);
        jersey.setStockL(stockL);
        jersey.setStockXL(stockXL);
        jersey.setBrand(brand);
        jersey.setTeam(team);
        jersey.setLeague(league);
        jerseyRepository.save(jersey);
    }

    private void seedOrders() {
        orderRepository.deleteAll();
        shipmentRepository.deleteAll();

        seedAddresses();

        List<User> customers = userRepository.findAll().stream()
                .filter(u -> "CUSTOMER".equals(u.getRole()))
                .toList();

        List<Jersey> jerseys = jerseyRepository.findAll();
        List<PromoCode> promos = promoCodeRepository.findAll().stream()
                .filter(p -> p.getIsActive() != null && p.getIsActive())
                .sorted((a, b) -> a.getId().compareTo(b.getId()))
                .toList();
        
        if (customers.isEmpty() || jerseys.size() < 3) {
            return;
        }
        
        fixMissingShipments();
        
        System.out.println("=== Seed Orders: Found " + promos.size() + " active promo codes ===");
        for (PromoCode p : promos) {
            System.out.println("  Promo: " + p.getCode() + " (" + p.getDiscountPercent() + "%)");
        }

        int[][] orderConfigs = {
            {0, 0, 0, OrderStatus.COMPLETED.ordinal()},
            {1, 1, 0, OrderStatus.PROCESSING.ordinal()},
            {2, 2, 0, OrderStatus.PENDING.ordinal()},
            {3, 3, 0, OrderStatus.SHIPPED.ordinal()},
            {4, 4, 1, OrderStatus.CONFIRMED.ordinal()},
            {5, 5, 0, OrderStatus.COMPLETED.ordinal()},
            {6, 6, 0, OrderStatus.PROCESSING.ordinal()},
            {7, 7, 2, OrderStatus.DELIVERED.ordinal()},
            {8, 8, 0, OrderStatus.SHIPPED.ordinal()},
            {9, 9, 1, OrderStatus.COMPLETED.ordinal()},
            {10, 10, 0, OrderStatus.CANCELLED.ordinal()},
            {11, 11, 3, OrderStatus.PROCESSING.ordinal()},
            {12, 12, 0, OrderStatus.PENDING.ordinal()},
            {13, 13, 1, OrderStatus.CONFIRMED.ordinal()},
            {14, 14, 2, OrderStatus.SHIPPED.ordinal()},
        };

        int[] promoIndices = {0, 1, -1, 2, 3, -1, 4, -1, 0, 1, -1, 2, -1, 3, -1};
        String[] couriers = {"JNE Express", "SiCepat", "J&T Express", "Pos Indonesia", "TIKI"};

        String[] sizes = {"S", "M", "L", "XL"};
        LocalDateTime baseDate = LocalDateTime.of(2024, 3, 1, 10, 0);

        int shipmentCount = 0;

        for (int i = 0; i < orderConfigs.length; i++) {
            int[] config = orderConfigs[i];
            int customerIdx = config[0];
            int jerseyIdx = config[1];
            int extraItem = config[2];
            OrderStatus status = OrderStatus.values()[config[3]];

            Order order = new Order();
            order.setOrderDate(baseDate.plusDays(i).plusHours(i));
            order.setStatus(status);
            order.setCustomer(customers.get(customerIdx % customers.size()));

            int qty1 = (int)(Math.random() * 3) + 1;
            OrderItem item1 = new OrderItem();
            item1.setOrder(order);
            item1.setJersey(jerseys.get(jerseyIdx % jerseys.size()));
            item1.setSize(sizes[(int)(Math.random() * 4)]);
            item1.setQuantity(qty1);
            item1.setPrice(jerseys.get(jerseyIdx % jerseys.size()).getPrice());
            order.getItems().add(item1);

            if (extraItem > 0) {
                int jerseyIdx2 = (jerseyIdx + 1) % jerseys.size();
                int qty2 = (int)(Math.random() * 2) + 1;
                OrderItem item2 = new OrderItem();
                item2.setOrder(order);
                item2.setJersey(jerseys.get(jerseyIdx2));
                item2.setSize(sizes[(int)(Math.random() * 4)]);
                item2.setQuantity(qty2);
                item2.setPrice(jerseys.get(jerseyIdx2).getPrice());
                order.getItems().add(item2);

                if (extraItem > 1) {
                    int jerseyIdx3 = (jerseyIdx + 2) % jerseys.size();
                    int qty3 = (int)(Math.random() * 2) + 1;
                    OrderItem item3 = new OrderItem();
                    item3.setOrder(order);
                    item3.setJersey(jerseys.get(jerseyIdx3));
                    item3.setSize(sizes[(int)(Math.random() * 4)]);
                    item3.setQuantity(qty3);
                    item3.setPrice(jerseys.get(jerseyIdx3).getPrice());
                    order.getItems().add(item3);
                }
            }

            Double totalPrice = calculateTotalPrice(order);
            order.setTotalPrice(totalPrice);
            
            int promoIdx = promoIndices[i];
            if (promoIdx >= 0 && promos.size() > promoIdx) {
                PromoCode promo = promos.get(promoIdx);
                Double discountAmount = promo.calculateDiscount(totalPrice);
                order.setPromoCode(promo);
                order.setDiscountAmount(discountAmount);
                order.setFinalPrice(totalPrice - discountAmount);
            } else {
                order.setDiscountAmount(0.0);
                order.setFinalPrice(totalPrice);
            }
            
            orderRepository.save(order);

            if (status == OrderStatus.PROCESSING || status == OrderStatus.SHIPPED || status == OrderStatus.DELIVERED) {
                Shipment shipment = new Shipment();
                shipment.setOrder(order);
                shipment.setShippingCost(15000.0 + (int)(Math.random() * 35000));
                
                User customer = order.getCustomer();
                var addresses = addressRepository.findByUserId(customer.getId());
                if (!addresses.isEmpty()) {
                    shipment.setAddress(addresses.get(0));
                }
                
                if (status == OrderStatus.PROCESSING) {
                    shipment.setStatus(OrderStatus.PROCESSING);
                    shipment.setCourierName(null);
                    shipment.setTrackingNumber(null);
                } else {
                    shipment.setCourierName(couriers[(int)(Math.random() * couriers.length)]);
                    String tracking = "TRK" + System.currentTimeMillis() + String.format("%02d", i);
                    shipment.setTrackingNumber(tracking);
                    
                    if (status == OrderStatus.SHIPPED) {
                        shipment.setStatus(OrderStatus.SHIPPED);
                    } else if (status == OrderStatus.DELIVERED) {
                        shipment.setStatus(OrderStatus.DELIVERED);
                    }
                }
                
                shipmentRepository.save(shipment);
                shipmentCount++;
            }
        }

        System.out.println("=== Database Seeder: 15 Orders berhasil diinsert ===");
        System.out.println("=== Database Seeder: " + shipmentCount + " Shipments berhasil diinsert ===");
    }

    private void seedAddresses() {
        if (addressRepository.count() > 0) {
            return;
        }
        
        List<User> customers = userRepository.findAll().stream()
                .filter(u -> "CUSTOMER".equals(u.getRole()))
                .toList();

        String[][] addressData = {
            {"Rumah Bandung", "Jl. Braga No. 123, Bandung, 40111, Jawa Barat"},
            {"Kantor Surabaya", "Jl. Tunjungan No. 45, Surabaya, 60261, Jawa Timur"},
            {"Rumah Medan", "Jl. Gatot Subroto No. 88, Medan, 20159, Sumatera Utara"},
            {"Kos Yogyakarta", "Jl. Malioboro No. 56, Yogyakarta, 55111, DI Yogyakarta"},
            {"Rumah Semarang", "Jl. Pandanaran No. 77, Semarang, 50134, Jawa Tengah"},
            {"Apartemen Makassar", "Jl. Pettarani No. 99, Makassar, 90111, Sulawesi Selatan"},
            {"Rumah Palembang", "Jl. Merdeka No. 10, Palembang, 30111, Sumatera Selatan"},
            {"Kos Padang", "Jl. Hayam Wuruk No. 22, Padang, 25111, Sumatera Barat"},
            {"Villa Denpasar", "Jl. Sunset Road No. 33, Denpasar, 80111, Bali"},
            {"Rumah Bogor", "Jl. Pajajaran No. 44, Bogor, 16151, Jawa Barat"},
            {"Kos Malang", "Jl. Kawi No. 55, Malang, 65111, Jawa Timur"},
            {"Rumah Solo", "Jl. Slamet Riyadi No. 66, Solo, 57111, Jawa Tengah"},
            {"Apartemen Samarinda", "Jl. AW Syahranie No. 77, Samarinda, 75119, Kalimantan Timur"},
            {"Rumah Pekanbaru", "Jl. Sudirman No. 88, Pekanbaru, 28111, Riau"},
        };

        int idx = 0;
        for (User customer : customers) {
            if (idx < addressData.length) {
                Address addr = new Address();
                addr.setUser(customer);
                addr.setTitle(addressData[idx][0]);
                addr.setFullAddress(addressData[idx][1]);
                addr.setCity(addressData[idx][1].split(", ")[1]);
                addr.setPostalCode(addressData[idx][1].split(", ")[2]);
                addr.setIsDefault(true);
                addressRepository.save(addr);
                idx++;
            }
        }
        
        System.out.println("=== Database Seeder: " + idx + " Addresses berhasil diinsert ===");
    }

    private Double calculateTotalPrice(Order order) {
        Double total = 0.0;
        for (OrderItem item : order.getItems()) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }

    private void fixMissingShipments() {
        List<Order> ordersNeedingShipment = orderRepository.findAll().stream()
                .filter(o -> {
                    OrderStatus s = o.getStatus();
                    return s == OrderStatus.PROCESSING || s == OrderStatus.SHIPPED || 
                           s == OrderStatus.DELIVERED || s == OrderStatus.RETURNED;
                })
                .toList();

        int fixedCount = 0;
        for (Order order : ordersNeedingShipment) {
            Optional<Shipment> existingShipment = shipmentRepository.findByOrderId(order.getId());
            if (existingShipment.isEmpty()) {
                Shipment shipment = new Shipment();
                shipment.setOrder(order);
                shipment.setStatus(order.getStatus());
                shipment.setShippingCost(25000.0);
                
                if (order.getStatus() == OrderStatus.PROCESSING) {
                    shipment.setCourierName(null);
                    shipment.setTrackingNumber(null);
                } else {
                    shipment.setCourierName("JNE Express");
                    shipment.setTrackingNumber("TRK" + System.currentTimeMillis() + order.getId());
                }
                
                shipmentRepository.save(shipment);
                fixedCount++;
                System.out.println("  [FIX] Created missing shipment for Order #" + order.getId());
            }
        }
        
        if (fixedCount > 0) {
            System.out.println("=== Database Seeder: Fixed " + fixedCount + " missing shipments ===");
        }
    }

    private void seedPromoCodes() {
        LocalDate today = LocalDate.now();

        PromoCode promo1 = new PromoCode();
        promo1.setCampaignName("Promo Kemerdekaan");
        promo1.setCode("MERDEKA17");
        promo1.setDiscountPercent(17.0);
        promo1.setMaxDiscount(50000.0);
        promo1.setStartDate(today.minusDays(5));
        promo1.setEndDate(today.plusDays(30));
        promo1.setIsActive(true);
        promoCodeRepository.save(promo1);

        PromoCode promo2 = new PromoCode();
        promo2.setCampaignName("Diskon Akhir Tahun");
        promo2.setCode("TAHUNBARU2025");
        promo2.setDiscountPercent(25.0);
        promo2.setMaxDiscount(75000.0);
        promo2.setStartDate(today.minusDays(30));
        promo2.setEndDate(today.plusDays(10));
        promo2.setIsActive(true);
        promoCodeRepository.save(promo2);

        PromoCode promo3 = new PromoCode();
        promo3.setCampaignName("Promo Musim Panas");
        promo3.setCode("SUMMER25");
        promo3.setDiscountPercent(25.0);
        promo3.setStartDate(today.plusDays(5));
        promo3.setEndDate(today.plusDays(60));
        promo3.setIsActive(true);
        promoCodeRepository.save(promo3);

        PromoCode promo4 = new PromoCode();
        promo4.setCampaignName("Flash Sale");
        promo4.setCode("FLASH99");
        promo4.setDiscountPercent(50.0);
        promo4.setMaxDiscount(100000.0);
        promo4.setStartDate(today.minusDays(2));
        promo4.setEndDate(today.plusDays(1));
        promo4.setIsActive(true);
        promoCodeRepository.save(promo4);

        PromoCode promo5 = new PromoCode();
        promo5.setCampaignName("Promo Ramadan");
        promo5.setCode("RAMADAN45");
        promo5.setDiscountPercent(45.0);
        promo5.setStartDate(today.minusDays(60));
        promo5.setEndDate(today.minusDays(20));
        promo5.setIsActive(false);
        promoCodeRepository.save(promo5);

        PromoCode promo6 = new PromoCode();
        promo6.setCampaignName("Diskon Member");
        promo6.setCode("MEMBER20");
        promo6.setDiscountPercent(20.0);
        promo6.setStartDate(today.minusDays(15));
        promo6.setEndDate(today.plusDays(45));
        promo6.setIsActive(true);
        promoCodeRepository.save(promo6);

        PromoCode promo7 = new PromoCode();
        promo7.setCampaignName("Promo Liga Champion");
        promo7.setCode("CHAMPION777");
        promo7.setDiscountPercent(30.0);
        promo7.setMaxDiscount(150000.0);
        promo7.setStartDate(today.minusDays(10));
        promo7.setEndDate(today.plusDays(5));
        promo7.setIsActive(true);
        promoCodeRepository.save(promo7);

        PromoCode promo8 = new PromoCode();
        promo8.setCampaignName("Diskon Pertama");
        promo8.setCode("HEMAT50K");
        promo8.setDiscountPercent(50.0);
        promo8.setStartDate(today.minusDays(100));
        promo8.setEndDate(today.minusDays(90));
        promo8.setIsActive(false);
        promoCodeRepository.save(promo8);

        PromoCode promo9 = new PromoCode();
        promo9.setCampaignName("Promo Weekend");
        promo9.setCode("WEEKEND15");
        promo9.setDiscountPercent(15.0);
        promo9.setStartDate(today.plusDays(7));
        promo9.setEndDate(today.plusDays(9));
        promo9.setIsActive(true);
        promoCodeRepository.save(promo9);

        PromoCode promo10 = new PromoCode();
        promo10.setCampaignName("Big Sale");
        promo10.setCode("BIGSALE100");
        promo10.setDiscountPercent(10.0);
        promo10.setStartDate(today.minusDays(1));
        promo10.setEndDate(today.plusDays(14));
        promo10.setIsActive(true);
        promoCodeRepository.save(promo10);

        System.out.println("=== Database Seeder: 10 PromoCodes berhasil diinsert ===");
    }

    private void seedFaqs() {
        Faq faq1 = new Faq();
        faq1.setQuestion("Apakah jersey yang dijual original 100%?");
        faq1.setAnswer("Ya, semua jersey yang kami jual adalah produk original 100% dari brand resmi seperti Nike, Adidas, Puma, dan lainnya. Kami tidak menjual produk KW atau replika.");
        faq1.setCategory("Produk");
        faq1.setSortOrder(1);
        faq1.setIsActive(true);
        faqRepository.save(faq1);

        Faq faq2 = new Faq();
        faq2.setQuestion("Bisakah saya custom nama dan nomor di jersey?");
        faq2.setAnswer("Mohon maaf, saat ini kami belum menyediakan layanan custom nama dan nomor jersey. Jersey yang kami jual adalah produk original dengan design resmi dari club/league.");
        faq2.setCategory("Produk");
        faq2.setSortOrder(2);
        faq2.setIsActive(true);
        faqRepository.save(faq2);

        Faq faq3 = new Faq();
        faq3.setQuestion("Berapa lama waktu pengiriman jersey?");
        faq3.setAnswer("Waktu pengiriman bervariasi tergantung lokasi. Untuk Jawa biasanya 2-4 hari kerja, luar Jawa 4-7 hari kerja, dan daerah terpencil bisa lebih lama. Kami menggunakan JNE, SiCepat, J&T, Pos Indonesia, dan TIKI.");
        faq3.setCategory("Pengiriman");
        faq3.setSortOrder(3);
        faq3.setIsActive(true);
        faqRepository.save(faq3);

        Faq faq4 = new Faq();
        faq4.setQuestion("Bagaimana jika jersey yang diterima cacat/rusak?");
        faq4.setAnswer("Jika jersey yang Anda terima dalam kondisi cacat atau rusak, silakan foto dan laporkan ke kami dalam waktu 1x24 jam setelah paket diterima. Kami akan mengganti dengan jersey baru tanpa biaya tambahan.");
        faq4.setCategory("Pengiriman");
        faq4.setSortOrder(4);
        faq4.setIsActive(true);
        faqRepository.save(faq4);

        Faq faq5 = new Faq();
        faq5.setQuestion("Metode pembayaran apa saja yang tersedia?");
        faq5.setAnswer("Kami menerima pembayaran melalui Transfer Bank (BCA, Mandiri, BNI, BRI), E-Wallet (OVO, Dana, GoPay), dan Virtual Account. Pembayaran harus dilakukan dalam 1x24 jam setelah order.");
        faq5.setCategory("Pembayaran");
        faq5.setSortOrder(5);
        faq5.setIsActive(true);
        faqRepository.save(faq5);

        Faq faq6 = new Faq();
        faq6.setQuestion("Bagaimana cara menggunakan kode kupon/promo?");
        faq6.setAnswer("Anda dapat memasukkan kode kupon pada kolom 'Kode Promo' saat checkout. Pastikan kupon masih aktif dan belum melewati masa berlaku. Setiap kupon memiliki ketentuan minimal belanja yang berbeda.");
        faq6.setCategory("Pembayaran");
        faq6.setSortOrder(6);
        faq6.setIsActive(true);
        faqRepository.save(faq6);

        Faq faq7 = new Faq();
        faq7.setQuestion("Apakah bisa return/refund jersey?");
        faq7.setAnswer("Kami menerima return jika jersey yang diterima tidak sesuai pesanan (salah size, salah jersey, atau cacat). Pengajuan return harus dilakukan maksimal 1x24 jam setelah paket diterima dengan foto bukti.");
        faq7.setCategory("Produk");
        faq7.setSortOrder(7);
        faq7.setIsActive(true);
        faqRepository.save(faq7);

        Faq faq8 = new Faq();
        faq8.setQuestion("Bagaimana cara mengetahui ketersediaan stok jersey?");
        faq8.setAnswer("Stok jersey kami diperbarui secara real-time di website. Jika jersey yang Anda inginkan habis, Anda bisa menghubungi kami via WhatsApp untuk informasi restock. Biasanya restock dilakukan dalam 1-2 minggu.");
        faq8.setCategory("Produk");
        faq8.setSortOrder(8);
        faq8.setIsActive(true);
        faqRepository.save(faq8);

        Faq faq9 = new Faq();
        faq9.setQuestion("Apakah ada biaya tambahan untuk pengiriman?");
        faq9.setAnswer("Ya, biaya pengiriman dihitung berdasarkan berat paket, dimensi, dan tujuan pengiriman. Biaya pengiriman sudah termasuk dalam total pembayaran saat checkout. Gratis ongkir mungkin tersedia dengan promo tertentu.");
        faq9.setCategory("Pengiriman");
        faq9.setSortOrder(9);
        faq9.setIsActive(true);
        faqRepository.save(faq9);

        Faq faq10 = new Faq();
        faq10.setQuestion("Bagaimana cara menghubungi customer service?");
        faq10.setAnswer("Anda dapat menghubungi customer service kami melalui WhatsApp di nomor yang tertera di website, email, atau melalui kolom komentar di media sosial kami. Jam operasional CS adalah Senin-Jumat pukul 09.00-17.00 WIB.");
        faq10.setCategory("Layanan");
        faq10.setSortOrder(10);
        faq10.setIsActive(true);
        faqRepository.save(faq10);

        System.out.println("=== Database Seeder: 10 FAQs berhasil diinsert ===");
    }
}