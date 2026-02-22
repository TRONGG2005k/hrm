# 📊 BÁO CÁO PHÂN TÍCH BẢO MẬT API - HRM SYSTEM

## 📋 TỔNG QUAN

**Ngày phân tích:** 21/02/2026  
**Tổng số Controller:** 25  
**Tổng số API Endpoint:** ~80+  
**Các Role trong hệ thống:** `ROLE_EMPLOYEE`, `ROLE_MANAGER`, `ROLE_HR_STAFF`, `ROLE_HR_MANAGER`, `ROLE_ADMIN`

---

## 🔴 CẢNH BÁO BẢO MẬT NGHIÊM TRỌNG

### Vấn đề hiện tại trong SecurityConfiguration:
```java
.anyRequest().permitAll()  // ⚠️ TẤT CẢ API ĐỀU ĐƯỢC TRUY CẬP KHÔNG CẦN XÁC THỰC!
```

**Đề xuất sửa đổi:**
```java
.anyRequest().authenticated()  // ✅ Yêu cầu tất cả request phải xác thực
```

---

## 📂 PHÂN LOẠI API

### 1️⃣ PUBLIC API (Không cần xác thực)

| STT | Endpoint | Method | Mô tả | Lý do public |
|-----|----------|--------|-------|--------------|
| 1 | `/api/v1/auth/login` | POST | Đăng nhập | Cần để lấy token |
| 2 | `/api/v1/auth/refresh` | POST | Refresh token | Cần để làm mới access token |
| 3 | `/api/v1/auth/activate` | POST | Kích hoạt tài khoản | Kích hoạt lần đầu |
| 4 | `/api/v1/attendance/scan` | POST | Quét khuôn mặt chấm công | Máy chấm công không cần đăng nhập |
| 5 | `/api/v1/employees/faces/recognize` | POST | Nhận diện khuôn mặt | Máy chấm công sử dụng |
| 6 | `/hello` | GET | Health check | Kiểm tra server |

---

### 2️⃣ AUTHENTICATED API (Cần xác thực - Chưa phân quyền cụ thể)

#### 🔐 Auth Module
| Endpoint | Method | Mô tả | Đề xuất quyền |
|----------|--------|-------|---------------|
| `/api/v1/auth/logout` | DELETE | Đăng xuất | `Any Authenticated` |
| `/api/v1/auth/logoutAll` | DELETE | Đăng xuất tất cả thiết bị | `Any Authenticated` |

---

#### 👤 User Management Module
| Endpoint | Method | Mô tả | Đề xuất quyền |
|----------|--------|-------|---------------|
| `/api/v1/user-accounts` | GET | Lấy danh sách tài khoản | `ROLE_ADMIN`, `ROLE_HR_MANAGER` |
| `/api/v1/user-accounts` | POST | Tạo tài khoản thủ công | `ROLE_ADMIN`, `ROLE_HR_MANAGER` |
| `/api/v1/user-accounts/auto/{id}` | POST | Tạo tài khoản tự động | `ROLE_ADMIN`, `ROLE_HR_MANAGER` |
| `/api/v1/user-accounts/{id}` | GET | Lấy chi tiết tài khoản | `Any Authenticated` (xem của mình) / `ROLE_HR_MANAGER` (xem tất cả) |
| `/api/v1/user-accounts/{id}` | PUT | Cập nhật tài khoản | `Any Authenticated` (củamình) / `ROLE_HR_MANAGER` (cập nhật tất cả) |
| `/api/v1/user-accounts/{id}` | DELETE | Xóa tài khoản | `ROLE_ADMIN` |
| `/api/v1/roles` | GET | Lấy danh sách roles | `ROLE_ADMIN`, `ROLE_HR_MANAGER` |
| `/api/v1/roles` | POST | Tạo role | `ROLE_ADMIN` |
| `/api/v1/roles/{id}` | GET | Lấy chi tiết role | `ROLE_ADMIN`, `ROLE_HR_MANAGER` |
| `/api/v1/roles/{id}` | PUT | Cập nhật role | `ROLE_ADMIN` |
| `/api/v1/roles/{id}` | DELETE | Xóa role | `ROLE_ADMIN` |

---

