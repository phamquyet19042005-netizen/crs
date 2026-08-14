# HƯỚNG DẪN CHẠY VÀ TEST LAB 4 (MICROSERVICES VỚI JWT & API GATEWAY)

Dự án đã được cấu hình hoàn chỉnh theo cấu trúc **Maven Multi-module** gồm 4 service:
1. `auth-service` (Cổng `8081`) - Database: `auth_db`
2. `course-service` (Cổng `8082`) - Database: `course_db`
3. `registration-service` (Cổng `8083`) - Database: `registration_db`
4. `api-gateway` (Cổng `8080`) - Điểm truy cập API duy nhất

---

## 1. Chuẩn bị Cơ sở dữ liệu MySQL

Mật khẩu MySQL đã được thiết lập mặc định trong tất cả các `application.properties` là **`quyetdragon`**.

Mở MySQL Workbench / DBeaver / Navicat / cmd và chạy nội dung file `init_databases.sql`:
```sql
CREATE DATABASE IF NOT EXISTS auth_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS course_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS registration_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

---

## 2. Mở và chạy trên IntelliJ IDEA 2025.2.2

1. Mở thư mục `lab4` trong IntelliJ IDEA.
2. IntelliJ sẽ tự động nhận diện Maven Parent POM (`crs-parent`) và 4 sub-modules. Nhấn nút **Sync Maven Changes** (biểu tượng chữ M) nếu cần.
3. Khởi chạy 4 service theo thứ tự (chạy hàm `main` của từng file):
   - **`AuthServiceApplication.java`** (`auth-service`) -> Cổng 8081 (sẽ tự động seed user `admin` và `student1`).
   - **`CourseServiceApplication.java`** (`course-service`) -> Cổng 8082.
   - **`RegistrationServiceApplication.java`** (`registration-service`) -> Cổng 8083.
   - **`ApiGatewayApplication.java`** (`api-gateway`) -> Cổng 8080.

> [!TIP]
> Bạn có thể bật cửa sổ **Services** trong IntelliJ IDEA (`View -> Tool Windows -> Services` hoặc `Alt + 8`) để quản lý và start/stop cả 4 services cùng một lúc rất tiện lợi.

---

## 3. Kiểm thử với Postman qua API Gateway (`localhost:8080`)

Tất cả các request từ client đều gọi thông qua **Gateway cổng 8080**, không gọi trực tiếp cổng 8081/8082/8083.

File Postman collection đã được tạo sẵn tại:
📁 `c:\Users\phamq\IdeaProjects\lab4\Buoi4_API_Gateway.postman_collection.json`

### Cách import vào Postman:
1. Mở Postman -> Chọn **Import** -> Chọn file `Buoi4_API_Gateway.postman_collection.json`.
2. Chạy lần lượt 10 request có sẵn trong collection:

| STT | Request Name | Method | URL | Header / Body | Kết quả kỳ vọng | Ý nghĩa bảo mật |
|:---|:---|:---|:---|:---|:---|:---|
| **1** | Login Admin | `POST` | `http://localhost:8080/api/auth/login` | Body: `admin` / `admin123` | **200 OK** + Token Role ADMIN | Tự động lưu `{{admin_token}}` vào biến collection |
| **2** | Login Student1 | `POST` | `http://localhost:8080/api/auth/login` | Body: `student1` / `student123` | **200 OK** + Token Role STUDENT | Tự động lưu `{{student_token}}` vào biến collection |
| **3** | Login sai Pass | `POST` | `http://localhost:8080/api/auth/login` | Body: `admin` / `saipassword` | **401 Unauthorized** | Báo lỗi thông tin đăng nhập |
| **4** | Xem môn học Public | `GET` | `http://localhost:8080/api/courses` | Không cần Header | **200 OK** danh sách môn học | Route public xem khóa học |
| **5** | Tạo môn học không Token | `POST` | `http://localhost:8080/api/courses` | Không có Authorization | **401 Unauthorized** | **Gateway chặn sớm** tại AuthHeaderFilter |
| **6** | Tạo môn học với Token Student | `POST` | `http://localhost:8080/api/courses` | `Authorization: Bearer {{student_token}}` | **403 Forbidden** | Qua Gateway, nhưng **Course-Service từ chối** vì thiếu role ADMIN |
| **7** | Tạo môn học với Token Admin | `POST` | `http://localhost:8080/api/courses` | `Authorization: Bearer {{admin_token}}` | **201 Created** | Thêm môn học thành công |
| **8** | Đăng ký môn học với Token Student | `POST` | `http://localhost:8080/api/registrations` | `Authorization: Bearer {{student_token}}`, Body: `{"studentId":1,"courseId":1}` | **201 Created** | Registration-Service xác thực JWT & gọi `reserve-seat` sang Course-Service |
| **9** | Partner API với API Key đúng | `GET` | `http://localhost:8080/api/public/courses` | `X-API-KEY: crs-partner-key-2026` | **200 OK** | Gateway cho phép truy cập theo API Key của đối tác |
| **10** | Partner API với API Key sai | `GET` | `http://localhost:8080/api/public/courses` | `X-API-KEY: wrong-key` | **403 Forbidden** | Gateway chặn truy cập đối tác không hợp lệ |

---

## 4. Tài khoản kiểm thử mặc định

- **ADMIN**:
  - `username`: `admin`
  - `password`: `admin123`
  - `role`: `ADMIN`
- **STUDENT**:
  - `username`: `student1`
  - `password`: `student123`
  - `role`: `STUDENT`

## 5. Các Secret & API Key cấu hình

- **JWT Secret Key** (dùng chung cho cả 4 services):
  `CRS-Microservices-Secret-Key-Nam-3-Hoc-Ky-2026-Doi-Trong-Thuc-Te`
- **Partner API Key**:
  `crs-partner-key-2026`
