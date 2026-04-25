package com.computershop.config;

import com.computershop.main.entities.*;
import com.computershop.main.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Seeds initial data into the file-based H2 database on first startup.
 * Runs only once — skipped if roles already exist.
 */
@Component
@Profile("h2")
public class DataInitializer implements CommandLineRunner {

    @Autowired private RoleRepository roleRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ImageRepository imageRepository;
    @Autowired private ProductRepository productRepository;

    @Override
    public void run(String... args) {
        // Already seeded — skip
        if (roleRepository.count() > 0) {
            return;
        }

        System.out.println("[DataInitializer] Seeding initial data...");

        // ── Roles ────────────────────────────────────────────────────────────
        Role adminRole    = roleRepository.save(new Role(1, "admin"));
        Role customerRole = roleRepository.save(new Role(2, "customer"));
        roleRepository.save(new Role(3, "staff"));
        roleRepository.save(new Role(4, "supplier"));

        // ── Users ────────────────────────────────────────────────────────────
        User admin = new User();
        admin.setUsername("admin");
        admin.setPasswordHash("admin123_hashed");
        admin.setRole(adminRole);
        admin.setEnabled(true);
        userRepository.save(admin);

        User user = new User();
        user.setUsername("user");
        user.setPasswordHash("user123_hashed");
        user.setRole(customerRole);
        user.setEnabled(true);
        userRepository.save(user);

        // ── Categories ───────────────────────────────────────────────────────
        Category keyboards   = categoryRepository.save(new Category("Keyboards",    "Computer keyboards, mechanical keyboards, gaming keyboards"));
        Category mice        = categoryRepository.save(new Category("Mice",          "Computer mice, gaming mice, wireless mice"));
        Category headsets    = categoryRepository.save(new Category("Headsets",      "Gaming headsets, audio headphones, communication headsets"));
        Category chairs      = categoryRepository.save(new Category("Gaming Chairs", "Gaming chairs, office chairs, ergonomic seating"));
        Category components  = categoryRepository.save(new Category("Components",    "CPU, RAM, GPU, motherboard and computer components"));
        Category monitors    = categoryRepository.save(new Category("Monitors",      "Computer monitors, gaming displays, LCD, LED screens"));
        Category speakers    = categoryRepository.save(new Category("Speakers",      "Computer speakers, gaming audio, sound systems"));

        // ── Images ───────────────────────────────────────────────────────────
        Image img1  = imageRepository.save(new Image("https://www.logitechg.com/content/dam/gaming/en/products/g915/g915-gallery-2.png"));
        Image img2  = imageRepository.save(new Image("https://product.hstatic.net/20000/product/thumbchuot_6ed5e43202c9498aacde369cb95573b3_0859ba8bea17000e77b7e6d0c7f0_master.gif"));
        Image img3  = imageRepository.save(new Image("https://owlgaming.vn/wp-content/uploads/2024/06/ARCTIS-7P-1.jpg"));
        Image img4  = imageRepository.save(new Image("https://www.dxracer-europe.com/bilder/artiklar/32125.jpg?m=15000"));
        Image img5  = imageRepository.save(new Image("https://bizweb.dktcdn.net/100/329/122/files/amd-5700g-02.jpg?v=15000"));
        Image img6  = imageRepository.save(new Image("https://dlcdnwebimgs.asus.com/gain/72C16A36-4EE3-4AC4-A58A-35F6B8A2FB6F/w717/h525/fwebp"));
        Image img7  = imageRepository.save(new Image("https://bizweb.dktcdn.net/thumb/grande/100/487/147/products/loa-logitech-speaker-system-z623-eu-246c339d-c1e4-4b1d-9700-1ee364c0ec0d.jpg?v=15000"));
        Image img8  = imageRepository.save(new Image("https://cdn2.cellphones.com.vn/x/media/catalog/product/_/0/_0000_43020_keyboard_corsair_k70_rgb_m.jpg"));
        Image img9  = imageRepository.save(new Image("https://product.hstatic.net/20000/product/g-pro-x-superlight-wireless-black-666_10000ce2e486f9108dbbb17c29159_1450bb4a9bd34dcb92fc77f627eb600d.jpg"));
        Image img10 = imageRepository.save(new Image("https://row.hyperx.com/cdn/shop/products/hyperx_cloud_alpha_s_blackblue_1_main.jpg?v=15000&width=1920"));

        // ── Products ─────────────────────────────────────────────────────────
        productRepository.save(new Product("Logitech G915 Mechanical Keyboard",   "Premium wireless mechanical keyboard with tactile switches, RGB lighting",         new BigDecimal("18000"), 50,  keyboards,  img1));
        productRepository.save(new Product("Razer DeathAdder V3 Gaming Mouse",    "Gaming mouse with Focus Pro 30K sensor, 8000Hz polling rate",                     new BigDecimal("15000"), 75,  mice,       img2));
        productRepository.save(new Product("SteelSeries Arctis 7P Headset",       "Wireless gaming headset with 7.1 audio, ClearCast microphone",                    new BigDecimal("12000"), 30,  headsets,   img3));
        productRepository.save(new Product("DXRacer Formula Series Gaming Chair", "Ergonomic gaming chair with memory foam padding, multi-directional adjustment",   new BigDecimal("10000"), 15,  chairs,     img4));
        productRepository.save(new Product("AMD Ryzen 7 5800X CPU",               "8-core 16-thread processor, 3.8GHz base clock, 4.7GHz boost",                    new BigDecimal("17000"), 25,  components, img5));
        productRepository.save(new Product("ASUS ROG Swift PG279QM Monitor",      "27\" QHD 240Hz IPS gaming monitor with G-Sync technology",                        new BigDecimal("15000"), 20,  monitors,   img6));
        productRepository.save(new Product("Logitech Z623 2.1 Speakers",          "2.1 speaker system with 200W power output, THX certified",                        new BigDecimal("20000"), 40,  speakers,   img7));
        productRepository.save(new Product("Corsair K70 RGB MK.2 Keyboard",       "Mechanical keyboard with Cherry MX Red switches, aluminum frame",                 new BigDecimal("12000"), 35,  keyboards,  img8));
        productRepository.save(new Product("Logitech G Pro X Superlight Mouse",   "Ultra-lightweight 63g gaming mouse with HERO 25K sensor",                        new BigDecimal("20000"), 45,  mice,       img9));
        productRepository.save(new Product("HyperX Cloud Alpha S Headset",        "Gaming headset with 50mm drivers, 7.1 surround sound",                           new BigDecimal("20000"), 60,  headsets,   img10));
        productRepository.save(new Product("Samsung 980 PRO 1TB SSD",             "NVMe PCIe 4.0 SSD, 7000MB/s read speed",                                         new BigDecimal("20000"), 45,  components, img8));
        productRepository.save(new Product("Corsair Vengeance LPX 16GB RAM",      "DDR4 3200MHz RAM, 2x8GB kit, aluminum heat spreader",                            new BigDecimal("15000"), 38,  components, img9));
        productRepository.save(new Product("MSI RTX 4070 Ti SUPER Graphics Card", "High-end gaming graphics card, 16GB GDDR6X",                                     new BigDecimal("20000"), 8,   components, img10));

        System.out.println("[DataInitializer] Seed complete.");
    }
}
