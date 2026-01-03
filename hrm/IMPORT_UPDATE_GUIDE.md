# 🔄 Import Update Guide - Hướng Dẫn Cập Nhật Import

## 📝 Tổng Quan

Sau khi di chuyển file vào cấu trúc modular, bạn cần cập nhật import statements trong tất cả các file Java. Tài liệu này hướng dẫn cách thực hiện.

---

## 🛠️ Phương Pháp 1: Sử Dụng IDE (Khuyến Khích)

### IntelliJ IDEA

**Step 1: Mở Project**
1. File → Open Project → Chọn thư mục HRM

**Step 2: Refactor Imports**
1. Nút phải chuột vào Project Root
2. Chọn "Refactor" → "Analyze" → "Run Inspection by Name"
3. Tìm "Unused import" hoặc "Broken import"
4. Chạy inspection để tìm lỗi

**Step 3: Fix Imports Tự Động**
1. Sử dụng Ctrl + Shift + O (Windows/Linux) hoặc Cmd + Shift + O (Mac)
2. Hoặc: Edit → Optimize Imports

**Step 4: Fix Individual File**
1. Mở file
2. Nhấn Alt + Enter (Windows/Linux) hoặc Cmd + Enter (Mac)
3. Chọn "Add import"
4. Chọn class đúng từ danh sách

---

### Visual Studio Code / VS Code

**Extension Cần Thiết:**
- Extension Pack for Java
- Language Support for Java (Red Hat)

**Steps:**
1. Mở file Java
2. Hover vào broken import (đường đỏ)
3. Click "Quick Fix"
4. Chọn "Add import to ..."
5. Chọn package đúng

---

### Eclipse IDE

**Steps:**
1. Mở file Java
2. Ctrl + Shift + M (Quick Fix)
3. Chọn import đúng từ danh sách

---

## 🔍 Phương Pháp 2: Find & Replace (Manual)

### Mapping Table

| Old Import | New Import |
|-----------|-----------|
| `com.example.hrm.entity.Attendance` | `com.example.hrm.modules.attendance.entity.Attendance` |
| `com.example.hrm.entity.Employee` | `com.example.hrm.modules.employee.entity.Employee` |
| `com.example.hrm.service.AttendanceService` | `com.example.hrm.modules.attendance.service.AttendanceService` |
| `com.example.hrm.service.EmployeeService` | `com.example.hrm.modules.employee.service.EmployeeService` |
| `com.example.hrm.repository.AttendanceRepository` | `com.example.hrm.modules.attendance.repository.AttendanceRepository` |
| `com.example.hrm.controller.AttendanceController` | `com.example.hrm.modules.attendance.controller.AttendanceController` |
| `com.example.hrm.dto.request.AttendanceCreateRequest` | `com.example.hrm.modules.attendance.dto.request.AttendanceCreateRequest` |
| `com.example.hrm.dto.response.AttendanceResponse` | `com.example.hrm.modules.attendance.dto.response.AttendanceResponse` |
| `com.example.hrm.mapper.AttendanceMapper` | `com.example.hrm.modules.attendance.mapper.AttendanceMapper` |
| `com.example.hrm.exception.ResourceNotFoundException` | `com.example.hrm.shared.exception.ResourceNotFoundException` |
| `com.example.hrm.configuration.SecurityConfiguration` | `com.example.hrm.shared.config.SecurityConfiguration` |
| `com.example.hrm.enums.*` | `com.example.hrm.shared.enums.*` |

### Cách Sử Dụng Find & Replace

**IntelliJ IDEA:**
1. Ctrl + H → Open Find and Replace
2. Nhập: `import com.example.hrm.entity.`
3. Replace with: `import com.example.hrm.modules.attendance.entity.`
4. Chọn "Replace All" (cẩn thận!)

**VS Code:**
1. Ctrl + H → Open Find and Replace
2. Tương tự như trên

**Terminal/Command Line:**
```bash
# Sử dụng sed (Linux/Mac)
sed -i 's/import com\.example\.hrm\.entity\.Attendance;/import com.example.hrm.modules.attendance.entity.Attendance;/g' **/*.java

# Sử dụng PowerShell (Windows)
Get-ChildItem -Recurse -Filter "*.java" | ForEach-Object {
    (Get-Content $_.FullName) -replace `
    'import com\.example\.hrm\.entity\.Attendance;', `
    'import com.example.hrm.modules.attendance.entity.Attendance;' | `
    Set-Content $_.FullName
}
```

---

## 📋 Complete Import Migration List

### Attendance Module

```java
// Old → New
import com.example.hrm.entity.Attendance;
→ import com.example.hrm.modules.attendance.entity.Attendance;

