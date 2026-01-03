# 🏗️ HRM Project - Modular Architecture Implementation

## 📌 Tổng Quan

Dự án HRM đã được tái cấu trúc từ cấu trúc **layer-based** (cũ) sang **module-based / feature-based** (mới), theo mô hình **Modular Monolith Architecture**.

### Trước (Cấu Trúc Cũ - Layer-Based)
```
src/main/java/com/example/hrm/
├── controller/       (tất cả controllers)
├── service/          (tất cả services)
├── entity/           (tất cả entities)
├── dto/              (tất cả DTOs)
├── repository/       (tất cả repositories)
├── mapper/           (tất cả mappers)
└── ...
```

**Vấn đề:** Khó tìm code, khó maintain khi project phát triển lớn

### Sau (Cấu Trúc Mới - Feature-Based)
```
src/main/java/com/example/hrm/
├── modules/
│   ├── attendance/   (module chấm công - có controller, service, entity, dto, ...)
│   ├── employee/     (module nhân viên - có controller, service, entity, dto, ...)
│   ├── organization/ (module tổ chức - có controller, service, entity, dto, ...)
│   ├── payroll/      (module bảng lương - có controller, service, entity, dto, ...)
│   └── ...
└── shared/           (code được chia sẻ giữa các modules)
```

**Lợi ích:** Dễ tìm code, dễ maintain, dễ scale, dễ phân chia công việc cho team

---

## 📊 Danh Sách Modules

### 1. **Attendance Module** 📋
**Chức năng:** Quản lý chấm công nhân viên
- **Path:** `src/main/java/com/example/hrm/modules/attendance/`
- **Entities:** Attendance, AttendancePenalty, BreakTime, OTRate
- **Services:** AttendanceService, AttendanceCheckInService, AttendanceCheckOutService, AttendancePenaltyService, AttendanceScanService, BreakTimeService, OTRateService
- **Controllers:** AttendanceController, AttendanceScanController
- **Endpoints:** `/api/v1/attendance/*`

### 2. **Employee Module** 👤
**Chức năng:** Quản lý thông tin nhân viên
- **Path:** `src/main/java/com/example/hrm/modules/employee/`
- **Entities:** Employee, Contact, SalaryAdjustment
- **Services:** EmployeeService, ContactService, SalaryAdjustmentService
- **Controllers:** EmployeeController, ContactController, SalaryAdjustmentController
- **Endpoints:** `/api/v1/employees/*`, `/api/v1/contacts/*`, `/api/v1/salary-adjustments/*`

### 3. **Organization Module** 🏢
**Chức năng:** Quản lý cơ cấu tổ chức
- **Path:** `src/main/java/com/example/hrm/modules/organization/`
- **Entities:** Department, SubDepartment, Address, Province, District, Ward
- **Services:** DepartmentService, SubDepartmentService, AddressService, ProvinceService, DistrictService, WardService
- **Controllers:** DepartmentController, SubDepartmentController, AddressController, ProvinceController, DistrictController, WardController
- **Endpoints:** `/api/v1/departments/*`, `/api/v1/organizations/*`

### 4. **Payroll Module** 💰
**Chức năng:** Quản lý bảng lương
- **Path:** `src/main/java/com/example/hrm/modules/payroll/`
- **Entities:** Payroll, PayrollCycle, PayrollApprovalHistory, SalaryContract
- **Services:** PayrollService, PayrollCycleService, SalaryContractService
- **Controllers:** PayrollCycleController, SalaryContractController
- **Endpoints:** `/api/v1/payroll/*`

### 5. **Penalty Module** ⚠️
**Chức năng:** Quản lý các quy tắc phạt và phạt nhân viên
- **Path:** `src/main/java/com/example/hrm/modules/penalty/`
- **Entities:** PenaltyRule, PenaltySource
- **Services:** PenaltyRuleService, PenaltyService
- **Controllers:** PenaltyRuleController
- **Endpoints:** `/api/v1/penalties/*`

### 6. **Contract Module** 📄
**Chức năng:** Quản lý hợp đồng lao động
- **Path:** `src/main/java/com/example/hrm/modules/contract/`
- **Entities:** Contract
- **Services:** ContractService
- **Controllers:** ContractController
- **Endpoints:** `/api/v1/contracts/*`

