# 📊 HRM Project - Module Architecture Visualization

## 🏗️ Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                     Spring Boot Application                         │
│                        (HrmApplication)                             │
└──────────────┬──────────────────────────────────────────────────────┘
               │
               ├─────────────────────────────────────────────────────┐
               │                                                     │
        ┌──────▼──────┐                                       ┌──────▼──────────┐
        │   @Controllers                                      │   @Services    │
        │   @RestControllers                                  │   Business     │
        │   API Endpoints                                     │   Logic        │
        └──────▲──────┘                                       └──────▲──────────┘
               │                                                    │
        ┌──────┴──────────────────────────────────────────────┬────┘
        │                                                      │
        │        ┌────────────────────────────────┐            │
        │        │      @Repositories            │            │
        │        │   Database Access Layer       │            │
        │        └────────────────────────────────┘            │
        │                  │                                    │
        │                  ▼                                    │
        │        ┌────────────────────────────────┐            │
        │        │        @Entities               │            │
        │        │   Database Tables              │            │
        │        └────────────────────────────────┘            │
        │                                                       │
        │        ┌────────────────────────────────┐            │
        │        │         DTOs                   │            │
        └───────►│  Request / Response Objects    │◄───────────┘
                 └────────────────────────────────┘
                          │
                          ▼
                 ┌────────────────────┐
                 │   @Mappers         │
                 │ Entity ↔ DTO       │
                 └────────────────────┘
```

---

## 🗂️ Module Organization Chart

```
com.example.hrm/
│
├── HrmApplication (Main)
│
├── modules/                          ← Feature-based Modules
│   ├── attendance/                   ← Module 1: Chấm công
│   │   ├── entity/
│   │   ├── dto/
│   │   ├── repository/
│   │   ├── service/
│   │   ├── controller/
│   │   └── mapper/
│   │
│   ├── employee/                     ← Module 2: Nhân viên
│   │   ├── entity/
│   │   ├── dto/
│   │   ├── repository/
│   │   ├── service/
│   │   ├── controller/
│   │   └── mapper/
│   │
│   ├── organization/                 ← Module 3: Tổ chức
│   │   ├── entity/
│   │   ├── dto/
│   │   ├── repository/
│   │   ├── service/
│   │   ├── controller/
│   │   └── mapper/
│   │
│   ├── payroll/                      ← Module 4: Bảng lương
│   │   ├── entity/
│   │   ├── dto/
│   │   ├── repository/
│   │   ├── service/
│   │   ├── controller/
│   │   └── mapper/
│   │
│   ├── penalty/                      ← Module 5: Phạt
│   ├── contract/                     ← Module 6: Hợp đồng
│   ├── file/                         ← Module 7: File
│   ├── user/                         ← Module 8: Người dùng
│   ├── auth/                         ← Module 9: Xác thực
│   ├── face-recognition/             ← Module 10: Nhận diện
│   └── email/                        ← Module 11: Email
│
├── shared/                           ← Shared Resources
│   ├── config/                       ← Spring Configurations
│   │   ├── SecurityConfiguration.java
│   │   ├── RedisConfig.java
│   │   ├── JwtKeyStore.java
│   │   └── ...
│   │
│   ├── exception/                    ← Global Exceptions
│   │   ├── ResourceNotFoundException.java
│   │   ├── BusinessException.java
│   │   └── ...
│   │
│   ├── enums/                        ← Shared Enums
│   │   ├── AttendanceStatus.java
│   │   ├── AdjustmentType.java
│   │   └── ...
│   │
│   ├── redis/                        ← Redis Utilities
│   │   └── RedisService.java
│   │
│   └── util/                         ← Utility Classes
│       ├── DateUtil.java
│       ├── StringUtil.java
│       └── ...
│
└── call_api/                         ← External APIs
    └── CallApiFaceRecognition.java
