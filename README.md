# Literature Platform Backend

Adabiy platformasi uchun to'liq backend API - Spring Boot, Spring Security va AI integratsiyasi bilan.

## Xususiyatlar

### Autentifikatsiya va Avtorizatsiya
- ✅ JWT asosida autentifikatsiya
- ✅ Role-based access control (USER va ADMIN)
- ✅ Ro'yxatdan o'tish va login
- ✅ Parolni unutish va tiklash
- ✅ Email tasdiqlash

### Kitoblar Boshqaruvi
- ✅ Kitoblarni CRUD operatsiyalari
- ✅ Qidiruv va filtrlash
- ✅ Muallif va kategoriya bo'yicha filtrlash
- ✅ Mashhur, yangi va yuqori baholangan kitoblar
- ✅ Fayl yuklash (muqova, PDF, audio)
- ✅ Ko'rishlar va yuklab olishlar soni

### Foydalanuvchi Funksiyalari
- ✅ Sevimli kitoblar
- ✅ Kitoblarga sharh va baho
- ✅ Profil boshqaruvi
- ✅ Parolni o'zgartirish

### AI Integratsiyasi
- ✅ OpenAI bilan chatbot
- ✅ Kitoblarni tahlil qilish
- ✅ Kitoblarni umumlashtirish
- ✅ Shaxsiylashtirilgan tavsiyalar

### WebSocket Chat
- ✅ Real-time chat xonalari
- ✅ Kitoblar bo'yicha muhokamalar

## Texnologiyalar

- **Java 17**
- **Spring Boot 3.2.1**
- **Spring Security** - JWT autentifikatsiya
- **Spring Data JPA** - Ma'lumotlar bazasi bilan ishlash
- **PostgreSQL** - Asosiy ma'lumotlar bazasi
- **WebSocket** - Real-time chat
- **OpenAI API** - AI funksiyalari
- **Lombok** - Boilerplate kodini kamaytirish
- **ModelMapper** - DTO mapping
- **Maven** - Dependency management

## O'rnatish va Ishga Tushirish

### Talablar

- Java 17 yoki yuqori
- Maven 3.6+
- PostgreSQL 12+

### 1. Ma'lumotlar bazasini yaratish

```sql
CREATE DATABASE literature_db;
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE literature_db TO postgres;
```

### 2. Konfiguratsiya

`src/main/resources/application.properties` faylini tahrirlang:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/literature_db
spring.datasource.username=postgres
spring.datasource.password=postgres

# Email (Gmail uchun)
spring.mail.username=sizning-emailingiz@gmail.com
spring.mail.password=sizning-app-parolingiz

# OpenAI
openai.api.key=sizning-openai-api-key
```

### 3. Loyihani build qilish

```bash
mvn clean install
```

### 4. Ishga tushirish

```bash
mvn spring-boot:run
```

Yoki:

```bash
java -jar target/platform-1.0.0.jar
```

Server `http://localhost:8080` da ishga tushadi.

## API Endpoints

### Autentifikatsiya

| Method | Endpoint | Tavsif | Access |
|--------|----------|--------|--------|
| POST | `/api/auth/register` | Ro'yxatdan o'tish | Public |
| POST | `/api/auth/login` | Login | Public |
| POST | `/api/auth/forgot-password` | Parolni unutish | Public |
| POST | `/api/auth/reset-password` | Parolni tiklash | Public |

### Foydalanuvchi

| Method | Endpoint | Tavsif | Access |
|--------|----------|--------|--------|
| GET | `/api/users/me` | Joriy foydalanuvchi ma'lumotlari | Authenticated |
| PUT | `/api/users/me` | Profilni yangilash | Authenticated |
| POST | `/api/users/me/profile-image` | Profil rasmini yuklash | Authenticated |
| POST | `/api/users/me/change-password` | Parolni o'zgartirish | Authenticated |

### Kitoblar

| Method | Endpoint | Tavsif | Access |
|--------|----------|--------|--------|
| GET | `/api/books` | Barcha kitoblar | Public |
| GET | `/api/books/{id}` | Kitob ma'lumotlari | Public |
| GET | `/api/books/search?keyword=` | Qidirish | Public |
| GET | `/api/books/featured` | Taniqli kitoblar | Public |
| GET | `/api/books/top-rated` | Eng yuqori baholangan | Public |
| GET | `/api/books/latest` | Yangi kitoblar | Public |
| GET | `/api/books/popular` | Mashhur kitoblar | Public |
| POST | `/api/books` | Kitob yaratish | ADMIN |
| PUT | `/api/books/{id}` | Kitobni yangilash | ADMIN |
| DELETE | `/api/books/{id}` | Kitobni o'chirish | ADMIN |
| POST | `/api/books/{id}/cover` | Muqova yuklash | ADMIN |
| POST | `/api/books/{id}/pdf` | PDF yuklash | ADMIN |
| POST | `/api/books/{id}/audio` | Audio yuklash | ADMIN |