import com.example.hrm.entity.AttendancePenalty;
→ import com.example.hrm.modules.attendance.entity.AttendancePenalty;

import com.example.hrm.entity.BreakTime;
→ import com.example.hrm.modules.attendance.entity.BreakTime;

import com.example.hrm.entity.OTRate;
→ import com.example.hrm.modules.attendance.entity.OTRate;

import com.example.hrm.service.AttendanceService;
→ import com.example.hrm.modules.attendance.service.AttendanceService;

import com.example.hrm.repository.AttendanceRepository;
→ import com.example.hrm.modules.attendance.repository.AttendanceRepository;

import com.example.hrm.controller.AttendanceController;
→ import com.example.hrm.modules.attendance.controller.AttendanceController;

import com.example.hrm.mapper.AttendanceMapper;
→ import com.example.hrm.modules.attendance.mapper.AttendanceMapper;
```

### Employee Module

```java
import com.example.hrm.entity.Employee;
→ import com.example.hrm.modules.employee.entity.Employee;

import com.example.hrm.entity.Contact;
→ import com.example.hrm.modules.employee.entity.Contact;

import com.example.hrm.service.EmployeeService;
→ import com.example.hrm.modules.employee.service.EmployeeService;

// ... tương tự cho các entities, services, controllers khác
```

### Organization Module

```java
import com.example.hrm.entity.Department;
→ import com.example.hrm.modules.organization.entity.Department;

import com.example.hrm.entity.SubDepartment;
→ import com.example.hrm.modules.organization.entity.SubDepartment;

import com.example.hrm.entity.Address;
→ import com.example.hrm.modules.organization.entity.Address;

import com.example.hrm.entity.Province;
→ import com.example.hrm.modules.organization.entity.Province;

import com.example.hrm.entity.District;
→ import com.example.hrm.modules.organization.entity.District;

import com.example.hrm.entity.Ward;
→ import com.example.hrm.modules.organization.entity.Ward;

// ... services, controllers ...
```

### Payroll Module

```java
import com.example.hrm.entity.Payroll;
→ import com.example.hrm.modules.payroll.entity.Payroll;

import com.example.hrm.entity.PayrollCycle;
→ import com.example.hrm.modules.payroll.entity.PayrollCycle;

import com.example.hrm.entity.SalaryContract;
→ import com.example.hrm.modules.payroll.entity.SalaryContract;

// ... services, controllers ...
```

### Penalty Module

```java
import com.example.hrm.entity.PenaltyRule;
→ import com.example.hrm.modules.penalty.entity.PenaltyRule;

import com.example.hrm.service.PenaltyRuleService;
→ import com.example.hrm.modules.penalty.service.PenaltyRuleService;

// ... repositories, controllers ...
```

### Contract Module

```java
import com.example.hrm.entity.Contract;
→ import com.example.hrm.modules.contract.entity.Contract;

import com.example.hrm.service.ContractService;
→ import com.example.hrm.modules.contract.service.ContractService;

// ... repositories, controllers ...
```

### File Module

```java
import com.example.hrm.entity.FileAttachment;
→ import com.example.hrm.modules.file.entity.FileAttachment;

import com.example.hrm.service.FileUploadService;
→ import com.example.hrm.modules.file.service.FileUploadService;

import com.example.hrm.service.FileAttachmentService;
→ import com.example.hrm.modules.file.service.FileAttachmentService;
```

### User Module

```java
import com.example.hrm.entity.UserAccount;
→ import com.example.hrm.modules.user.entity.UserAccount;

import com.example.hrm.entity.Role;
→ import com.example.hrm.modules.user.entity.Role;

import com.example.hrm.entity.Permission;
→ import com.example.hrm.modules.user.entity.Permission;

import com.example.hrm.service.UserAccountService;
→ import com.example.hrm.modules.user.service.UserAccountService;

import com.example.hrm.service.RoleService;
→ import com.example.hrm.modules.user.service.RoleService;
```

### Auth Module

```java
import com.example.hrm.service.AuthService;
→ import com.example.hrm.modules.auth.service.AuthService;

import com.example.hrm.service.JwtService;
→ import com.example.hrm.modules.auth.service.JwtService;

import com.example.hrm.controller.AuthController;
→ import com.example.hrm.modules.auth.controller.AuthController;
```

### Face Recognition Module

```java
import com.example.hrm.service.FaceRecognitionService;
→ import com.example.hrm.modules.face-recognition.service.FaceRecognitionService;