```

---

## 🔗 Inter-Module Communication Flow

```
┌─────────────────────────────────────────────────────────────────────┐
│                        Client Request                               │
│                       (HTTP/REST API)                               │
└────────────────────────────────┬────────────────────────────────────┘
                                 │
                                 ▼
                    ┌────────────────────────┐
                    │ Controller Layer       │
                    │ @RestController        │
                    │ /api/v1/attendance/*   │
                    └────────────────┬───────┘
                                     │
                                     ▼
                    ┌────────────────────────┐
                    │ Service Layer (Biz)    │
                    │ @Service               │
                    │ AttendanceService      │
                    └────────────────┬───────┘
                                     │
                ┌────────────────────┼────────────────────┐
                │                    │                    │
                ▼                    ▼                    ▼
    ┌─────────────────────┐  ┌──────────────────┐  ┌──────────────┐
    │ Own Repository      │  │ Call Other       │  │ Use Shared   │
    │ AttendanceRepo      │  │ Module Service   │  │ Exception    │
    │ .findById()         │  │ EmployeeService  │  │ Util/Config  │
    └──────────┬──────────┘  └──────────────────┘  └──────────────┘
               │
               ▼
    ┌──────────────────────┐
    │ Database             │
    │ (JPA Entities)       │
    │ Attendance Table     │
    └──────────────────────┘
                │
                ▼
    ┌──────────────────────┐
    │ Response             │
    │ Mapper               │
    │ Entity → DTO         │
    └──────────────────────┘
                │
                ▼
    ┌──────────────────────┐
    │ Response DTO         │
    │ AttendanceResponse   │
    └──────────────────────┘
                │
                ▼
    ┌──────────────────────┐
    │ HTTP Response        │
    │ (JSON)               │
    └──────────────────────┘
```

---

## 📦 Module Dependencies Map

```
Attendance Module
├── Depends on: Employee Module (để lấy employee info)
├── Depends on: Organization Module (để lấy department info)
├── Depends on: Shared Config (Exception handling, etc.)
└── API: /api/v1/attendance/*

Employee Module
├── Depends on: Organization Module (để lấy department)
├── Depends on: Shared Config
└── API: /api/v1/employees/*

Organization Module
├── Depends on: Shared Config
└── API: /api/v1/departments/*

Payroll Module
├── Depends on: Employee Module
├── Depends on: Attendance Module (để tính lương)
├── Depends on: Shared Config
└── API: /api/v1/payroll/*

Penalty Module
├── Depends on: Employee Module
├── Depends on: Attendance Module
├── Depends on: Shared Config
└── API: /api/v1/penalties/*

User Module
├── Depends on: Shared Config
└── API: /api/v1/users/*

Auth Module
├── Depends on: User Module
├── Depends on: Shared Config (JWT, Security)
└── API: /api/v1/auth/*

File Module
├── Depends on: Shared Config
└── API: /api/v1/files/*

Face Recognition Module
├── Depends on: Attendance Module
├── Depends on: call_api (External API)
├── Depends on: Shared Config
└── API: /api/v1/face-recognition/*

Email Module
├── Depends on: Shared Config
└── API: /api/v1/email/*

Contract Module
├── Depends on: Employee Module
├── Depends on: Shared Config
└── API: /api/v1/contracts/*
```

---

## 🔄 Data Flow Example: Creating Attendance Record

```
1. Client sends POST request
   POST /api/v1/attendance
   Body: { "employeeId": 1, "checkInTime": "2024-01-03 08:00:00" }
                                    │
                                    ▼
2. AttendanceController receives request
   ├── Validates input
   ├── Calls AttendanceService.createAttendance()
                                    │
                                    ▼
3. AttendanceService processes business logic
   ├── Validates employee exists (calls EmployeeService)
   ├── Maps request DTO to Entity (AttendanceMapper)
   ├── Saves to database (AttendanceRepository.save())
   ├── Handles any exceptions
                                    │
                                    ▼
4. AttendanceRepository
   └── Saves Attendance Entity to DB table
                                    │
                                    ▼
5. Response is created
   ├── Entity is mapped to Response DTO
   ├── Response is serialized to JSON
                                    │
                                    ▼
6. HTTP 201 Created
   Body: {
     "id": 1,
     "employeeId": 1,
     "checkInTime": "2024-01-03T08:00:00",
     "status": "SUCCESS",
     "createdAt": "2024-01-03T08:00:01"
   }
```

---

## 🎯 Module Interaction Example

```
Scenario: When calculating payroll, we need attendance data

PayrollService.calculatePayroll(employeeId, month)
│
├─ Step 1: Get employee info
│  └─ EmployeeService.getEmployee(employeeId)
│     └─ Returns: Employee entity with salary info
│
├─ Step 2: Get attendance records
│  └─ AttendanceService.getAttendanceByEmployeeId(employeeId)
│     └─ Returns: List of Attendance records
│
├─ Step 3: Get overtime rates
│  └─ OTRateService.getOTRates(employeeId, month)
│     └─ Returns: List of OT rates
│
├─ Step 4: Get penalties
│  └─ PenaltyService.getPenalties(employeeId, month)
│     └─ Returns: List of penalties
│
├─ Step 5: Calculate and save payroll
│  └─ PayrollRepository.save(payrollEntity)
│
└─ Step 6: Return result
   └─ PayrollResponse with calculated salary

Note: Tất cả interactions đều qua Service layer
      Không direct access database từ controller
      Tất cả exceptions được catch bởi shared exception handling
```

---

## 📊 Package Naming Convention

```
com.example.hrm
    │
    ├── modules.{moduleName}
    │   ├── entity              (JPA Entities)
    │   ├── dto.request         (Request DTOs)
    │   ├── dto.response        (Response DTOs)
    │   ├── repository          (Data Access)
    │   ├── service             (Business Logic)
    │   ├── service.impl        (Service Implementations - optional)
    │   ├── controller          (REST Controllers)
    │   ├── mapper              (Entity ↔ DTO Mappers)
    │   └── exception           (Module-specific exceptions - optional)
    │
    ├── shared
    │   ├── config              (Spring configurations)
    │   ├── exception           (Global exceptions)
    │   ├── enums               (Shared enums)
    │   ├── redis               (Redis operations)
    │   ├── util                (Utility classes)
    │   └── constants           (Constants - optional)
    │
    ├── call_api
    │   └── ...                 (External API calls)
    │
    └── HrmApplication          (Main Spring Boot class)
```

---

## ✅ Migration Checklist

```
□ Create all module packages
  ├── attendance/
  ├── employee/
  ├── organization/
  ├── payroll/
  ├── penalty/
  ├── contract/
  ├── file/
  ├── user/
  ├── auth/
  ├── face-recognition/
  └── email/

□ Move entities to respective modules
  └── entity/ under each module

□ Move repositories to respective modules
  └── repository/ under each module

□ Move services to respective modules
  └── service/ under each module

□ Move controllers to respective modules
  └── controller/ under each module

□ Move DTOs to respective modules
  └── dto/request/ and dto/response/ under each module

□ Move mappers to respective modules
  └── mapper/ under each module

□ Move shared files
  ├── configuration → shared/config/
  ├── exception → shared/exception/
  ├── enums → shared/enums/
  ├── redis → shared/redis/
  └── util → shared/util/

□ Update all package declarations
  └── Check each Java file has correct package statement

□ Update all imports
  └── Use IDE's refactor or Find & Replace

□ Update HrmApplication.java
  └── Add @ComponentScan with correct basePackages

□ Verify compilation
  └── mvn clean compile

□ Run tests
  └── mvn test

□ Start application
  └── mvn spring-boot:run

□ Test API endpoints
  └── Verify all endpoints work as before
```

---

## 📚 Related Documentation

- **PROJECT_STRUCTURE.md** - Detailed structure explanation
- **MIGRATION_GUIDE.md** - Step-by-step migration instructions
- **IMPORT_UPDATE_GUIDE.md** - How to update imports
- **README_MODULAR_STRUCTURE.md** - Overview and best practices

---

**Version:** 1.0  
**Last Updated:** January 3, 2026  
**Status:** Ready for Implementation

