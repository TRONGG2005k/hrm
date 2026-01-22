package com.example.hrm.modules.employee.service;

import com.example.hrm.modules.employee.dto.request.EmployeeImportDto;
import com.example.hrm.modules.employee.dto.response.EmployeeImportResponse;
import com.example.hrm.modules.file.dto.response.FileAttachmentResponse;
import com.example.hrm.modules.file.entity.FileAttachment;
import com.example.hrm.shared.enums.RefType;
// import com.example.hrm.modules.employee.mapper.AddressMapper;
import com.example.hrm.modules.employee.mapper.ContactMapper;
import com.example.hrm.modules.file.mapper.FileAttachmentMapper;
import com.example.hrm.modules.employee.repository.*;
import org.springframework.stereotype.Service;
import com.example.hrm.modules.employee.mapper.EmployeeMapper;
import com.example.hrm.modules.employee.entity.Employee;
import com.example.hrm.modules.payroll.dto.response.ImportErrorDto;
import com.example.hrm.shared.enums.EmployeeStatus;
import com.example.hrm.shared.enums.Gender;
import com.example.hrm.shared.enums.ShiftType;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import com.example.hrm.modules.employee.dto.request.EmployeeRequest;
import com.example.hrm.modules.employee.dto.response.EmployeeResponse;
import com.example.hrm.modules.file.repository.FileAttachmentRepository;
import com.example.hrm.modules.organization.repository.SubDepartmentRepository;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final AddressRepository addressRepository;
    private final FileAttachmentRepository fileAttachmentRepository;
    private final SubDepartmentRepository subDepartmentRepository;
    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;
    private final PositionRepository positionRepository;


    public Page<EmployeeResponse> getAllEmployees(int page, int size) {
        var employeePage = employeeRepository.findByIsDeletedFalse(PageRequest.of(page, size));
        return employeePage.map(employeeMapper::toResponse);
    }

    public EmployeeResponse getEmployeeById(String id) {
        var employee = employeeRepository.findEmployeeWithFiles(id)
                .orElseThrow(
                        () -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND, 404, "Employee not found with id: " + id
                                + "or has been deleted"));
        List<FileAttachment> fileAttachments = fileAttachmentRepository
                .findByRefTypeAndRefIdAndIsDeletedFalse(RefType.EMPLOYEE.getValue(), employee.getId());
        List<FileAttachmentResponse> fileAttachmentResponses = fileAttachments.stream().map(
                FileAttachmentMapper.INSTANCE::toResponse).toList();
        var employeeResponse = employeeMapper.toResponse(employee);
        employeeResponse.setContacts(
                contactRepository.findByEmployeeIdAndIsDeletedFalse(employee.getId())
                        .stream().map(contactMapper::toResponse)
                        .toList());
        employeeResponse.setFileAttachmentResponses(fileAttachmentResponses);
        return employeeResponse;
    }

    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {

        Employee employee = employeeMapper.toEntity(request);

        employee.setAddress(
                addressRepository.findByIdAndIsDeletedFalse(request.getAddressId())
                        .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND, 404))
        );

        employee.setSubDepartment(
                subDepartmentRepository.findByIdAndIsDeletedFalse(request.getSubDepartmentId())
                        .orElseThrow(() -> new AppException(ErrorCode.SUB_DEPARTMENT_NOT_FOUND, 404))
        );

        employee.setPosition(
                positionRepository.findByIdAndActiveTrue(request.getPositionId())
                        .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, 404))
        );

        return employeeMapper.toResponse(employeeRepository.save(employee));
    }



    @Transactional
    public EmployeeResponse updateEmployee(String id, EmployeeRequest request) {

        Employee employee = employeeRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND, 404));

        employeeMapper.updateEntity(request, employee);

        if (request.getAddressId() != null) {
            employee.setAddress(
                    addressRepository.findByIdAndIsDeletedFalse(request.getAddressId())
                            .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND, 404))
            );
        }

        if (request.getSubDepartmentId() != null) {
            employee.setSubDepartment(
                    subDepartmentRepository.findByIdAndIsDeletedFalse(request.getSubDepartmentId())
                            .orElseThrow(() -> new AppException(ErrorCode.SUB_DEPARTMENT_NOT_FOUND, 404))
            );
        }

        if (request.getPositionId() != null) {
            employee.setPosition(
                    positionRepository.findByIdAndActiveTrue(request.getPositionId())
                            .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, 404))
            );
        }

        return employeeMapper.toResponse(employeeRepository.save(employee));
    }



    @Transactional
    public void deleteEmployee(String id) {
        var employee = employeeRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(
                        () -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND, 404, "Employee not found with id: " + id));
        employee.setIsDeleted(true);
        employeeRepository.save(employee);
    }

    /**
     * Export employee data to Excel file
     * @param page page number (0-based)
     * @param size page size
     * @return byte array of Excel file
     */
    public byte[] exportEmployeesToExcel(int page, int size) throws IOException {
        Page<Employee> employeePage = employeeRepository.findByIsDeletedFalse(PageRequest.of(page, size));

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Employee Data");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Code", "First Name", "Last Name", "Date of Birth", "Gender", "Email", "Phone", "Status", "Join Date", "Shift Type", "Address ID", "Sub Department ID", "Position ID"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Fill data rows
        int rowNum = 1;
        for (Employee employee : employeePage.getContent()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(employee.getCode() != null ? employee.getCode() : "");
            row.createCell(1).setCellValue(employee.getFirstName() != null ? employee.getFirstName() : "");
            row.createCell(2).setCellValue(employee.getLastName() != null ? employee.getLastName() : "");
            row.createCell(3).setCellValue(employee.getDateOfBirth() != null ? employee.getDateOfBirth().toString() : "");
            row.createCell(4).setCellValue(employee.getGender() != null ? employee.getGender().toString() : "");
            row.createCell(5).setCellValue(employee.getEmail() != null ? employee.getEmail() : "");
            row.createCell(6).setCellValue(employee.getPhone() != null ? employee.getPhone() : "");
            row.createCell(7).setCellValue(employee.getStatus() != null ? employee.getStatus().toString() : "");
            row.createCell(8).setCellValue(employee.getJoinDate() != null ? employee.getJoinDate().toString() : "");
            row.createCell(9).setCellValue(employee.getShiftType() != null ? employee.getShiftType().toString() : "");
            row.createCell(10).setCellValue(employee.getAddress() != null ? employee.getAddress().getId() : "");
            row.createCell(11).setCellValue(employee.getSubDepartment() != null ? employee.getSubDepartment().getId() : "");
            row.createCell(12).setCellValue(employee.getPosition() != null ? employee.getPosition().getId() : "");
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write to byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    /**
     * Import employee data from Excel file
     * @param file the Excel file to import
     * @return import response with results and errors
     */
    @Transactional
    public EmployeeImportResponse importEmployeesFromExcel(MultipartFile file) throws IOException {
        List<EmployeeImportDto> importData = parseExcelFile(file);
        List<ImportErrorDto> errors = new ArrayList<>();
        List<String> importedIds = new ArrayList<>();
        int successCount = 0;

        for (EmployeeImportDto dto : importData) {
            try {
                validateImportDto(dto);
                Employee employee = createEmployeeFromImportDto(dto);
                employeeRepository.save(employee);
                importedIds.add(employee.getId());
                successCount++;
            } catch (Exception e) {
                errors.add(ImportErrorDto.builder()
                        .rowNumber(dto.getRowNumber())
                        .message(e.getMessage())
                        .build());
            }
        }

        return EmployeeImportResponse.builder()
                .totalRows(importData.size())
                .successCount(successCount)
                .errorCount(errors.size())
                .errors(errors)
                .importedEmployeeIds(importedIds)
                .build();
    }

    private List<EmployeeImportDto> parseExcelFile(MultipartFile file) throws IOException {
        List<EmployeeImportDto> data = new ArrayList<>();

        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        // Skip header row
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            EmployeeImportDto dto = EmployeeImportDto.builder()
                    .rowNumber(i + 1)
                    .code(getCellValueAsString(row.getCell(0)))
                    .firstName(getCellValueAsString(row.getCell(1)))
                    .lastName(getCellValueAsString(row.getCell(2)))
                    .dateOfBirth(getCellValueAsString(row.getCell(3)))
                    .gender(getCellValueAsString(row.getCell(4)))
                    .email(getCellValueAsString(row.getCell(5)))
                    .phone(getCellValueAsString(row.getCell(6)))
                    .status(getCellValueAsString(row.getCell(7)))
                    .joinDate(getCellValueAsString(row.getCell(8)))
                    .shiftType(getCellValueAsString(row.getCell(9)))
                    .addressId(getCellValueAsString(row.getCell(10)))
                    .subDepartmentId(getCellValueAsString(row.getCell(11)))
                    .positionId(getCellValueAsString(row.getCell(12)))
                    .build();

            data.add(dto);
        }

        workbook.close();
        return data;
    }

    private void validateImportDto(EmployeeImportDto dto) {
        if (dto.getCode() == null || dto.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Employee code is required");
        }
        if (dto.getFirstName() == null || dto.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (dto.getLastName() == null || dto.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        // Check if employee code already exists
        if (employeeRepository.findByCodeAndIsDeletedFalse(dto.getCode()).isPresent()) {
            throw new IllegalArgumentException("Employee with code " + dto.getCode() + " already exists");
        }

        // Check if email already exists - Note: This assumes email is unique, but we need to check if such a method exists
        // For now, we'll skip email uniqueness check as it might not be enforced in the database

        // Validate date formats
        if (dto.getDateOfBirth() != null && !dto.getDateOfBirth().trim().isEmpty()) {
            try {
                LocalDate.parse(dto.getDateOfBirth());
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid date of birth format. Use YYYY-MM-DD");
            }
        }

        if (dto.getJoinDate() != null && !dto.getJoinDate().trim().isEmpty()) {
            try {
                LocalDate.parse(dto.getJoinDate());
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid join date format. Use YYYY-MM-DD");
            }
        }

        // Validate enums
        if (dto.getGender() != null && !dto.getGender().trim().isEmpty()) {
            try {
                Gender.valueOf(dto.getGender().toUpperCase());
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid gender. Valid values: " + java.util.Arrays.toString(Gender.values()));
            }
        }

        if (dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) {
            try {
                EmployeeStatus.valueOf(dto.getStatus().toUpperCase());
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid status. Valid values: " + java.util.Arrays.toString(EmployeeStatus.values()));
            }
        }

        if (dto.getShiftType() != null && !dto.getShiftType().trim().isEmpty()) {
            try {
                ShiftType.valueOf(dto.getShiftType().toUpperCase());
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid shift type. Valid values: " + java.util.Arrays.toString(ShiftType.values()));
            }
        }

        // Validate foreign keys
        if (dto.getAddressId() != null && !dto.getAddressId().trim().isEmpty()) {
            if (addressRepository.findByIdAndIsDeletedFalse(dto.getAddressId()).isEmpty()) {
                throw new IllegalArgumentException("Address with ID " + dto.getAddressId() + " not found");
            }
        }

        if (dto.getSubDepartmentId() != null && !dto.getSubDepartmentId().trim().isEmpty()) {
            if (subDepartmentRepository.findByIdAndIsDeletedFalse(dto.getSubDepartmentId()).isEmpty()) {
                throw new IllegalArgumentException("Sub department with ID " + dto.getSubDepartmentId() + " not found");
            }
        }

        if (dto.getPositionId() != null && !dto.getPositionId().trim().isEmpty()) {
            if (positionRepository.findByIdAndActiveTrue(dto.getPositionId()).isEmpty()) {
                throw new IllegalArgumentException("Position with ID " + dto.getPositionId() + " not found or inactive");
            }
        }
    }

    private Employee createEmployeeFromImportDto(EmployeeImportDto dto) {
        Employee employee = Employee.builder()
                .code(dto.getCode())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .isDeleted(false)
                .build();

        // Set optional fields
        if (dto.getDateOfBirth() != null && !dto.getDateOfBirth().trim().isEmpty()) {
            employee.setDateOfBirth(LocalDate.parse(dto.getDateOfBirth()));
        }

        if (dto.getJoinDate() != null && !dto.getJoinDate().trim().isEmpty()) {
            employee.setJoinDate(LocalDate.parse(dto.getJoinDate()));
        } else {
            employee.setJoinDate(LocalDate.now());
        }

        if (dto.getGender() != null && !dto.getGender().trim().isEmpty()) {
            employee.setGender(Gender.valueOf(dto.getGender().toUpperCase()));
        }

        if (dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) {
            employee.setStatus(EmployeeStatus.valueOf(dto.getStatus().toUpperCase()));
        }

        if (dto.getShiftType() != null && !dto.getShiftType().trim().isEmpty()) {
            employee.setShiftType(ShiftType.valueOf(dto.getShiftType().toUpperCase()));
        }

        // Set foreign keys
        if (dto.getAddressId() != null && !dto.getAddressId().trim().isEmpty()) {
            employee.setAddress(addressRepository.findByIdAndIsDeletedFalse(dto.getAddressId()).orElse(null));
        }

        if (dto.getSubDepartmentId() != null && !dto.getSubDepartmentId().trim().isEmpty()) {
            employee.setSubDepartment(subDepartmentRepository.findByIdAndIsDeletedFalse(dto.getSubDepartmentId()).orElse(null));
        }

        if (dto.getPositionId() != null && !dto.getPositionId().trim().isEmpty()) {
            employee.setPosition(positionRepository.findByIdAndActiveTrue(dto.getPositionId()).orElse(null));
        }

        return employee;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }
}
