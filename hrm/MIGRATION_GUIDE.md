# Hướng Dẫn Cập Nhật Modular Structure

## 📋 Checklist Hoàn Thiện Migration

Sau khi di chuyển các file tới cấu trúc modular, bạn cần thực hiện các bước sau:

### 1️⃣ Cập Nhật Package Declarations (tất cả file)

**Entities:**
```java
// Cũ
package com.example.hrm.entity;

// Mới
package com.example.hrm.modules.attendance.entity;
```

**Services:**
```java
// Cũ
package com.example.hrm.service;

// Mới
package com.example.hrm.modules.attendance.service;
```

**Controllers:**
```java
// Cũ
package com.example.hrm.controller;

// Mới
package com.example.hrm.modules.attendance.controller;
```

**Repositories:**
```java
// Cũ
package com.example.hrm.repository;

// Mới
package com.example.hrm.modules.attendance.repository;
```

**DTOs:**
```java
// Cũ
package com.example.hrm.dto.request;
package com.example.hrm.dto.response;

// Mới
package com.example.hrm.modules.attendance.dto.request;
package com.example.hrm.modules.attendance.dto.response;
```

**Mappers:**
```java
// Cũ
package com.example.hrm.mapper;

// Mới
package com.example.hrm.modules.attendance.mapper;
```

**Configuration & Shared:**
```java
// Cũ
package com.example.hrm.configuration;
package com.example.hrm.exception;
package com.example.hrm.enums;

// Mới
package com.example.hrm.shared.config;
package com.example.hrm.shared.exception;
package com.example.hrm.shared.enums;
```

---

### 2️⃣ Cập Nhật Imports (Tất cả file)

**Trước:**
```java
import com.example.hrm.entity.Attendance;
import com.example.hrm.service.AttendanceService;
import com.example.hrm.repository.AttendanceRepository;
import com.example.hrm.dto.request.AttendanceCreateRequest;
import com.example.hrm.dto.response.AttendanceResponse;
import com.example.hrm.mapper.AttendanceMapper;
import com.example.hrm.exception.ResourceNotFoundException;
import com.example.hrm.configuration.SecurityConfiguration;
```

**Sau:**
```java
import com.example.hrm.modules.attendance.entity.Attendance;
import com.example.hrm.modules.attendance.service.AttendanceService;
import com.example.hrm.modules.attendance.repository.AttendanceRepository;
import com.example.hrm.modules.attendance.dto.request.AttendanceCreateRequest;
import com.example.hrm.modules.attendance.dto.response.AttendanceResponse;
import com.example.hrm.modules.attendance.mapper.AttendanceMapper;
import com.example.hrm.shared.exception.ResourceNotFoundException;
import com.example.hrm.shared.config.SecurityConfiguration;
```

---

### 3️⃣ File Cần Di Chuyển vào Shared Folder

```bash
src/main/java/com/example/hrm/shared/

config/
├── JwtKeyStore.java
├── RedisConfig.java
├── SecurityConfiguration.java
├── StartUpTask.java
├── WebConfig.java
├── ... (tất cả Spring Configuration classes)

exception/
├── ResourceNotFoundException.java
├── BusinessException.java
├── ValidationException.java
├── ... (tất cả Exception classes)

enums/
├── AdjustmentType.java
├── AttendanceStatus.java
├── BasedOn.java
├── BreakType.java
├── ContractStatus.java
├── ... (tất cả Enum classes)

redis/
├── RedisService.java
├── RedisUtil.java
├── ... (Redis utilities)

util/
├── DateUtil.java
├── StringUtil.java
├── ValidationUtil.java
├── ... (utility classes)
```

---

### 4️⃣ Cập Nhật HrmApplication.java

```java
package com.example.hrm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * HRM Application - Main Entry Point
 * 
 * Cấu hình ComponentScan để Spring tìm các components ở tất cả modules
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.example.hrm.modules",
    "com.example.hrm.shared",
    "com.example.hrm.call_api"
})
public class HrmApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(HrmApplication.class, args);
    }
}
```

---

### 5️⃣ Cập Nhật Mappers (MapStruct nếu sử dụng)

Nếu sử dụng MapStruct, cập nhật `@Mapper` configuration:

```java
package com.example.hrm.modules.attendance.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * AttendanceMapper - MapStruct Configuration
 */
@Mapper(componentModel = "spring")
public interface AttendanceMapper {
    
    AttendanceMapper INSTANCE = Mappers.getMapper(AttendanceMapper.class);
    
    AttendanceResponse toResponse(Attendance entity);
    
    Attendance toEntity(AttendanceCreateRequest request);
}
```

---

### 6️⃣ Dependency Injection Giữa Modules

**Không nên làm:**
```java
// ❌ Import trực tiếp từ module khác
import com.example.hrm.modules.employee.entity.Employee;
import com.example.hrm.modules.employee.service.EmployeeService;
```

**Nên làm:**
```java
// ✅ Inject service thông qua dependency injection
@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final EmployeeService employeeService;  // Inject từ module khác
    
    public void recordAttendance(Long employeeId) {
        Employee employee = employeeService.getEmployee(employeeId);
        // ...
    }
}
```

---

### 7️⃣ Cập Nhật pom.xml (nếu cần)

