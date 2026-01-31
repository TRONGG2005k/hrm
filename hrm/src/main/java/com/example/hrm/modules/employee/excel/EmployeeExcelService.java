package com.example.hrm.modules.employee.excel;

import com.example.hrm.modules.employee.entity.Employee;
import com.example.hrm.modules.employee.excel.dto.EmployeeExcelExportDto;
import com.example.hrm.modules.employee.excel.dto.EmployeeExcelImportDto;
import com.example.hrm.modules.employee.excel.dto.ExcelImportResult;
import com.example.hrm.modules.employee.excel.parser.EmployeeExcelImportService;
import com.example.hrm.modules.employee.excel.util.EmployeeUtil;
import com.example.hrm.modules.employee.excel.validator.EmployeeValidator;
import com.example.hrm.modules.employee.excel.mapper.EmployeeExcelMapper;
import com.example.hrm.modules.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class EmployeeExcelService {


    private final EmployeeExcelImportService employeeExcelImportService;
    private final EmployeeValidator employeeValidator;
    private final EmployeeExcelMapper employeeExcelMapper;
    private final EmployeeRepository employeeRepository;
    private final EmployeeUtil employeeUtil;

//    @Transactional
    public ExcelImportResult importEmployees(MultipartFile file) {
        List<EmployeeExcelImportDto> dtos = employeeExcelImportService.parseExcel(file);

        List<String> errors = new ArrayList<>();
        int successCount = 0;
        int rowNumber = 2; // nếu dòng 1 là header
        for (EmployeeExcelImportDto dto : dtos) {
            List<String> rowErrors = employeeValidator.validateEmployee(dto, rowNumber);

            if (!rowErrors.isEmpty()) {
                errors.addAll(rowErrors);
                rowNumber++;
                continue;
            }

            try {
                Employee employee = employeeExcelMapper.toEntity(dto);
                employeeRepository.save(employee); // lưu từng dòng
                successCount++;
            } catch (DataIntegrityViolationException ex) {
                errors.add("Dòng " + rowNumber + ": Mã nhân viên đã tồn tại trong DB");
            } catch (Exception ex) {
                errors.add("Dòng " + rowNumber + ": Lỗi hệ thống khi lưu dữ liệu");
            }

            rowNumber++;
        }

        return new ExcelImportResult(successCount, errors);
    }

    public ExcelImportResult importOrUpdateEmployees(MultipartFile file) {
        List<EmployeeExcelImportDto> dtos = employeeExcelImportService.parseExcel(file);

        List<String> errors = new ArrayList<>();
        int successCount = 0;

        int rowNumber = 2; // dòng 1 là header
        for (EmployeeExcelImportDto dto : dtos) {

            // 1. Validate dữ liệu
            List<String> rowErrors = employeeValidator.validateEmployee(dto, rowNumber);
            if (!rowErrors.isEmpty()) {
                errors.addAll(rowErrors);
                rowNumber++;
                continue;
            }

            try {
                // 2. Check tồn tại theo code
                var existingOpt = employeeRepository.findByCodeAndIsDeletedFalse(dto.getCode());

                if (existingOpt.isPresent()) {
                    // 3a. Update
                    Employee existing = existingOpt.get();
                    employeeExcelMapper.updateEntity(existing, dto);
                    employeeRepository.save(existing);
                } else {
                    // 3b. Create mới
                    Employee employee = employeeExcelMapper.toEntity(dto);
                    employeeRepository.save(employee);
                }

                successCount++;
            } catch (DataIntegrityViolationException ex) {
                errors.add("Dòng " + rowNumber + ": Dữ liệu vi phạm ràng buộc DB");
            } catch (Exception ex) {
                errors.add("Dòng " + rowNumber + ": Lỗi hệ thống khi xử lý dữ liệu");
            }

            rowNumber++;
        }

        return new ExcelImportResult(successCount, errors);
    }

    public void export(OutputStream outputStream) throws IOException {

        List<Employee> employees = employeeRepository.findAllByIsDeletedFalse();
        Workbook workbook = new XSSFWorkbook();
        String sheetName = "Employees_" + LocalDateTime.now().toLocalDate();
        Sheet sheet = workbook.createSheet(sheetName);

        Row header = sheet.createRow(0);

        String[] headers = {
                "Code",
                "First Name",
                "Last Name",
                "Date of Birth",
                "Gender",
                "Email",
                "Phone",
                "Status",
                "Join Date",
                "Shift Type",
                "Street",
                "Ward",
                "District",
                "Province",
                "Department Name",
                "Position Name"
        };

        for (int i = 0; i < headers.length; i++) {
            header.createCell(i).setCellValue(headers[i]);
        }

        int rowIndex = 1;

        for (var employee : employees){
            Row row = sheet.createRow(rowIndex++);
            EmployeeExcelExportDto excelExportDto = employeeExcelMapper.toDto(employee);
            employeeUtil.buildRow(row, excelExportDto);
        }
        workbook.write(outputStream);
        workbook.close();

    }
}
