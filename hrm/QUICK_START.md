# 🚀 Quick Start - Bắt Đầu Migration Ngay

## ⏱️ 5 Phút Setup

### Bước 1: Đọc Overview (1 min)
```bash
Mở file: README_MODULAR_STRUCTURE.md
→ Hiểu được cấu trúc mới
```

### Bước 2: Xem Architecture (2 min)
```bash
Mở file: ARCHITECTURE_VISUALIZATION.md
→ Hiểu được interaction giữa modules
```

### Bước 3: Đọc Migration Guide (1 min)
```bash
Mở file: MIGRATION_GUIDE.md
→ Biết cần làm gì tiếp theo
```

### Bước 4: Bắt Đầu Migration (1 min)
```bash
Thực hiện 4 bước trong section "Step by Step" dưới
```

---

## 📋 Step by Step - Hướng Dẫn Chi Tiết

### PHASE 1: Prepare (5 minutes)

#### 1.1 Backup Project (nên làm trước)
```bash
# Duplicate thư mục HRM hiện tại
cp -r c:\project\hrm\hrm c:\project\hrm\hrm.backup

# Hoặc dùng Git
git commit -am "Before modular refactoring"
git branch before-refactoring
```

#### 1.2 Mở Project trong IDE
- IntelliJ IDEA, VS Code, hoặc Eclipse
- Mở thư mục HRM

#### 1.3 Kiểm tra Project
```bash
# Đảm bảo project build được
mvn clean compile

# Kết quả mong muốn: BUILD SUCCESS
```

---

### PHASE 2: Migrate Files (30-60 minutes)

#### 2.1 Option A: Dùng Script PowerShell (TỰ ĐỘNG)
```bash
# Cd tới thư mục project
cd c:\project\hrm\hrm

# Chạy script
.\migrate-to-modular.ps1

# Kết quả: Tất cả file sẽ được di chuyển
```

#### 2.2 Option B: Manual Migration (KIỂM SOÁT)

**Bước 1: Di chuyển Entities**
```
Cấu trúc cũ:        →    Cấu trúc mới:
entity/Attendance.java   modules/attendance/entity/Attendance.java
entity/Employee.java     modules/employee/entity/Employee.java
...
```

**Cách làm:**
1. Mở folder `src/main/java/com/example/hrm/entity`
2. Chọn file Attendance.java
3. Chuột phải → Cut (Ctrl + X)
4. Navigate đến `src/main/java/com/example/hrm/modules/attendance/entity`
5. Chuột phải → Paste (Ctrl + V)
6. Lặp lại cho tất cả entities

**Bước 2: Di chuyển Services**
```
Tương tự như entities, nhưng di chuyển vào service/ folder
```

**Bước 3: Di chuyển Controllers**
**Bước 4: Di chuyển DTOs**
**Bước 5: Di chuyển Repositories**
**Bước 6: Di chuyển Mappers**
**Bước 7: Di chuyển Shared Files**

---

### PHASE 3: Update Package Declarations (20-30 minutes)

#### 3.1 Cập nhật tất cả Java Files

Cho mỗi file Java sau khi di chuyển:

**Trước:**
```java
package com.example.hrm.entity;

public class Attendance {
    ...
}
```

**Sau:**
```java
package com.example.hrm.modules.attendance.entity;

public class Attendance {
    ...
}
```

**Cách làm:**
1. Mở file vừa di chuyển
2. Cập nhật dòng `package` ở đầu file
3. Save file

**Quick Tip:** Sử dụng Find & Replace
- IntelliJ: Ctrl + H
- Pattern: `package com.example.hrm.entity;` → `package com.example.hrm.modules.attendance.entity;`

---

### PHASE 4: Update Imports (30-45 minutes)

#### 4.1 Sử dụng IDE Auto-Fix (Khuyến Khích)

**IntelliJ IDEA:**
```
1. Ctr + Shift + O (Optimize Imports)
2. Hoặc Alt + Enter trên error → Add import
```

**VS Code:**
```
1. Ctrl + H (Find & Replace)
2. Tìm old import, replace bằng new import
3. Xem IMPORT_UPDATE_GUIDE.md để có danh sách complete
```

#### 4.2 Manual Import Updates

Xem file: `IMPORT_UPDATE_GUIDE.md` cho danh sách đầy đủ

Key imports cần thay đổi:
```java
// Cũ → Mới
import com.example.hrm.entity.* → import com.example.hrm.modules.{module}.entity.*
import com.example.hrm.service.* → import com.example.hrm.modules.{module}.service.*
import com.example.hrm.controller.* → import com.example.hrm.modules.{module}.controller.*
import com.example.hrm.exception.* → import com.example.hrm.shared.exception.*
import com.example.hrm.configuration.* → import com.example.hrm.shared.config.*
import com.example.hrm.enums.* → import com.example.hrm.shared.enums.*
```

---

### PHASE 5: Update HrmApplication.java (5 minutes)

#### 5.1 Cập nhật File

**File:** `src/main/java/com/example/hrm/HrmApplication.java`

**Trước:**
```java
@SpringBootApplication
public class HrmApplication {
    public static void main(String[] args) {
        SpringApplication.run(HrmApplication.class, args);
    }
}
```