#### 👥 Employee Module
| Endpoint | Method | Mô tả | Đề xuất quyền |
|----------|--------|-------|---------------|
| `/api/v1/employees` | GET | Lấy danh sách nhân viên | `Any Authenticated` |
| `/api/v1/employees/{id}` | GET | Lấy chi tiết nhân viên | `Any Authenticated` (xem của mình) / `ROLE_MANAGER` (xem team) / `ROLE_HR_STAFF` (xem tất cả) |
| `/api/v1/employees` | POST | Tạo nhân viên | `ROLE_HR_STAFF`, `ROLE_HR_MANAGER`, `ROLE_ADMIN` |
| `/api/v1/employees/{id}` | PUT | Cập nhật nhân viên | `Any Authenticated` (củamình) / `ROLE_HR_STAFF` (cập nhật tất cả) |
| `/api/v1/employees/{id}` | DELETE | Xóa nhân viên | `ROLE_HR_MANAGER`, `ROLE_ADMIN` |
| `/api/v1/employees/import` | POST | Import nhân viên từ Excel | `ROLE_HR_STAFF`, `ROLE_HR_MANAGER` |
| `/api/v1/employees/import-or-update` | POST | Import/cập nhật nhân viên | `ROLE_HR_STAFF`, `ROLE_HR_MANAGER` |
| `/api/v1/employees/import-files` | POST | Import file đính kèm | `ROLE_HR_STAFF`, `ROLE_HR_MANAGER` |
| `/api/v1/employees/export` | GET | Export nhân viên ra Excel | `ROLE_HR_STAFF`, `ROLE_HR_MANAGER` |

---

#### 🏠 Address & Location Module
| Endpoint | Method | Mô tả | Đề xuất quyền |
|----------|--------|-------|---------------|
| `/api/v1/addresses` | GET | Lấy danh sách địa chỉ | `Any Authenticated` |
| `/api/v1/addresses/{id}` | GET | Lấy chi tiết địa chỉ | `Any Authenticated` |
| `/api/v1/addresses` | POST | Tạo địa chỉ | `Any Authenticated` |
| `/api/v1/addresses/{id}` | PUT | Cập nhật địa chỉ | `Any Authenticated` (củamình) |
| `/api/v1/addresses/{id}` | DELETE | Xóa địa chỉ | `Any Authenticated` (củamình) / `ROLE_HR_STAFF` |
| `/api/v1/provinces` | GET | Lấy danh sách tỉnh/thành | `Any Authenticated` |
| `/api/v1/provinces/{id}` | GET | Lấy chi tiết tỉnh/thành | `Any Authenticated` |
| `/api/v1/districts` | GET | Lấy danh sách quận/huyện | `Any Authenticated` |
| `/api/v1/districts/province/{provinceId}` | GET | Lấy quận theo tỉnh | `Any Authenticated` |
| `/api/v1/districts/{id}` | GET | Lấy chi tiết quận/huyện | `Any Authenticated` |
| `/api/v1/wards` | GET | Lấy danh sách phường/xã | `Any Authenticated` |
| `/api/v1/wards/district/{districtId}` | GET | Lấy phường theo quận | `Any Authenticated` |
| `/api/v1/wards/{id}` | GET | Lấy chi tiết phường/xã | `Any Authenticated` |
| `/api/v1/contacts` | GET | Lấy danh sách liên hệ | `Any Authenticated` |
| `/api/v1/contacts/{id}` | GET | Lấy chi tiết liên hệ | `Any Authenticated` |
| `/api/v1/contacts` | POST | Tạo liên hệ | `Any Authenticated` |
| `/api/v1/contacts/{id}` | PUT | Cập nhật liên hệ | `Any Authenticated` (củamình) |
| `/api/v1/contacts/{id}` | DELETE | Xóa liên hệ | `Any Authenticated` (củamình) |

---

