# 🛒 myjersey - Toko Jersey Bola Online

**myjersey** adalah aplikasi web e-commerce sederhana yang dirancang khusus untuk menjual dan mengelola koleksi jersey sepak bola dari berbagai klub dan negara. Aplikasi ini dibangun menggunakan arsitektur MVC (Model-View-Controller) dengan Java Spring Boot di sisi *backend* dan Thymeleaf serta Tailwind CSS di sisi *frontend*.

Aplikasi ini dibuat untuk memenuhi tugas Pemrograman Sistem Berbasis Framework (PSBF).

---

## 🚀 Fitur Utama

### 🛍️ Untuk Pelanggan (User)
- **Katalog Produk:** Jelajahi berbagai macam jersey berdasarkan tim, liga, maupun merek (*brand*) ternama.
- **Pencarian & Filter:** Filter koleksi jersey secara dinamis melalui parameter navigasi yang responsif.
- **Detail Produk:** Lihat detail lengkap produk termasuk harga, ukuran, logo tim, dan deskripsi jersey.
- **Wishlist:** Simpan jersey favorit ke dalam daftar keinginan (*wishlist*) sebelum membeli.
- **Autentikasi Aman:** Sistem pendaftaran (Register) dan masuk (Login) akun dengan antarmuka yang bersih dan modern.

### 🛡️ Untuk Administrator (Admin)
- **Manajemen Inventaris:** Tambah, perbarui, atau hapus data jersey beserta stok dan ukurannya.
- **Manajemen Entitas:** Kelola data relasional seperti Liga, Brand, dan Tim (termasuk penyimpanan logo berbasis Base64).
- **Pemrosesan Data:** *Seeder* bawaan untuk menginisialisasi database dengan data awal yang realistis.

---

## 🛠️ Teknologi yang Digunakan

- **Backend:** Java, Spring Boot, Spring MVC, Spring Data JPA, Spring Security (Opsional/Tergantung Konfigurasi).
- **Frontend:** HTML5, Thymeleaf (Template Engine), Tailwind CSS (via CDN untuk *styling* cepat dan responsif).
- **Database:** Relasional Database (MySQL/H2) - Manajemen ORM menggunakan Hibernate.
- **Penyimpanan Media:** Gambar (seperti logo klub) dikonversi dan disimpan di dalam database sebagai string `Base64`.

---

## ⚙️ Prasyarat (Requirements)

Sebelum menjalankan aplikasi ini secara lokal, pastikan Anda telah menginstal:
- **Java Development Kit (JDK)** versi 17 atau yang lebih baru.
- **Maven** (biasanya sudah bawaan jika menggunakan IDE seperti IntelliJ IDEA atau Eclipse).
- **Database Server** (contoh: MySQL) yang sudah berjalan (jika tidak menggunakan H2 *in-memory* database).

---

## 🏃‍♂️ Cara Menjalankan Secara Lokal (Local Setup)

1. **Clone repositori ini:**
   ```bash
   git clone https://github.com/aliridwan15/my-jersey-psbf.git
   cd my-jersey-psbf
   ```

2. **Konfigurasi Database:**
   Buka file `src/main/resources/application.properties` dan sesuaikan URL database, *username*, dan *password* dengan sistem lokal Anda.
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/nama_database_anda
   spring.datasource.username=root
   spring.datasource.password=password_anda
   ```
   *(Abaikan langkah ini jika Anda menggunakan database H2).*

3. **Jalankan Aplikasi:**
   Anda bisa menjalankannya langsung melalui IDE atau menggunakan perintah Maven:
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Akses Aplikasi:**
   Buka browser web Anda dan arahkan ke alamat berikut:
   ```
   http://localhost:8080
   ```

---

## 📸 Antarmuka (Tampilan Antarmuka)

*Aplikasi ini telah dirancang khusus dengan Tailwind CSS untuk memberikan antarmuka e-commerce yang bersih, modern, dan sangat responsif di berbagai ukuran perangkat, mulai dari desktop hingga ponsel.*

---

## 🤝 Kontribusi

Aplikasi ini dikembangkan untuk keperluan akademik/tugas kuliah. Namun, kritik dan saran untuk pembelajaran sangat diterima! Jika Anda menemukan kutu (*bug*) atau memiliki ide fitur baru, silakan buka *Issue* atau kirimkan *Pull Request*.

## 📄 Lisensi

Proyek ini dibuat untuk tujuan edukasi (PSBF). Silakan gunakan sebagai referensi atau bahan belajar.