Nếu bạn muốn đảm bảo MapStruct hoặc các plugin khác hoạt động đúng:

```xml
<plugins>
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.11.0</version>
        <configuration>
            <source>21</source>
            <target>21</target>
            <annotationProcessorPaths>
                <!-- MapStruct processor if using MapStruct -->
                <path>
                    <groupId>org.mapstruct</groupId>
                    <artifactId>mapstruct-processor</artifactId>
                    <version>1.5.5.Final</version>
                </path>
                <!-- Lombok processor -->
                <path>
                    <groupId>org.projectlombok</groupId>
                    <artifactId>lombok</artifactId>
                    <version>1.18.30</version>
                </path>
            </annotationProcessorPaths>
        </configuration>
    </plugin>
</plugins>
```

---

### 8️⃣ Kiểm Tra Compilation & Testing

```bash
# Xóa build cũ
mvn clean

# Compile lại
mvn compile

# Chạy tests
mvn test

# Build package
mvn package

# Chạy application
mvn spring-boot:run
```

---

### 9️⃣ Cấu Trúc Service Layers (Optional)

Nếu bạn muốn có interface cho services (best practice):

```
modules/attendance/
├── service/
│   ├── AttendanceService.java          (Interface)
│   ├── impl/
│   │   └── AttendanceServiceImpl.java   (Implementation)
│   ├── AttendanceCheckInService.java
│   └── ...
```

**Service Interface:**
```java
package com.example.hrm.modules.attendance.service;

import com.example.hrm.modules.attendance.dto.request.AttendanceCreateRequest;
import com.example.hrm.modules.attendance.dto.response.AttendanceResponse;

public interface AttendanceService {
    AttendanceResponse createAttendance(AttendanceCreateRequest request);
    AttendanceResponse getAttendance(Long id);
    // ...
}
```

**Service Implementation:**
```java
package com.example.hrm.modules.attendance.service.impl;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {
    
    private final AttendanceRepository attendanceRepository;
    private final AttendanceMapper attendanceMapper;
    
    @Override
    public AttendanceResponse createAttendance(AttendanceCreateRequest request) {
        // Implementation
    }
    // ...
}
```

---

## 🔍 Verification Checklist

- [ ] Tất cả file đã được di chuyển vào folder tương ứng
- [ ] Package declarations đã được cập nhật
- [ ] Imports đã được cập nhật (có thể dùng IDE refactor)
- [ ] HrmApplication.java có `@ComponentScan` đúng
- [ ] `mvn clean compile` chạy thành công (0 errors)
- [ ] `mvn test` pass
- [ ] Application khởi động thành công
- [ ] Các REST endpoints hoạt động đúng
- [ ] Database migrations chạy thành công (nếu có)

---

## 🚀 Lệnh Hữu Ích

```bash
# IDE refactor imports (IntelliJ/VS Code)
Ctrl + Shift + O (Windows/Linux)
Cmd + Shift + O (Mac)

# Quick fix imports in IntelliJ
Alt + Enter

# Maven clean & compile
mvn clean compile

# Check for unused imports
mvn spotbugs:check

# Format code
mvn spotless:apply
```

---

## 📚 Tài Liệu Tham Khảo

- **PROJECT_STRUCTURE.md** - Tổng quan về cấu trúc mới
- **ENTITY_TEMPLATE.java** - Template cho Entity classes
- **SERVICE_TEMPLATE.java** - Template cho Service classes
- **CONTROLLER_TEMPLATE.java** - Template cho Controller classes
- **REPOSITORY_TEMPLATE.java** - Template cho Repository interfaces
- **MAPPER_TEMPLATE.java** - Template cho Mapper classes
- **DTO_REQUEST_TEMPLATE.java** - Template cho Request DTOs
- **DTO_RESPONSE_TEMPLATE.java** - Template cho Response DTOs

---

## ⚠️ Lỗi Thường Gặp & Giải Pháp

### Error: Cannot resolve symbol 'Attendance'
**Nguyên nhân:** Import chưa được cập nhật
**Giải pháp:** Cập nhật import path thành `com.example.hrm.modules.attendance.entity.Attendance`

### Error: Could not resolve all dependencies
**Nguyên nhân:** Spring không tìm thấy beans
**Giải pháp:** Kiểm tra `@ComponentScan` trong HrmApplication.java

### Error: Circular dependency detected
**Nguyên nhân:** Hai modules phụ thuộc vào nhau
**Giải pháp:** Tách phần chung vào shared package, hoặc sử dụng event-driven communication

### Error: Cannot find symbol 'mapper'
**Nguyên nhân:** Mapper chưa được inject đúng
**Giải pháp:** Kiểm tra `@Component` hoặc `@Mapper(componentModel = "spring")`

---

## 💡 Best Practices

1. **Một Module = Một Feature:** Mỗi module đại diện cho một tính năng trong hệ thống
2. **Tránh Circular Dependencies:** Module A không nên phụ thuộc vào Module B nếu B phụ thuộc vào A
3. **Shared Package:** Sử dụng shared cho các class được sử dụng bởi nhiều modules
4. **API Contracts:** Define clear interfaces giữa modules
5. **Documentation:** Mỗi module nên có README hoặc documentation
6. **Separation of Concerns:** Mỗi class nên có một trách nhiệm duy nhất

