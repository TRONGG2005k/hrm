# HRM Project - Modular Architecture

## 🏗️ Cấu Trúc Dự Án Mới

Dự án đã được tái cấu trúc theo **Modular Monolith Architecture** pattern, tách tách từng feature vào một module riêng, với đầy đủ các layer (Controller, Service, Repository, Entity, DTO, Mapper).

```
hrm/
├── src/main/java/com/example/hrm/
│   ├── modules/                           # Các module chính của ứng dụng
│   │   ├── attendance/                    # Module Chấm công
│   │   │   ├── entity/                    # JPA Entities
│   │   │   │   ├── Attendance.java
│   │   │   │   ├── AttendancePenalty.java
│   │   │   │   ├── BreakTime.java
│   │   │   │   └── OTRate.java
│   │   │   ├── dto/
│   │   │   │   ├── request/               # Request DTOs
│   │   │   │   └── response/              # Response DTOs
│   │   │   ├── repository/                # JPA Repositories
│   │   │   ├── service/                   # Business Logic
│   │   │   ├── controller/                # REST Endpoints
│   │   │   └── mapper/                    # Entity <-> DTO Mappers
│   │   │
│   │   ├── employee/                      # Module Nhân viên
│   │   │   ├── entity/
│   │   │   │   ├── Employee.java
│   │   │   │   ├── Contact.java
│   │   │   │   └── SalaryAdjustment.java
│   │   │   ├── dto/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   ├── controller/
│   │   │   └── mapper/
│   │   │
│   │   ├── organization/                  # Module Tổ chức
│   │   │   ├── entity/
│   │   │   │   ├── Department.java
│   │   │   │   ├── SubDepartment.java
│   │   │   │   ├── Address.java
│   │   │   │   ├── Province.java
│   │   │   │   ├── District.java
│   │   │   │   └── Ward.java
│   │   │   ├── dto/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   ├── controller/
│   │   │   └── mapper/
│   │   │
│   │   ├── payroll/                       # Module Bảng lương
│   │   │   ├── entity/
│   │   │   │   ├── Payroll.java
│   │   │   │   ├── PayrollCycle.java
│   │   │   │   ├── PayrollApprovalHistory.java
│   │   │   │   └── SalaryContract.java
│   │   │   ├── dto/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   ├── controller/
│   │   │   └── mapper/
│   │   │
│   │   ├── penalty/                       # Module Phạt
│   │   │   ├── entity/
│   │   │   │   ├── PenaltyRule.java
│   │   │   │   └── PenaltySource.java
│   │   │   ├── dto/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   ├── controller/
│   │   │   └── mapper/
│   │   │
│   │   ├── contract/                      # Module Hợp đồng
│   │   │   ├── entity/
│   │   │   │   └── Contract.java
│   │   │   ├── dto/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   ├── controller/
│   │   │   └── mapper/
│   │   │
│   │   ├── file/                          # Module Quản lý file
│   │   │   ├── entity/
│   │   │   │   └── FileAttachment.java
│   │   │   ├── dto/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   │   ├── FileUploadService.java
│   │   │   │   └── FileAttachmentService.java
│   │   │   ├── controller/
│   │   │   └── mapper/
│   │   │
│   │   ├── user/                          # Module Quản lý người dùng
│   │   │   ├── entity/
│   │   │   │   ├── UserAccount.java
│   │   │   │   ├── Role.java
│   │   │   │   └── Permission.java
│   │   │   ├── dto/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   ├── controller/
│   │   │   └── mapper/
│   │   │
│   │   ├── auth/                          # Module Xác thực
│   │   │   ├── service/
│   │   │   │   ├── AuthService.java
│   │   │   │   └── JwtService.java
│   │   │   ├── dto/
│   │   │   │   ├── request/
│   │   │   │   └── response/
│   │   │   └── controller/
│   │   │
│   │   ├── face-recognition/              # Module Nhận diện khuôn mặt
│   │   │   ├── entity/
│   │   │   ├── service/
│   │   │   │   └── FaceRecognitionService.java
│   │   │   ├── controller/
│   │   │   └── dto/
│   │   │
│   │   └── email/                         # Module Email
│   │       ├── service/
│   │       │   └── EmailService.java
│   │       └── controller/
│   │
│   ├── shared/                            # Shared resources giữa các modules
│   │   ├── config/                        # Spring Configurations
│   │   │   ├── SecurityConfiguration.java
│   │   │   ├── RedisConfig.java
│   │   │   ├── JwtKeyStore.java
│   │   │   └── StartUpTask.java
│   │   ├── exception/                     # Global Exception Handling
│   │   │   └── ...exception classes...
│   │   ├── enums/                         # Shared Enums
│   │   │   ├── AdjustmentType.java
│   │   │   ├── AttendanceStatus.java
│   │   │   └── ...
│   │   ├── redis/                         # Redis utilities
│   │   └── util/                          # Utility classes
│   │
│   ├── call_api/                          # External API integrations
│   │   └── CallApiFaceRecognition.java
│   │
│   └── HrmApplication.java                # Main Spring Boot Application
│
└── src/resources/
    └── application.yml

```

## 📋 Module Mapping