#### 📋 Contract Module
| Endpoint | Method | Mô tả | Đề xuất quyền |
|----------|--------|-------|---------------|
| `/api/v1/contract` | GET | Lấy danh sách hợp đồng | `ROLE_HR_STAFF`, `ROLE_HR_MANAGER`, `ROLE_ADMIN` |
| `/api/v1/contract/{id}` | GET | Lấy chi tiết hợp đồng | `Any Authenticated` (xem của mình) / `ROLE_HR_STAFF` |
| `/api/v1/contract` | POST | Tạo hợp đồng | `ROLE_HR_STAFF`, `ROLE_HR_MANAGER` |
| `/api/v1/contract/{id}` | PUT | Cập nhật hợp đồng | `ROLE_HR_STAFF`, `ROLE_HR_MANAGER` |
| `/api/v1/contract/not-active` | GET | Lấy hợp đồng chưa active | `ROLE_HR_STAFF`, `ROLE_HR_MANAGER` |
| `/api/v1/contract/{contractId}/approve` | POST | Duyệt hợp đồng | `ROLE_HR_MANAGER`, `ROLE_ADMIN` |
| `/api/v1/contract/import` | POST | Import hợp đồng | `ROLE_HR_STAFF`, `ROLE_HR_MANAGER` |
| `/api/v1/contract/upload-files` | POST | Upload file hợp đồng | `ROLE_HR_STAFF`, `ROLE_HR_MANAGER` |
| `/api/v1/contract/export` | GET | Export hợp đồng | `ROLE_HR_STAFF`, `ROLE_HR_MANAGER` |
| `/api/v1/allowances` | GET | Lấy danh sách phụ cấp | `Any Authenticated` |
| `/api/v1/allowances/{id}` | GET | Lấy chi tiết phụ cấp | `Any Authenticated` |
| `/api/v1/allowances` | POST | Tạo phụ cấp | `ROLE_HR_MANAGER`, `ROLE_ADMIN` |
| `/api/v1/allowances/{id}` | PUT | Cập nhật phụ cấp | `ROLE_HR_MANAGER`, `ROLE_ADMIN` |
| `/api/v1/allowances/{id}` | DELETE | Xóa phụ cấp | `ROLE_HR_MANAGER`, `ROLE_ADMIN` |
| `/api/v1/allowances/import` | POST | Import phụ cấp | `ROLE_HR_MANAGER`, `ROLE_ADMIN` |
| `/api/v1/allowances/export` | GET | Export phụ cấp | `ROLE_HR_MANAGER`, `ROLE_ADMIN` |
| `/api/v1/allowance-rules` | POST | Tạo quy tắc phụ cấp | `ROLE_HR_MANAGER`, `ROLE_ADMIN` |
| `/api/v1/salary-contract` | POST | Tạo hợp đồng lương | `ROLE_HR_STAFF`, `ROLE_HR_MANAGER` |
| `/api/v1/salary-contract/employee/{employeeId}` | GET | Lấy hợp đồng lương theo NV | `Any Authenticated` (củamình) / `ROLE_HR_STAFF` |
| `/api/v1/salary-contract/{id}` | PUT | Cập nhật hợp đồng lương | `ROLE_HR_MANAGER`, `ROLE_ADMIN` |
| `/api/v1/salary-contract/{id}` | DELETE | Xóa hợp đồng lương | `ROLE_HR_MANAGER`, `ROLE_ADMIN` |
| `/api/v1/salary-adjustments` | POST | Tạo điều chỉnh lương | `ROLE_HR_STAFF`, `ROLE_HR_MANAGER` |
| `/api/v1/salary-adjustments/{id}` | PUT | Cập nhật điều chỉnh lương | `ROLE_HR_STAFF`, `ROLE_HR_MANAGER` |
| `/api/v1/salary-adjustments/{id}` | DELETE | Xóa điều chỉnh lương | `ROLE_HR_MANAGER`, `ROLE_ADMIN` |
| `/api/v1/salary-adjustments/employee/{employeeId}` | GET | Lấy điều chỉnh theo NV | `Any Authenticated` (củamình) / `ROLE_HR_STAFF` |
| `/api/v1/salary-adjustments/employee/{employeeId}/range` | GET | Lấy điều chỉnh trong kỳ | `ROLE_HR_STAFF`, `ROLE_HR_MANAGER` |

---

