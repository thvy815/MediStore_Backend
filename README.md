# Backend Java - Website Bán Thuốc Lẻ

## 1. Giới thiệu
Đây là backend của website bán thuốc, được phát triển bằng **Java 17** với **Spring Boot**.  
Backend cung cấp **REST API** để quản lý:
- Sản phẩm thuốc (kho)
- Đơn hàng đặt thuốc
- Thanh toán 
- Voucher 
- Báo cáo thống kê
- Thông tin khách hàng
- Quản lý người dùng (Admin, Warehouse Staff, Customer Service Staff, Pharmacist, Accountant, Customer)

## 2. Công nghệ sử dụng
- **Java 17 (LTS)**  
- **Spring Boot 3.x**  
- **Spring Web** (REST API)  
- **Spring Data JPA** (quản lý database)  
- **Spring Security** (Authentication & Authorization)  
- **Database**: PostgreSQL
- **Maven** (quản lý dependency)  
- **Optional**: Spring Boot DevTools, Spring Validation, Spring Mail

## 3. Cài đặt và chạy project
### 3.1 Yêu cầu
- JDK 17 đã cài đặt
- Maven đã cài đặt
- Database (PostgreSQL) 

### 3.2 Clone project
```bash
git clone https://github.com/thvy815/MediStore_Backend.git
cd MediStore_Backend