### Sharhlar

| Method | Endpoint | Tavsif | Access |
|--------|----------|--------|--------|
| POST | `/api/reviews` | Sharh yozish | Authenticated |
| PUT | `/api/reviews/{id}` | Sharhni tahrirlash | Authenticated |
| DELETE | `/api/reviews/{id}` | Sharhni o'chirish | Authenticated |
| GET | `/api/reviews/book/{bookId}` | Kitob sharhlari | Public |
| GET | `/api/reviews/my-reviews` | Mening sharhlarim | Authenticated |

### Sevimlilar

| Method | Endpoint | Tavsif | Access |
|--------|----------|--------|--------|
| POST | `/api/favorites/{bookId}` | Sevimliga qo'shish | Authenticated |
| DELETE | `/api/favorites/{bookId}` | Sevimlilardan o'chirish | Authenticated |
| GET | `/api/favorites` | Sevimli kitoblar | Authenticated |
| GET | `/api/favorites/check/{bookId}` | Sevimlidami? | Authenticated |

### AI

| Method | Endpoint | Tavsif | Access |
|--------|----------|--------|--------|
| POST | `/api/ai/chat` | AI bilan suhbat | Authenticated |
| GET | `/api/ai/analyze/{bookId}` | Kitobni tahlil qilish | Authenticated |
| GET | `/api/ai/summarize/{bookId}` | Kitobni umumlashtirish | Authenticated |
| GET | `/api/ai/recommendations` | Tavsiyalar | Authenticated |

### Chat

| Method | Endpoint | Tavsif | Access |
|--------|----------|--------|--------|
| GET | `/api/chat/rooms` | Barcha xonalar | Authenticated |
| GET | `/api/chat/rooms/{id}` | Xona ma'lumotlari | Authenticated |
| GET | `/api/chat/rooms/{roomId}/messages` | Xona xabarlari | Authenticated |
| POST | `/api/chat/rooms` | Xona yaratish | Authenticated |

### WebSocket

WebSocket endpoint: `/ws`

Topic subscriptions:
- `/topic/room.{roomId}` - Chat xonasi xabarlari

Send messages to:
- `/app/chat.send` - Xabar yuborish

## Ma'lumotlar Modeli

### User
- id, username, email, password
- fullName, phone, profileImage
- role (USER/ADMIN)
- isActive, emailVerified
- resetToken, resetTokenExpiry

### Book
- id, title, description
- author (Author entity)
- categories (Category entities)
- isbn, publishedYear, publisher
- language, pageCount
- coverImage, pdfFile, audioFile
- viewCount, downloadCount
- averageRating, ratingCount
- isFeatured, isActive

### Author
- id, name, biography
- birthDate, deathDate
- nationality, profileImage

### Category
- id, name, description

### Review
- id, user, book
- rating (1-5), comment

### Favorite
- id, user, book

## Admin Yaratish

Birinchi admin foydalanuvchini yaratish uchun database'da qo'lda yangilash:

```sql
INSERT INTO users (username, email, password, full_name, role, is_active, email_verified, created_at, updated_at)
VALUES ('admin', 'admin@example.com', '$2a$10$slYQMml8mBUvMO/RGPMQY.wz9P5/9qlz.tBOJLxJQQw4VqGxQJKKq', 
        'Administrator', 'ADMIN', true, true, NOW(), NOW());
```

Default password: `admin123`

## Frontend Integration

Backend `http://localhost:8080` da ishga tushadi va CORS orqali `http://localhost:3000` va `http://localhost:5173` frontend'larga ruxsat beradi.

Frontend uchun axios konfiguratsiyasi:

```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
});

// JWT token'ni har bir request'ga qo'shish
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default api;
```

## Development

### Test ma'lumotlarini qo'shish

Loyihani ishga tushirganingizdan so'ng, test ma'lumotlarini qo'lda qo'shishingiz mumkin:

1. Admin akkauntini yarating
2. Postman yoki cURL orqali API'larni test qiling
3. Author va Category qo'shing
4. Kitoblar qo'shing

## Production

Production uchun quyidagilarni o'zgartiring:

1. `application.properties` faylidagi barcha parol va key'larni o'zgartiring
2. CORS konfiguratsiyasini production domenlarga moslashtiring
3. File upload'lar uchun cloud storage (AWS S3) ishlatishni o'ylab ko'ring
4. Database connection pool'ni sozlang
5. Logging'ni sozlang

## Muammolarni Hal Qilish

### Ma'lumotlar bazasi ulanmayapti
- PostgreSQL ishga tushganligini tekshiring
- Database credentials to'g'riligini tekshiring
- Database mavjudligini tekshiring

### Email yuborilmayapti
- Gmail App Password yaratganligingizni tekshiring
- SMTP sozlamalarini to'g'riligini tekshiring

### OpenAI ishlamayapti
- API key to'g'riligini tekshiring
- Account balansini tekshiring

## Litsenziya

MIT License

## Muallif

Literature Platform Development Team