**Sau:**
```java
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

Thêm import:
```java
import org.springframework.context.annotation.ComponentScan;
```

---

### PHASE 6: Verify & Test (15-30 minutes)

#### 6.1 Check Compilation
```bash
mvn clean compile
```
Expected: `BUILD SUCCESS`

#### 6.2 Fix Remaining Errors
Nếu có errors:
1. Đọc error message
2. Tìm lỗi trong file tương ứng
3. Sửa import hoặc package name

#### 6.3 Run Tests
```bash
mvn test
```

#### 6.4 Start Application
```bash
mvn spring-boot:run
```

Expected:
```
Started HrmApplication in X.XXX seconds
```

#### 6.5 Test APIs
```bash
# Kiểm tra một vài endpoints
curl http://localhost:8080/api/v1/attendance
curl http://localhost:8080/api/v1/employees
# ... etc
```

---

## 🎯 Expected Timeline

| Phase | Task | Time |
|-------|------|------|
| 1 | Prepare | 5 min |
| 2 | Migrate Files | 30-60 min |
| 3 | Update Packages | 20-30 min |
| 4 | Update Imports | 30-45 min |
| 5 | Update Config | 5 min |
| 6 | Verify & Test | 15-30 min |
| **TOTAL** | **Full Migration** | **2-3 hours** |

---

## ✅ Checklist - Verify After Migration

- [ ] All files moved to correct modules
- [ ] All package declarations updated
- [ ] All imports updated
- [ ] HrmApplication.java has @ComponentScan
- [ ] `mvn clean compile` → BUILD SUCCESS
- [ ] `mvn test` → All tests pass (or no regression)
- [ ] `mvn spring-boot:run` → Application starts
- [ ] Test 3-5 API endpoints → Working
- [ ] Database migrations run successfully
- [ ] No console errors or warnings

---

## 🆘 Troubleshooting Quick Reference

### Error: "Cannot resolve symbol 'Attendance'"
**Fix:** Update import to `com.example.hrm.modules.attendance.entity.Attendance`

### Error: "Could not autowire bean of type EmployeeService"
**Fix:** Check HrmApplication has correct @ComponentScan

### Error: "compilation failure" 
**Fix:** Run `mvn clean compile` to see detailed errors

### Error: "Circular dependency detected"
**Fix:** Move shared code to shared/ folder

### Error: Database connection failed
**Fix:** Check application.yml in resources folder still accessible

---

## 📁 File Locations for Reference

```
c:\project\hrm\hrm\
├── README_MODULAR_STRUCTURE.md      ← Start here (Overview)
├── ARCHITECTURE_VISUALIZATION.md    ← Understanding
├── MIGRATION_GUIDE.md               ← Detailed steps
├── IMPORT_UPDATE_GUIDE.md           ← Import changes
├── PROJECT_STRUCTURE.md             ← Structure details
├── migrate-to-modular.ps1           ← Auto-migration script
├── QUICK_START.md                   ← This file
│
├── Templates (for reference):
├── ENTITY_TEMPLATE.java
├── SERVICE_TEMPLATE.java
├── CONTROLLER_TEMPLATE.java
├── REPOSITORY_TEMPLATE.java
├── MAPPER_TEMPLATE.java
├── DTO_REQUEST_TEMPLATE.java
├── DTO_RESPONSE_TEMPLATE.java
├── HRMAPPLICATION_TEMPLATE.java
│
└── src/
    └── main/
        └── java/com/example/hrm/
            ├── modules/             ← New modular structure
            ├── shared/              ← New shared resources
            └── HrmApplication.java  ← Update this file
```

---

## 💡 Pro Tips

1. **Backup First!** 
   - Tạo backup trước khi bắt đầu
   - Git commit là cách tốt nhất

2. **Use IDE Refactor**
   - IDE có thể tự động update imports
   - Tiết kiệm thời gian hơn manual

3. **One Module at a Time**
   - Migrate attendance, test nó hoạt động
   - Sau đó migrate employee, test nó
   - Tránh di chuyển tất cả cùng lúc rồi mới test

4. **Keep Tests Running**
   - Chạy test sau mỗi phase
   - Phát hiện lỗi sớm

5. **Documentation is Key**
   - Giữ lại các document này
   - Dùng cho onboarding team member mới

---

## 🔗 Next Steps Sau Migration

1. **Code Review**
   - Review package structure
   - Review imports
   - Verify module boundaries

2. **Team Communication**
   - Explain new structure tới team
   - Training nếu cần

3. **Update CI/CD**
   - Update build pipeline nếu có
   - Ensure tests run

4. **Documentation**
   - Update project README
   - Document module responsibilities

5. **Code Generation (Optional)**
   - Sử dụng templates để tạo new files
   - Maintain consistency

---

## 📞 Questions?

Nếu bạn gặp vấn đề:

1. Kiểm tra relevant .md file
2. Tìm error message trong console
3. Xem checklist ở trên
4. Review template files cho reference

---

**Good Luck!** 🚀

Đây là cái gì sẽ giúp project của bạn:
- ✅ Dễ maintain
- ✅ Dễ scale
- ✅ Dễ phân công work
- ✅ Dễ test
- ✅ Professional architecture

**Time to migrate: ~2-3 hours**
**Benefit: Lifetime** 😊