#### 📅 Attendance Module
| Endpoint | Method | Mô tả | Đề xuất quyền |
|----------|--------|-------|---------------|
| `/api/v1/attendance` | GET | Lấy danh sách chấm công | `ROLE_HR_STAFF`, `ROLE_HR_MANAGER`, `ROLE_MANAGER` |
| `/api/v1/attendance/{id}` | GET | Lấy chi tiết chấm công | `Any Authenticated` (củamình) / `ROLE_HR_STAFF` |
| `/api/v1/attendance/sub-department/{subDepartmentId}` | GET | Lấy chấm công theo phòng ban | `ROLE_MANAGER`, `ROLE_HR_STAFF`, `ROLE_HR_MANAGER` |
| `/api/v1/breaks` | GET | Lấy danh sách break time | `Any Authenticated` |
| `/api/v1/breaks` | POST | Tạo break time | `ROLE_HR_STAFF`, `ROLE_HR_MANAGER` |
| `/api/v1/breaks/{id}` | PUT | Cập nhật break time | `ROLE_HR_STAFF`, `ROLE_HR_MANAGER` |
| `/api/v1/breaks/{id}` | DELETE | Xóa break time | `ROLE_HR_STAFF`, `ROLE_HR_MANAGER` |
| `/api/v1/breaks/attendance/{attendanceId}` | GET | Lấy break theo attendance | `Any Authenticated` |
| `/api/v1/breaks/batch` | POST | Tạo break cho phòng ban | `ROLE_HR_STAFF`, `ROLE_HR_MANAGER` |
| `/api/v1/ot-rates` | GET | Lấy danh sách tỷ lệ OT | `Any Authenticated` |
| `/api/v1/ot-rates/{id}` | GET | Lấy chi tiết tỷ lệ OT | `Any Authenticated` |
| `/api/v1/ot-rates` | POST | Tạo tỷ lệ OT | `ROLE_HR_MANAGER`, `ROLE_ADMIN` |
| `/api/v1/ot-rates/{id}` | PUT | Cập nhật tỷ lệ OT | `ROLE_HR_MANAGER`, `ROLE_ADMIN` |
| `/api/v1/ot-rates/{id}` | DELETE | Xóa tỷ lệ OT | `ROLE_HR_MANAGER`, `ROLE_ADMIN` |
| `/api/v1/ot-rates/search` | GET | Tìm tỷ lệ OT theo ngày | `Any Authenticated` |

---

#### 🌴 Leave Module
| Endpoint | Method | Mô tả | Đề xuất quyền |
|----------|--------|-------|---------------|
| `/api/v1/leave-requests` | GET | Lấy danh sách đơn nghỉ | `ROLE_MANAGER`, `ROLE_HR_STAFF`, `ROLE_HR_MANAGER` |
| `/api/v1/leave-requests` | POST | Tạo đơn nghỉ phép | `Any Authenticated` |
| `/api/v1/leave-requests/{leaveId}` | GET | Lấy chi tiết đơn nghỉ | `Any Authenticated` (củamình) / `ROLE_MANAGER` |
| `/api/v1/leave-requests/{leaveId}` | PUT | Cập nhật đơn nghỉ | `Any Authenticated` (chưa duyệt) |
| `/api/v1/leave-requests/{leaveId}/approve` | POST | Duyệt/từ chối đơn nghỉ | `ROLE_MANAGER`, `ROLE_HR_MANAGER` |
| `/api/v1/leave-requests/list` | GET | Lấy đơn nghỉ theo trạng thái | `ROLE_MANAGER`, `ROLE_HR_STAFF`, `ROLE_HR_MANAGER` |
| `/api/v1/leave-requests/export` | GET | Export đơn nghỉ | `ROLE_HR_STAFF`, `ROLE_HR_MANAGER` |
| `/api/v1/leave-requests/import` | POST | Import đơn nghỉ | `ROLE_HR_STAFF`, `ROLE_HR_MANAGER` |
| `/api/leave-balance/me` | GET | Lấy số dư phép của tôi | `Any Authenticated` |

---

