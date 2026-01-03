# 🏗️ HRM - Modular Structure (Cấu Trúc Theo Module)

## Cấu Trúc Dự Án Modular

```
hrm/
└── src/main/java/com/example/hrm/
    │
    ├── modules/                                    # 📦 Các Module Chính
    │
    ├── attendance/                                 # 📋 Module Chấm Công
    │   ├── entity/
    │   │   ├── Attendance.java
    │   │   ├── AttendancePenalty.java
    │   │   ├── BreakTime.java
    │   │   └── OTRate.java
    │   ├── dto/
    │   │   ├── request/
    │   │   │   └── AttendanceCreateRequest.java
    │   │   └── response/
    │   │       └── AttendanceResponse.java
    │   ├── repository/
    │   │   ├── AttendanceRepository.java
    │   │   ├── BreakTimeRepository.java
    │   │   ├── OTRateRepository.java
    │   │   └── AttendancePenaltyRepository.java
    │   ├── service/
    │   │   ├── AttendanceService.java
    │   │   ├── AttendanceCheckInService.java
    │   │   ├── AttendanceCheckOutService.java
    │   │   ├── AttendancePenaltyService.java
    │   │   ├── AttendanceScanService.java
    │   │   ├── BreakTimeService.java
    │   │   └── OTRateService.java
    │   ├── controller/
    │   │   ├── AttendanceController.java
    │   │   └── AttendanceScanController.java
    │   └── mapper/
    │       ├── AttendanceMapper.java
    │       └── BreakTimeMapper.java
    │
    ├── employee/                                   # 👤 Module Nhân Viên
    │   ├── entity/
    │   │   ├── Employee.java
    │   │   ├── Contact.java
    │   │   └── SalaryAdjustment.java
    │   ├── dto/
    │   │   ├── request/
    │   │   └── response/
    │   ├── repository/
    │   │   ├── EmployeeRepository.java
    │   │   ├── ContactRepository.java
    │   │   └── SalaryAdjustmentRepository.java
    │   ├── service/
    │   │   ├── EmployeeService.java
    │   │   ├── ContactService.java
    │   │   └── SalaryAdjustmentService.java
    │   ├── controller/
    │   │   ├── EmployeeController.java
    │   │   ├── ContactController.java
    │   │   └── SalaryAdjustmentController.java
    │   └── mapper/
    │       ├── EmployeeMapper.java
    │       ├── ContactMapper.java
    │       └── SalaryAdjustmentMapper.java
    │
    ├── organization/                               # 🏢 Module Tổ Chức
    │   ├── entity/
    │   │   ├── Department.java
    │   │   ├── SubDepartment.java
    │   │   ├── Address.java
    │   │   ├── Province.java
    │   │   ├── District.java
    │   │   └── Ward.java
    │   ├── dto/
    │   │   ├── request/
    │   │   └── response/
    │   ├── repository/
    │   │   ├── DepartmentRepository.java
    │   │   ├── SubDepartmentRepository.java
    │   │   ├── AddressRepository.java
    │   │   ├── ProvinceRepository.java
    │   │   ├── DistrictRepository.java
    │   │   └── WardRepository.java
    │   ├── service/
    │   │   ├── DepartmentService.java
    │   │   ├── SubDepartmentService.java
    │   │   ├── AddressService.java
    │   │   ├── ProvinceService.java
    │   │   ├── DistrictService.java
    │   │   └── WardService.java
    │   ├── controller/
    │   │   ├── DepartmentController.java
    │   │   ├── SubDepartmentController.java
    │   │   ├── AddressController.java
    │   │   ├── ProvinceController.java
    │   │   ├── DistrictController.java
    │   │   └── WardController.java
    │   └── mapper/
    │       ├── DepartmentMapper.java
    │       ├── SubDepartmentMapper.java
    │       ├── AddressMapper.java
    │       ├── ProvinceMapper.java
    │       ├── DistrictMapper.java
    │       └── WardMapper.java
    │
    ├── payroll/                                    # 💰 Module Bảng Lương
    │   ├── entity/
    │   │   ├── Payroll.java
    │   │   ├── PayrollCycle.java
    │   │   ├── PayrollApprovalHistory.java
    │   │   └── SalaryContract.java
    │   ├── dto/
    │   │   ├── request/
    │   │   └── response/
    │   ├── repository/
    │   │   ├── PayrollRepository.java
    │   │   ├── PayrollCycleRepository.java
    │   │   ├── PayrollApprovalHistoryRepository.java
    │   │   └── SalaryContractRepository.java
    │   ├── service/
    │   │   ├── PayrollService.java
    │   │   ├── PayrollCycleService.java
    │   │   └── SalaryContractService.java
    │   ├── controller/
    │   │   ├── PayrollCycleController.java
    │   │   └── SalaryContractController.java
    │   └── mapper/
    │       ├── PayrollMapper.java
    │       ├── PayrollCycleMapper.java
    │       └── SalaryContractMapper.java
    │
    ├── penalty/                                    # ⚠️ Module Phạt
    │   ├── entity/
    │   │   ├── PenaltyRule.java
    │   │   └── PenaltySource.java
    │   ├── dto/
    │   │   ├── request/
    │   │   └── response/
    │   ├── repository/
    │   │   ├── PenaltyRuleRepository.java
    │   │   └── PenaltySourceRepository.java
    │   ├── service/
    │   │   ├── PenaltyRuleService.java
    │   │   └── PenaltyService.java
    │   ├── controller/
    │   │   └── PenaltyRuleController.java
    │   └── mapper/
    │       ├── PenaltyRuleMapper.java
    │       └── PenaltySourceMapper.java
    │
    ├── contract/                                   # 📄 Module Hợp Đồng
    │   ├── entity/
    │   │   └── Contract.java
    │   ├── dto/
    │   │   ├── request/
    │   │   └── response/
    │   ├── repository/
    │   │   └── ContractRepository.java
    │   ├── service/
    │   │   └── ContractService.java
    │   ├── controller/
    │   │   └── ContractController.java
    │   └── mapper/
    │       └── ContractMapper.java
    │
    ├── file/                                       # 📁 Module Quản Lý File
    │   ├── entity/
    │   │   └── FileAttachment.java
    │   ├── dto/
    │   │   ├── request/
    │   │   └── response/
    │   ├── repository/
    │   │   └── FileAttachmentRepository.java
    │   ├── service/
    │   │   ├── FileUploadService.java
    │   │   └── FileAttachmentService.java
    │   ├── controller/
    │   │   └── FileAttachmentController.java
    │   └── mapper/
    │       └── FileAttachmentMapper.java
    │
    ├── user/                                       # 🔐 Module Quản Lý Người Dùng
    │   ├── entity/
    │   │   ├── UserAccount.java
    │   │   ├── Role.java
    │   │   └── Permission.java
    │   ├── dto/
    │   │   ├── request/
    │   │   └── response/
    │   ├── repository/
    │   │   ├── UserAccountRepository.java
    │   │   ├── RoleRepository.java
    │   │   └── PermissionRepository.java
    │   ├── service/
    │   │   ├── UserAccountService.java
    │   │   ├── RoleService.java
    │   │   └── PermissionService.java
    │   ├── controller/
    │   │   ├── UserAccountController.java
    │   │   ├── RoleController.java
    │   │   └── AuthController.java
    │   └── mapper/
    │       ├── UserAccountMapper.java
    │       └── RoleMapper.java
    │
    ├── shared/                                     # 🔧 Code Chia Sẻ Giữa Modules
    │   ├── config/
    │   │   ├── JwtKeyStore.java
    │   │   ├── RedisConfig.java
    │   │   ├── SecurityConfiguration.java
    │   │   └── StartUpTask.java
    │   ├── constants/
    │   ├── exception/
    │   │   └── (Exception classes)
    │   ├── utils/
    │   │   └── (Utility classes)
    │   ├── enums/
    │   │   ├── AdjustmentType.java
    │   │   ├── AttendanceStatus.java
    │   │   ├── BasedOn.java
    │   │   ├── BreakType.java
    │   │   └── (Other enums)
    │   ├── redis/
    │   │   └── (Redis related classes)
    │   ├── call_api/
    │   │   └── CallApiFaceRecognition.java
    │   └── configuration/
    │       ├── entryPoint/
    │       └── filter/
    │
    └── HrmApplication.java                        # 🚀 Main Application Entry Point
```

