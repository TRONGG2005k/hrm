package com.example.hrm.modules.employee.excel;

import com.example.hrm.modules.employee.entity.Employee;
import com.example.hrm.modules.employee.excel.dto.EmployeeExcelImportDto;
import com.example.hrm.modules.employee.excel.dto.ExcelImportResult;
import com.example.hrm.modules.employee.excel.exception.ExcelImportException;
import com.example.hrm.modules.employee.excel.parser.EmployeeExcelImportService;
import com.example.hrm.modules.employee.excel.validator.EmployeeValidator;
import com.example.hrm.modules.employee.excel.mapper.EmployeeExcelMapper;
import com.example.hrm.modules.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class EmployeeExcelService {


    private final EmployeeExcelImportService employeeExcelService;
    private final EmployeeValidator employeeValidator;
    private final EmployeeExcelMapper employeeExcelMapper;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public ExcelImportResult importEmployees(MultipartFile file) {
        List<EmployeeExcelImportDto> dtos = employeeExcelService.parseExcel(file);

        List<String> errors = new ArrayList<>();
        List<Employee> employees = new ArrayList<>();

        int rowNumber = 2; // nếu dòng 1 là header
        for (EmployeeExcelImportDto dto : dtos) {
            List<String> rowErrors = employeeValidator.validateEmployee(dto, rowNumber);
            if (!rowErrors.isEmpty()) {
                errors.addAll(rowErrors);
            } else {
                Employee employee = employeeExcelMapper.toEntity(dto);
                employees.add(employee);
            }
            rowNumber++;
        }

        if (!errors.isEmpty()) {
            throw new ExcelImportException(errors);
        }

        employeeRepository.saveAll(employees);
        return new ExcelImportResult(employees.size(), errors);
    }
}