#### 💰 Payroll Module
| Endpoint | Method | Mô tả | Đề xuất quyền |
|----------|--------|-------|---------------|
| `/api/v1/payroll` | GET | Lấy danh sách bảng lương | `ROLE_HR_STAFF`, `ROLE_HR_MANAGER` |
| `/api/v1/payroll/create` | POST | Tạo bảng lương cho 1 NV | `ROLE_HR_STAFF`, `ROLE_HR_MANAGER` |
| `/api/v1/payroll` | POST | Tạo bảng lương cho tất cả | `ROLE_HR_MANAGER`, `ROLE_ADMIN` |
| `/api/v1/payroll/{employeeId}` | GET | Lấy lương chi tiết NV | `Any Authenticated` (củamình) / `ROLE_HR_STAFF` |
| `/api/v1/payroll/approval` | POST | Duyệt bảng lương | `ROLE_HR_MANAGER`, `ROLE_ADMIN` |
| `/api/v1/payroll/list` | GET | Lấy danh sách theo trạng thái | `ROLE_HR_STAFF`, `ROLE_HR_MANAGER` |
| `/api/v1/payroll/employee/{employeeId}` | GET | Lấy lương theo NV | `Any Authenticated` (củamình) / `ROLE_HR_STAFF` |
| `/api/v1/payroll/month` | GET | Lấy lương theo tháng | `ROLE_HR_STAFF`, `ROLE_HR_MANAGER` |
| `/api/v1/payroll/detail/{payrollId}` | GET | Lấy chi tiết bảng lương | `Any Authenticated` (củamình) / `ROLE_HR_STAFF` |
| `/api/v1/payroll-cycles` | POST | Tạo chu kỳ lương | `ROLE_HR_MANAGER`, `ROLE_ADMIN` |
| `/api/v1/payroll-cycles/active` | GET | Lấy chu kỳ lương active | `Any Authenticated` |
| `/api/v1/payroll-cycles/history` | GET | Lấy lịch sử chu kỳ lương | `ROLE_HR_MANAGER`, `ROLE_ADMIN` |

---

#### ⚠️ Penalty Module
| Endpoint | Method | Mô tả | Đề xuất quyền |
|----------|--------|-------|---------------|
| `/api/v1/penalty-rules` | GET | Lấy danh sách quy tắc phạt | `Any Authenticated` |
| `/api/v1/penalty-rules/active/list` | GET | Lấy quy tắc phạt đang active | `Any Authenticated` |
| `/api/v1/penalty-rules/{id}` | GET | Lấy chi tiết quy tắc phạt | `Any Authenticated` |
| `/api/v1/penalty-rules` | POST | Tạo quy tắc phạt | `ROLE_HR_MANAGER`, `ROLE_ADMIN` |
| `/api/v1/penalty-rules/{id}` | PUT | Cập nhật quy tắc phạt | `ROLE_HR_MANAGER`, `ROLE_ADMIN` |
| `/api/v1/penalty-rules/{id}` | DELETE | Xóa quy tắc phạt | `ROLE_HR_MANAGER`, `ROLE_ADMIN` |

---

#### 🏢 Organization Module
| Endpoint | Method | Mô tả | Đề xuất quyền |
|----------|--------|-------|---------------|
| `/api/v1/departments` | GET | Lấy danh sách phòng ban | `Any Authenticated` |
| `/api/v1/departments/{id}` | GET | Lấy chi tiết phòng ban | `Any Authenticated` |
| `/api/v1/departments` | POST | Tạo phòng ban | `ROLE_HR_MANAGER`, `ROLE_ADMIN` |
| `/api/v1/departments/{id}` | PUT | Cập nhật phòng ban | `ROLE_HR_MANAGER`, `ROLE_ADMIN` |
| `/api/v1/departments/{id}` | DELETE | Xóa phòng ban | `ROLE_ADMIN` |
| `/api/v1/departments/import` | POST | Import phòng ban | `ROLE_HR_MANAGER`, `ROLE_ADMIN` |
| `/api/v1/departments/export` | GET | Export phòng ban | `ROLE_HR_MANAGER`, `ROLE_ADMIN` |
| `/api/v1/sub-departments` | GET | Lấy danh sách bộ phận | `Any Authenticated` |
| `/api/v1/sub-departments/{id}` | GET | Lấy chi tiết bộ phận | `Any Authenticated` |
| `/api/v1/sub-departments/department/{departmentId}` | GET | Lấy bộ phận theo phòng ban | `Any Authenticated` |
| `/api/v1/sub-departments` | POST | Tạo bộ phận | `ROLE_HR_MANAGER`, `ROLE_ADMIN` |
| `/api/v1/sub-departments/{id}` | PUT | Cập nhật bộ phận | `ROLE_HR_MANAGER`, `ROLE_ADMIN` |
| `/api/v1/sub-departments/{id}` | DELETE | Xóa bộ phận | `ROLE_ADMIN` |
| `/api/v1/sub-departments/import` | POST | Import bộ phận | `ROLE_HR_MANAGER`, `ROLE_ADMIN` |
| `/api/v1/sub-departments/export` | GET | Export bộ phận | `ROLE_HR_MANAGER`, `ROLE_ADMIN` |
| `/api/v1/positions` | GET | Lấy danh sách chức vụ | `Any Authenticated` |
| `/api/v1/positions/{id}` | GET | Lấy chi tiết chức vụ | `Any Authenticated` |
| `/api/v1/positions` | POST | Tạo chức vụ | `ROLE_HR_MANAGER`, `ROLE_ADMIN` |
| `/api/v1/positions/{id}` | PUT | Cập nhật chức vụ | `ROLE_HR_MANAGER`, `ROLE_ADMIN` |
| `/api/v1/positions/import` | POST | Import chức vụ | `ROLE_HR_MANAGER`, `ROLE_ADMIN` |
| `/api/v1/positions/export` | GET | Export chức vụ | `ROLE_HR_MANAGER`, `ROLE_ADMIN` |