---

## 📋 Danh Sách 8 Module Chính

| # | Module | Chức Năng | Path |
|---|--------|----------|------|
| 1 | **Attendance** | Quản lý chấm công, chấm công OT, phạt công | `modules/attendance/` |
| 2 | **Employee** | Quản lý thông tin nhân viên, liên hệ, điều chỉnh lương | `modules/employee/` |
| 3 | **Organization** | Quản lý bộ phận, địa chỉ, tỉnh/huyện/xã | `modules/organization/` |
| 4 | **Payroll** | Quản lý bảng lương, chu kỳ lương, hợp đồng lương | `modules/payroll/` |
| 5 | **Penalty** | Quản lý quy tắc phạt, nguồn phạt | `modules/penalty/` |
| 6 | **Contract** | Quản lý hợp đồng lao động | `modules/contract/` |
| 7 | **File** | Quản lý tải file, tài liệu đính kèm | `modules/file/` |
| 8 | **User** | Quản lý tài khoản, vai trò, quyền hạn, xác thực | `modules/user/` |

---

## 🔧 Shared/Common (Chia Sẻ Chung)

Các lớp được chia sẻ giữa các modules:
- **Configuration**: JwtKeyStore, RedisConfig, SecurityConfiguration, StartUpTask
- **Exception**: Custom exceptions
- **Utils**: Helper methods
- **Enums**: AdjustmentType, AttendanceStatus, BreakType, v.v...
- **Redis**: Redis-related operations
- **API Integration**: CallApiFaceRecognition