### 7. **File Module** 📁
**Chức năng:** Quản lý tải file và lưu trữ tài liệu đính kèm
- **Path:** `src/main/java/com/example/hrm/modules/file/`
- **Entities:** FileAttachment
- **Services:** FileUploadService, FileAttachmentService
- **Controllers:** FileAttachmentController
- **Endpoints:** `/api/v1/files/*`

### 8. **User Module** 🔐
**Chức năng:** Quản lý tài khoản người dùng, vai trò và quyền
- **Path:** `src/main/java/com/example/hrm/modules/user/`
- **Entities:** UserAccount, Role, Permission
- **Services:** UserAccountService, RoleService
- **Controllers:** UserAccountController, RoleController
- **Endpoints:** `/api/v1/users/*`, `/api/v1/roles/*`

### 9. **Auth Module** 🔑
**Chức năng:** Xác thực và cấp phép
- **Path:** `src/main/java/com/example/hrm/modules/auth/`
- **Services:** AuthService, JwtService
- **Controllers:** AuthController
- **Endpoints:** `/api/v1/auth/*`

### 10. **Face Recognition Module** 👁️
**Chức năng:** Nhận diện khuôn mặt cho chấm công
- **Path:** `src/main/java/com/example/hrm/modules/face-recognition/`
- **Services:** FaceRecognitionService
- **Controllers:** FaceRecognitionController
- **Endpoints:** `/api/v1/face-recognition/*`

### 11. **Email Module** 📧
**Chức năng:** Gửi email
- **Path:** `src/main/java/com/example/hrm/modules/email/`
- **Services:** EmailService
- **Controllers:** EmailTestController
- **Endpoints:** `/api/v1/email/*`

### 12. **Shared Resources** 🔗
**Chức năng:** Code được sử dụng chung bởi tất cả các modules
- **Path:** `src/main/java/com/example/hrm/shared/`
- **Nội dung:**
  - `config/` - Spring configurations, security, JWT, Redis
  - `exception/` - Global exception handling
  - `enums/` - Enum classes được sử dụng chung
  - `redis/` - Redis utilities
  - `util/` - Utility classes

---

## 🗂️ Cấu Trúc File Chi Tiết (Ví dụ Attendance Module)

```
modules/attendance/
├── entity/
│   ├── Attendance.java
│   ├── AttendancePenalty.java
│   ├── BreakTime.java
│   └── OTRate.java
├── dto/
│   ├── request/
│   │   ├── AttendanceCreateRequest.java
│   │   ├── AttendanceUpdateRequest.java
│   │   └── ...
│   └── response/
│       ├── AttendanceResponse.java
│       ├── AttendanceListResponse.java
│       └── ...
├── repository/
│   ├── AttendanceRepository.java
│   ├── BreakTimeRepository.java
│   ├── OTRateRepository.java
│   └── ...
├── service/
│   ├── AttendanceService.java
│   ├── AttendanceCheckInService.java
│   ├── AttendanceCheckOutService.java
│   ├── AttendancePenaltyService.java
│   ├── AttendanceScanService.java
│   ├── BreakTimeService.java
│   ├── OTRateService.java
│   └── ... (có thể có impl/ subfolder nếu sử dụng interface)
├── controller/
│   ├── AttendanceController.java
│   └── AttendanceScanController.java
└── mapper/
    ├── AttendanceMapper.java
    ├── BreakTimeMapper.java
    ├── OTRateMapper.java
    └── ...
```

---

## 🚀 Bắt Đầu Sử Dụng

### 1. Xem Tổng Quan Cấu Trúc
```bash
Mở file: PROJECT_STRUCTURE.md
```

### 2. Đọc Hướng Dẫn Migration
```bash
Mở file: MIGRATION_GUIDE.md
```

### 3. Xem Các Template
- `ENTITY_TEMPLATE.java` - Template cho Entity
- `SERVICE_TEMPLATE.java` - Template cho Service
- `CONTROLLER_TEMPLATE.java` - Template cho Controller
- `REPOSITORY_TEMPLATE.java` - Template cho Repository
- `MAPPER_TEMPLATE.java` - Template cho Mapper
- `DTO_REQUEST_TEMPLATE.java` - Template cho Request DTO
- `DTO_RESPONSE_TEMPLATE.java` - Template cho Response DTO