| Module | Entities | Services | Controllers |
|--------|----------|----------|-------------|
| **attendance** | Attendance, AttendancePenalty, BreakTime, OTRate | AttendanceService, AttendanceCheckInService, AttendanceCheckOutService, AttendancePenaltyService, AttendanceScanService, BreakTimeService, OTRateService | AttendanceController, AttendanceScanController |
| **employee** | Employee, Contact, SalaryAdjustment | EmployeeService, ContactService, SalaryAdjustmentService | EmployeeController, ContactController, SalaryAdjustmentController |
| **organization** | Department, SubDepartment, Address, Province, District, Ward | DepartmentService, SubDepartmentService, AddressService, ProvinceService, DistrictService, WardService | DepartmentController, SubDepartmentController, AddressController, ProvinceController, DistrictController, WardController |
| **payroll** | Payroll, PayrollCycle, PayrollApprovalHistory, SalaryContract | PayrollService, PayrollCycleService, SalaryContractService | PayrollCycleController, SalaryContractController |
| **penalty** | PenaltyRule, PenaltySource | PenaltyRuleService, PenaltyService | PenaltyRuleController |
| **contract** | Contract | ContractService | ContractController |
| **file** | FileAttachment | FileUploadService, FileAttachmentService | FileAttachmentController |
| **user** | UserAccount, Role, Permission | UserAccountService, RoleService | UserAccountController, RoleController |
| **auth** | - | AuthService, JwtService | AuthController |
| **face-recognition** | - | FaceRecognitionService | FaceRecognitionController |
| **email** | - | EmailService | EmailTestController |

## 🔄 Migration Steps

### 1. **Entities** → Move to module entities folder
```bash
Attendance.java → modules/attendance/entity/
AttendancePenalty.java → modules/attendance/entity/
BreakTime.java → modules/attendance/entity/
OTRate.java → modules/attendance/entity/
Employee.java → modules/employee/entity/
Contact.java → modules/employee/entity/
...
```

### 2. **DTOs** → Move to module dto folders
```bash
*Request DTOs → modules/{module}/dto/request/
*Response DTOs → modules/{module}/dto/response/
```

### 3. **Repositories** → Move to module repositories folder
```bash
AttendanceRepository.java → modules/attendance/repository/
EmployeeRepository.java → modules/employee/repository/
...
```

### 4. **Services** → Move to module services folder
```bash
AttendanceService.java → modules/attendance/service/
EmployeeService.java → modules/employee/service/
...
```

### 5. **Controllers** → Move to module controllers folder
```bash
AttendanceController.java → modules/attendance/controller/
EmployeeController.java → modules/employee/controller/
...
```

### 6. **Mappers** → Move to module mappers folder
```bash
*Mapper.java → modules/{module}/mapper/
```

### 7. **Shared Resources** → Move to shared folder
```bash
All configuration classes → shared/config/
All exception classes → shared/exception/
All enums → shared/enums/
Redis-related classes → shared/redis/
```

## 🎯 Lợi ích của cấu trúc mới

✅ **Tính Modular**: Mỗi module là độc lập, dễ maintain và mở rộng
✅ **Tổ chức code rõ ràng**: Dễ tìm kiếm code liên quan đến một feature
✅ **Tái sử dụng**: Dễ chia sẻ code giữa các module thông qua shared package
✅ **Scalability**: Dễ dàng thêm feature mới hoặc tách module thành microservice
✅ **Team collaboration**: Nhiều team có thể làm việc trên các module khác nhau mà không xung đột
✅ **Testing**: Dễ viết unit test cho từng module
✅ **Dependency management**: Rõ ràng phụ thuộc của từng module

## 📝 Naming Convention

### Entity Packages
```
com.example.hrm.modules.{moduleName}.entity
```

### Service Packages
```
com.example.hrm.modules.{moduleName}.service
com.example.hrm.modules.{moduleName}.service.impl
```

### Controller Packages
```
com.example.hrm.modules.{moduleName}.controller
```

### Repository Packages
```
com.example.hrm.modules.{moduleName}.repository
```

### DTO Packages
```
com.example.hrm.modules.{moduleName}.dto.request
com.example.hrm.modules.{moduleName}.dto.response
```

### Mapper Packages
```
com.example.hrm.modules.{moduleName}.mapper
```

## 🔌 Cross-Module Communication

Khi một module cần sử dụng service từ module khác:

```java
// Không nên: Import trực tiếp
import com.example.hrm.modules.employee.service.EmployeeService;

// Nên: Inject qua interface hoặc shared service
@Service
public class AttendanceService {
    @Autowired
    private EmployeeService employeeService;
}
```

## ⚙️ Spring Configuration

Thêm `@ComponentScan` vào HrmApplication.java:

```java
@SpringBootApplication
@ComponentScan("com.example.hrm")
public class HrmApplication {
    public static void main(String[] args) {
        SpringApplication.run(HrmApplication.class, args);
    }
}
```

## 📚 Tiếp theo

1. Di chuyển tất cả file từ cấu trúc cũ vào module tương ứng
2. Cập nhật imports trong tất cả file
3. Kiểm tra lỗi compile
4. Test application
5. Update documentation