---

## ✅ Lợi Ích của Cấu Trúc Modular

✅ **Tổ chức code rõ ràng** - Dễ tìm code liên quan  
✅ **Độc lập module** - Có thể phát triển/test từng module riêng  
✅ **Tái sử dụng code** - Code chung trong `shared/`  
✅ **Dễ bảo trì** - Khi thay đổi, chỉ cần sửa trong module đó  
✅ **Phân công dễ** - Team có thể chia việc theo module  
✅ **Scalable** - Dễ thêm module mới  
✅ **Giảm xung đột** - Git conflicts ít hơn khi nhiều người làm  

---

## 🎯 Layer Trong Mỗi Module

Mỗi module có cấu trúc **3-layer** (hoặc 4-layer nếu có mapper):

```
module/
├── entity/         → Database models (JPA Entities)
├── dto/           → Data Transfer Objects (Request/Response)
├── repository/    → Data Access Layer (Database queries)
├── service/       → Business Logic Layer
├── controller/    → REST API endpoints
└── mapper/        → Entity ↔ DTO conversions
```

---

## 📝 Ví Dụ: Request Flow trong Module Attendance

```
User Request (HTTP)
         ↓
[AttendanceController] - nhận request
         ↓
[AttendanceService] - xử lý business logic
         ↓
[AttendanceRepository] - truy cập database
         ↓
[Attendance Entity] - data model
         ↓
Database (MySQL/PostgreSQL)
```

---

## 🚀 Cách Sử Dụng Cấu Trúc Này

1. **Thêm API mới** → Thêm controller method trong module tương ứng
2. **Thêm business logic** → Thêm service method
3. **Truy cập database** → Thêm repository method
4. **Thêm validation** → Thêm trong service
5. **Thêm module mới** → Tạo folder mới trong `modules/`

---

**Ngày cập nhật:** 3 Tháng 1, 2026  
**Kiến trúc:** Modular Monolith (Feature-Based)