---

#### 📎 File Management Module
| Endpoint | Method | Mô tả | Đề xuất quyền |
|----------|--------|-------|---------------|
| `/api/v1/files/upload` | POST | Upload file | `Any Authenticated` |
| `/api/v1/files/download/{id}` | GET | Download file | `Any Authenticated` (có quyền truy cập entity) |
| `/api/v1/files/{id}` | DELETE | Xóa file | `Any Authenticated` (chủ sở hữu) / `ROLE_HR_STAFF` |

---

#### 👤 Face Recognition Module
| Endpoint | Method | Mô tả | Đề xuất quyền |
|----------|--------|-------|---------------|
| `/api/v1/employees/{employeeId}/faces` | POST | Đăng ký khuôn mặt | `ROLE_HR_STAFF`, `ROLE_HR_MANAGER` |
| `/api/v1/employees/{employeeId}/faces` | PUT | Cập nhật khuôn mặt | `ROLE_HR_STAFF`, `ROLE_HR_MANAGER` |
| `/api/v1/employees/{employeeId}/faces` | DELETE | Xóa khuôn mặt | `ROLE_HR_MANAGER`, `ROLE_ADMIN` |
| `/api/v1/employees/faces/batch` | POST | Đăng ký khuôn mặt hàng loạt | `ROLE_HR_MANAGER`, `ROLE_ADMIN` |

---

#### 🔧 Dev Module (Chỉ trong môi trường dev)
| Endpoint | Method | Mô tả | Đề xuất quyền |
|----------|--------|-------|---------------|
| `/api/v1/dev/mail/test` | GET | Test gửi email | `ROLE_ADMIN` (chỉ dev profile) |

---

## 📝 CODE MẪU CẤU HÌNH BẢO MẬT