### 4. Chạy Migration Script (PowerShell)
```bash
.\migrate-to-modular.ps1
```

### 5. Cập Nhật Imports
Sau khi di chuyển file, bạn cần cập nhật:
- Package declarations
- Import statements
- ComponentScan configuration

Xem chi tiết trong `MIGRATION_GUIDE.md`

### 6. Compile & Test
```bash
mvn clean compile
mvn test
mvn spring-boot:run
```

---

## 📝 Quy Tắc & Best Practices

### ✅ Nên Làm

1. **Mỗi Module = Một Feature**
   ```
   Attendance module chỉ chứa code liên quan đến chấm công
   Employee module chỉ chứa code liên quan đến nhân viên
   ```

2. **Dependency Injection**
   ```java
   @Service
   public class AttendanceService {
       @Autowired
       private EmployeeService employeeService;  // Inject từ module khác
   }
   ```

3. **Shared Code**
   ```java
   // Để trong shared/
   com.example.hrm.shared.exception.ResourceNotFoundException
   com.example.hrm.shared.config.SecurityConfiguration
   ```

4. **Service Interfaces (Best Practice)**
   ```
   modules/attendance/service/
   ├── AttendanceService.java      (Interface)
   └── impl/
       └── AttendanceServiceImpl.java (Implementation)
   ```

### ❌ Không Nên Làm

1. **Tạo Module cho Mọi Class**
   - ❌ Không tạo module cho một vài utils nhỏ
   - ✅ Để trong shared/util

2. **Circular Dependencies**
   - ❌ Module A import Module B, Module B import Module A
   - ✅ Tách phần chung vào shared, hoặc sử dụng event-driven

3. **Quá Nhiều File trong Một Package**
   - ❌ service/ có 50 file service
   - ✅ Tách thành subdirectories: service/impl/, service/dto/, v.v.

4. **Import Toàn Bộ Module**
   - ❌ `import com.example.hrm.modules.employee.*;`
   - ✅ Import chính xác những gì cần: `import com.example.hrm.modules.employee.service.EmployeeService;`

---

## 🔄 Workflow Cho Team

### Scenario: Team A làm Attendance Module, Team B làm Employee Module

```
Timeline:
Day 1: Tạo module structure (done ✅)
Day 2: Team A làm Attendance, Team B làm Employee
Day 3: Integration test
Day 4: Deploy
```

**Lợi ích:**
- Không có conflict trong Git
- Dễ code review từng module
- Dễ test độc lập
- Dễ deploy từng module

---

## 🐛 Troubleshooting

### Lỗi: `Cannot resolve symbol 'Attendance'`
**Giải pháp:**
```java
// Thay từ:
import com.example.hrm.entity.Attendance;

// Thành:
import com.example.hrm.modules.attendance.entity.Attendance;
```

### Lỗi: `Could not autowire bean of type EmployeeService`
**Giải pháp:**
Kiểm tra HrmApplication.java có `@ComponentScan`:
```java
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.example.hrm.modules",
    "com.example.hrm.shared"
})
public class HrmApplication {
    ...
}
```

### Lỗi: Application không start
**Giải pháp:**
```bash
# Xóa build cũ
mvn clean

# Compile lại
mvn compile

# Xem log
mvn spring-boot:run
```

---

## 📚 Tài Liệu Tham Khảo

- [Modular Monolith Architecture](https://en.wikipedia.org/wiki/Modular_monolith)
- [Spring Boot Project Structure Best Practices](https://docs.spring.io/spring-boot/docs/)
- [Java Package Naming Conventions](https://docs.oracle.com/javase/tutorial/java/package/namingconventions.html)

---

## 📞 Hỗ Trợ

Nếu bạn gặp vấn đề:

1. Kiểm tra `MIGRATION_GUIDE.md`
2. Xem ví dụ trong template files
3. Verify theo checklist trong `MIGRATION_GUIDE.md`

---

**Last Updated:** January 3, 2026
**Version:** 1.0
**Status:** Ready for migration