import com.example.hrm.controller.FaceRecognitionController;
→ import com.example.hrm.modules.face-recognition.controller.FaceRecognitionController;
```

### Email Module

```java
import com.example.hrm.service.EmailService;
→ import com.example.hrm.modules.email.service.EmailService;

import com.example.hrm.controller.EmailTestController;
→ import com.example.hrm.modules.email.controller.EmailTestController;
```

### Shared Imports (Most Important!)

```java
// Exceptions
import com.example.hrm.exception.*;
→ import com.example.hrm.shared.exception.*;

import com.example.hrm.exception.ResourceNotFoundException;
→ import com.example.hrm.shared.exception.ResourceNotFoundException;

// Configuration
import com.example.hrm.configuration.*;
→ import com.example.hrm.shared.config.*;

import com.example.hrm.configuration.SecurityConfiguration;
→ import com.example.hrm.shared.config.SecurityConfiguration;

import com.example.hrm.configuration.RedisConfig;
→ import com.example.hrm.shared.config.RedisConfig;

import com.example.hrm.configuration.JwtKeyStore;
→ import com.example.hrm.shared.config.JwtKeyStore;

// Enums
import com.example.hrm.enums.*;
→ import com.example.hrm.shared.enums.*;

// Redis
import com.example.hrm.redis.*;
→ import com.example.hrm.shared.redis.*;

// Call API
import com.example.hrm.call_api.*;
→ import com.example.hrm.call_api.*;  (Giữ nguyên)
```

---

## ✅ Verification Steps

Sau khi cập nhật imports, kiểm tra:

### 1. Compile Check
```bash
mvn clean compile
```
Nếu thành công → Không có broken imports

### 2. IDE Error Check
- Mở IDE
- Chờ indexing xong
- Không có đường đỏ (error) trong code

### 3. Search for Old Imports
```bash
# Tìm các import cũ còn sót lại
grep -r "import com.example.hrm.entity" src/main/java --include="*.java"
grep -r "import com.example.hrm.service" src/main/java --include="*.java"
grep -r "import com.example.hrm.controller" src/main/java --include="*.java"
```

### 4. Run Tests
```bash
mvn test
```

### 5. Start Application
```bash
mvn spring-boot:run
```

---

## 🎯 Quick Reference - Find & Replace Patterns

Sử dụng các pattern này với Find & Replace (Regular Expression ON):

```regex
# Tất cả entity imports
Find: import com\.example\.hrm\.entity\.([A-Za-z]+);
Replace: import com.example.hrm.modules.MODULENAME.entity.$1;

# Tất cả service imports
Find: import com\.example\.hrm\.service\.([A-Za-z]+);
Replace: import com.example.hrm.modules.MODULENAME.service.$1;

# Tất cả controller imports
Find: import com\.example\.hrm\.controller\.([A-Za-z]+);
Replace: import com.example.hrm.modules.MODULENAME.controller.$1;

# Tất cả dto/request imports
Find: import com\.example\.hrm\.dto\.request\.([A-Za-z]+);
Replace: import com.example.hrm.modules.MODULENAME.dto.request.$1;

# Tất cả dto/response imports
Find: import com\.example\.hrm\.dto\.response\.([A-Za-z]+);
Replace: import com.example.hrm.modules.MODULENAME.dto.response.$1;

# Exception imports
Find: import com\.example\.hrm\.exception\.([A-Za-z]+);
Replace: import com.example.hrm.shared.exception.$1;

# Configuration imports
Find: import com\.example\.hrm\.configuration\.([A-Za-z]+);
Replace: import com.example.hrm.shared.config.$1;

# Enum imports
Find: import com\.example\.hrm\.enums\.([A-Za-z]+);
Replace: import com.example.hrm.shared.enums.$1;
```

---

## 💡 Troubleshooting

### Problem: Nhiều class có tên giống nhau
**Giải pháp:**
- Không nên dùng Find & Replace All tự động
- Kiểm tra từng import trước khi replace
- Sử dụng IDE's Quick Fix thay vào đó

### Problem: Cannot resolve symbol
**Giải pháp:**
1. Kiểm tra tên package đúng chưa
2. Kiểm tra file có tồn tại không
3. Rebuild project: Maven → Rebuild Project

### Problem: Circular dependency error
**Giải pháp:**
- Xem lại imports
- Đảm bảo A không import B nếu B import A
- Tách shared code ra

---

## 📞 Cần Giúp?

Nếu bạn không chắc import nào đúng:

1. Kiểm tra file tồn tại ở path nào
2. Xem lại MIGRATION_GUIDE.md
3. Sử dụng IDE's "Find Class" (Ctrl + Shift + T / Cmd + Shift + T)