```java
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                // ========== PUBLIC ENDPOINTS ==========
                .requestMatchers("/api/v1/auth/login",
                               "/api/v1/auth/refresh",
                               "/api/v1/auth/activate",
                               "/api/v1/attendance/scan",
                               "/api/v1/employees/faces/recognize",
                               "/hello").permitAll()
                
                // ========== DEV ENDPOINTS ==========
                .requestMatchers("/api/v1/dev/**").hasRole("ADMIN")
                
                // ========== USER MANAGEMENT ==========
                .requestMatchers(HttpMethod.GET, "/api/v1/user-accounts").hasAnyRole("ADMIN", "HR_MANAGER")
                .requestMatchers(HttpMethod.POST, "/api/v1/user-accounts").hasAnyRole("ADMIN", "HR_MANAGER")
                .requestMatchers(HttpMethod.POST, "/api/v1/user-accounts/auto/**").hasAnyRole("ADMIN", "HR_MANAGER")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/user-accounts/**").hasRole("ADMIN")
                
                // ========== ROLE MANAGEMENT ==========
                .requestMatchers("/api/v1/roles/**").hasAnyRole("ADMIN", "HR_MANAGER")
                
                // ========== EMPLOYEE MANAGEMENT ==========
                .requestMatchers(HttpMethod.POST, "/api/v1/employees").hasAnyRole("HR_STAFF", "HR_MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/employees/**").hasAnyRole("HR_MANAGER", "ADMIN")
                .requestMatchers("/api/v1/employees/import/**").hasAnyRole("HR_STAFF", "HR_MANAGER")
                .requestMatchers("/api/v1/employees/export").hasAnyRole("HR_STAFF", "HR_MANAGER")
                
                // ========== PAYROLL ==========
                .requestMatchers("/api/v1/payroll/**").hasAnyRole("HR_STAFF", "HR_MANAGER")
                .requestMatchers(HttpMethod.POST, "/api/v1/payroll").hasAnyRole("HR_MANAGER", "ADMIN")
                .requestMatchers("/api/v1/payroll/approval").hasAnyRole("HR_MANAGER", "ADMIN")
                .requestMatchers("/api/v1/payroll-cycles/**").hasAnyRole("HR_MANAGER", "ADMIN")
                
                // ========== CONTRACT ==========
                .requestMatchers(HttpMethod.POST, "/api/v1/contract/**").hasAnyRole("HR_STAFF", "HR_MANAGER")
                .requestMatchers("/api/v1/contract/{contractId}/approve").hasAnyRole("HR_MANAGER", "ADMIN")
                .requestMatchers("/api/v1/allowances/**").hasAnyRole("HR_MANAGER", "ADMIN")
                .requestMatchers("/api/v1/salary-contract/**").hasAnyRole("HR_STAFF", "HR_MANAGER", "ADMIN")
                .requestMatchers("/api/v1/salary-adjustments/**").hasAnyRole("HR_STAFF", "HR_MANAGER")
                
                // ========== ORGANIZATION ==========
                .requestMatchers(HttpMethod.POST, "/api/v1/departments/**").hasAnyRole("HR_MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/departments/**").hasAnyRole("HR_MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/departments/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/v1/sub-departments/**").hasAnyRole("HR_MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/sub-departments/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/positions/**").hasAnyRole("HR_MANAGER", "ADMIN")
                
                // ========== ATTENDANCE ==========
                .requestMatchers("/api/v1/breaks/**").hasAnyRole("HR_STAFF", "HR_MANAGER")
                .requestMatchers("/api/v1/ot-rates/**").hasAnyRole("HR_MANAGER", "ADMIN")
                
                // ========== LEAVE ==========
                .requestMatchers(HttpMethod.POST, "/api/v1/leave-requests/**/approve").hasAnyRole("MANAGER", "HR_MANAGER")
                .requestMatchers(HttpMethod.GET, "/api/v1/leave-requests").hasAnyRole("MANAGER", "HR_STAFF", "HR_MANAGER")
                
                // ========== PENALTY ==========
                .requestMatchers("/api/v1/penalty-rules/**").hasAnyRole("HR_MANAGER", "ADMIN")
                
                // ========== FACE RECOGNITION ==========
                .requestMatchers("/api/v1/employees/**/faces").hasAnyRole("HR_STAFF", "HR_MANAGER")
                .requestMatchers("/api/v1/employees/faces/batch").hasAnyRole("HR_MANAGER", "ADMIN")
                
                // ========== ALL OTHER REQUESTS ==========
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(customAuthenticationEntryPoint)
            );

        return http.build();
    }
}
```

---

## 📊 TÓM TẮT PHÂN QUYỀN

| Role | Quyền truy cập |
|------|----------------|
| **PUBLIC** | Login, Refresh token, Activate, Scan attendance, Face recognize, Health check |
| **ROLE_EMPLOYEE** | Xem thông tin cá nhân, Tạo đơn nghỉ phép, Xem số dư phép, Upload/Download file cá nhân |
| **ROLE_MANAGER** | Tất cả quyền của EMPLOYEE + Duyệt đơn nghỉ phép team, Xem chấm công team |
| **ROLE_HR_STAFF** | Tất cả quyền của EMPLOYEE + Quản lý nhân viên, Hợp đồng, Chấm công, Lương (xem), Đơn nghỉ |
| **ROLE_HR_MANAGER** | Tất cả quyền của HR_STAFF + Duyệt lương, Duyệt hợp đồng, Quản lý phòng ban, Cấu hình hệ thống |
| **ROLE_ADMIN** | Toàn quyền hệ thống |

---

## ✅ KHUYẾN NGHỊ

1. **Ngay lập tức:** Thay đổi `.anyRequest().permitAll()` thành `.anyRequest().authenticated()`
2. **Ưu tiên cao:** Thêm phân quyền chi tiết cho từng API endpoint
3. **Bảo mật:** Sử dụng `@PreAuthorize` cho các phương thức phức tạp
4. **Kiểm tra:** Thêm kiểm tra quyền sở hữu dữ liệu (data ownership) trong service layer
5. **Audit:** Thêm logging cho các thao tác quan trọng

---

*Báo cáo được tạo tự động bởi AI Assistant*
