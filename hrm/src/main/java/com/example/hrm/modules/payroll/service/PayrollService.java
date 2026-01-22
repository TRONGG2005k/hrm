package com.example.hrm.modules.payroll.service;

import com.example.hrm.modules.attendance.entity.Attendance;
import com.example.hrm.modules.attendance.repository.AttendanceRepository;
import com.example.hrm.modules.contract.entity.AllowanceRule;
import com.example.hrm.modules.contract.entity.SalaryAdjustment;
import com.example.hrm.modules.contract.entity.SalaryContract;
import com.example.hrm.modules.contract.repository.AllowanceRuleRepository;
import com.example.hrm.modules.contract.repository.SalaryContractRepository;
import com.example.hrm.modules.contract.service.SalaryAdjustmentService;
import com.example.hrm.modules.employee.entity.Employee;
import com.example.hrm.modules.employee.repository.EmployeeRepository;
import com.example.hrm.modules.payroll.PayrollCalculator;
import com.example.hrm.modules.payroll.PayrollPeriodCalculator;
import com.example.hrm.modules.payroll.dto.request.PayrollApprovalRequest;
import com.example.hrm.modules.payroll.dto.request.PayrollImportDto;
import com.example.hrm.modules.payroll.dto.request.PayrollRequest;
import com.example.hrm.modules.payroll.dto.response.*;
import com.example.hrm.modules.payroll.entity.Payroll;
import com.example.hrm.modules.payroll.entity.PayrollApprovalHistory;
import com.example.hrm.modules.payroll.mapper.PayrollResponseMapper;
import com.example.hrm.modules.payroll.repository.PayrollApprovalHistoryRepository;
import com.example.hrm.modules.payroll.repository.PayrollRepository;
import com.example.hrm.modules.user.entity.UserAccount;
import com.example.hrm.modules.user.repository.UserAccountRepository;
import com.example.hrm.shared.enums.PayrollApprovalStatus;
import com.example.hrm.shared.enums.PayrollStatus;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class PayrollService {

    private final PayrollRepository payrollRepository;
    private final SalaryContractRepository salaryContractRepository;
    private final SalaryAdjustmentService salaryAdjustmentService;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final PayrollCycleService payrollCycleService;
    private final PayrollCalculator payrollCalculator;
    private final AllowanceRuleRepository allowanceRuleRepository;
    private final PayrollResponseMapper payrollResponseMapper;
    private final PayrollApprovalHistoryRepository payrollApprovalHistoryRepository;
    private final UserAccountRepository userAccountRepository;
    private final ObjectMapper objectMapper;
    public PayrollResponse create(PayrollRequest request) {

        var cycle = payrollCycleService.getActive();
        var period = calculatePeriod(cycle, request.getYear(), request.getMonth());

        var employee = getEmployee(request.getEmployeeId());
        checkPayrollExists(employee.getId(), request.getYear(), request.getMonth());

        var salaryContract = getSalaryContract(employee);
        var attendanceList = getAttendanceList(employee, period);
        var salaryAdjustments = getSalaryAdjustments(employee, period);
        var allowance = getAllowance(employee);
        var payrollDetail = calculatePayrollDetail(salaryContract, attendanceList, salaryAdjustments, allowance, cycle);

        Payroll payroll = buildAndSavePayroll(employee, request, payrollDetail);

        return buildPayrollResponse(request, employee, attendanceList, salaryAdjustments, payrollDetail, payroll.getId(), PayrollStatus.DRAFT);
    }

    public List<PayrollListItemResponse> createForAllEmployees(int month, int year) {

        var employees = employeeRepository.findAllByIsDeletedFalse();
        List<PayrollListItemResponse> results = new ArrayList<>();

        for (var employee : employees) {
            try {
                PayrollRequest request = new PayrollRequest();
                request.setEmployeeId(employee.getId());
                request.setMonth(month);
                request.setYear(year);

                PayrollResponse response = create(request);
                results.add(payrollResponseMapper.toListResponse(response));

            } catch (AppException ex) {
                if (ex.getErrorCode() == ErrorCode.PAYROLL_ALREADY_EXISTS) {
                    // Bỏ qua nhân viên đã có payroll trong kỳ
                    continue;
                }
                // Lỗi khác thì vẫn ném ra
                throw ex;
            }
        }

        return results;
    }

    public Page<PayrollListItemResponse> getAll(int page, int size){
        var listResponse = payrollRepository.findByIsDeletedFalse(PageRequest.of(page, size));
        return listResponse.map(payrollResponseMapper::toListResponse);
    }

    public Page<PayrollListItemResponse> getAllByEmployeeId(String employeeId, int page, int size){
        var listResponse = payrollRepository.findByEmployeeIdAndIsDeletedFalse(employeeId, PageRequest.of(page, size));
        return listResponse.map(payrollResponseMapper::toListResponse);
    }

    public Page<PayrollListItemResponse> getAllByMouth(int page, int size, int month, int year){


        var listResponse = payrollRepository.findByIsDeletedFalse(
                PageRequest.of(page, size),
                YearMonth.of(year, month));
        return listResponse.map(payrollResponseMapper::toListResponse);
    }

    public PayrollResponse getById(String payrollId){
        Payroll payroll = payrollRepository.findByIdAndIsDeletedFalse(payrollId)
                .orElseThrow(() ->new AppException(ErrorCode.PAYROLL_NOT_FOUND, 404));

        PayrollRequest request = new PayrollRequest(
                payroll.getId(), payroll.getMonth().getMonthValue(),
                payroll.getMonth().getYear());
        var cycle = payrollCycleService.getActive();
        var period = calculatePeriod(cycle, request.getYear(), request.getMonth());

        var employee = getEmployee(payroll.getId());
        var salaryContract = getSalaryContract(employee);
        var attendanceList = getAttendanceList(employee, period);
        var salaryAdjustments = getSalaryAdjustments(employee, period);
        var allowance = getAllowance(employee);
        var payrollDetail = calculatePayrollDetail(salaryContract, attendanceList, salaryAdjustments, allowance, cycle);

        return buildPayrollResponse(request, employee, attendanceList, salaryAdjustments, payrollDetail, payroll.getId(), payroll.getStatus());
    }

    public PayrollResponse getDetailByEmployee(String employeeId, int month, int year){
        Payroll payroll = payrollRepository.findByEmployeeIdAndMonthAndIsDeletedFalse(
                employeeId, YearMonth.of(year, month)).orElseThrow(() ->new AppException(ErrorCode.PAYROLL_NOT_FOUND, 404));

        PayrollRequest request = new PayrollRequest(employeeId, month, year);
        var cycle = payrollCycleService.getActive();
        var period = calculatePeriod(cycle, request.getYear(), request.getMonth());

        var employee = getEmployee(employeeId);
        var salaryContract = getSalaryContract(employee);
        var attendanceList = getAttendanceList(employee, period);
        var salaryAdjustments = getSalaryAdjustments(employee, period);
        var allowance = getAllowance(employee);
        var payrollDetail = calculatePayrollDetail(salaryContract, attendanceList, salaryAdjustments, allowance, cycle);

        return buildPayrollResponse(request, employee, attendanceList, salaryAdjustments, payrollDetail, payroll.getId(), payroll.getStatus());
    }

    public ApprovedPayrollListResponse salaryApproval(PayrollApprovalRequest request) throws JsonProcessingException {

        UserAccount user = userAccountRepository.findByUsernameAndIsDeletedFalse(
                SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, 404));


        String monthStr = String.format("%04d-%02d", request.getYear(), request.getMonth());

        List<Payroll> payrollList = payrollRepository
                .findAllByMonthAndStatusAndIsDeletedFalse(YearMonth.of(request.getYear(), request.getMonth()), PayrollStatus.DRAFT);

        if (payrollList.isEmpty()) {
            throw new AppException(ErrorCode.PAYROLL_NOT_FOUND, 404,
                    "không tìm thấy bảng lương nào chưa duyệt trong kỳ"  + monthStr);
        }

        BigDecimal totalPayrollAmount = BigDecimal.ZERO;

        PayrollStatus newStatus = request.getStatus(); // nếu DTO dùng enum

        for (Payroll item : payrollList) {
            item.setStatus(newStatus);
            totalPayrollAmount = totalPayrollAmount.add(item.getTotalSalary());
        }

        payrollRepository.saveAll(payrollList);

        // Lưu lịch sử duyệt
        PayrollApprovalHistory history = PayrollApprovalHistory.builder()
                .month(request.getMonth())
                .year(request.getYear())
                .totalAmount(totalPayrollAmount.doubleValue())
                .status(newStatus == PayrollStatus.APPROVED
                        ? PayrollApprovalStatus.APPROVED
                        : PayrollApprovalStatus.REJECTED)
                .comment(request.getComment())
                .approvedBy(user.getEmployee()) // hoặc từ context
                .payrollSnapshot(objectMapper.writeValueAsString(payrollList))
                .build();

        payrollApprovalHistoryRepository.save(history);

        return new ApprovedPayrollListResponse(
                monthStr,
                totalPayrollAmount,
                payrollList.stream()
                        .map(payrollResponseMapper::toListResponse)
                        .toList()
        );
    }

    public PayrollListResponse getPayrollList(PayrollApprovalRequest request){

        String monthStr = String.format("%04d-%02d", request.getYear(), request.getMonth());
        List<Payroll> payrollList = payrollRepository
                .findAllByMonthAndStatusAndIsDeletedFalse(YearMonth.of(request.getYear(), request.getMonth()), request.getStatus());

        if (payrollList.isEmpty()) {
            throw new AppException(ErrorCode.PAYROLL_NOT_FOUND, 404,
                    "Không tìm thấy bảng lương với trạng thái "
                            + request.getStatus() + " trong kỳ " + monthStr);
        }

        BigDecimal totalPayrollAmount = getTotalPayrollAmountByStatus(request);

//        PayrollStatus newStatus = request.getStatus(); // nếu DTO dùng enum

        return new PayrollListResponse(
                monthStr,
                totalPayrollAmount,
                payrollList.stream()
                        .map(payrollResponseMapper::toListResponse)
                        .toList()
        );
    }

    private PayrollPeriodCalculator.PayrollPeriod calculatePeriod(PayrollCycleResponse cycle, int year, int month) {
        return new PayrollPeriodCalculator().calculate(cycle, year, month);
    }

    private Employee getEmployee(String employeeId) {
        return employeeRepository.findByIdAndIsDeletedFalse(employeeId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND, 404));
    }

    private void checkPayrollExists(String employeeId, int year, int month) {
        if (payrollRepository.existsByEmployeeIdAndMonthAndIsDeletedFalse(employeeId, YearMonth.of(year, month))) {
            throw new AppException(ErrorCode.PAYROLL_ALREADY_EXISTS, 400);
        }
    }

    private SalaryContract getSalaryContract(Employee employee) {
        return salaryContractRepository
                .findByEmployee_IdAndContract_IdAndIsDeletedFalse(
                        employee.getId(),
                        employee.getContracts().getFirst().getId()
                )
                .orElseThrow(() -> new AppException(ErrorCode.SALARY_CONTRACT_NOT_FOUND, 404));
    }

    private List<Attendance> getAttendanceList(Employee employee, PayrollPeriodCalculator.PayrollPeriod period) {
        return attendanceRepository.findOTForEmployee(
                employee.getId(),
                period.startDate(),
                period.endDate()
        );
    }

    private List<SalaryAdjustment> getSalaryAdjustments(Employee employee, PayrollPeriodCalculator.PayrollPeriod period) {
        return salaryAdjustmentService.getForPayroll(
                employee.getId(),
                period.startDate(),
                period.endDate()
        );
    }

    private List<AllowanceRule> getAllowance(Employee employee) {
        return allowanceRuleRepository.findActiveRules(
                employee.getPosition().getId(),
                employee.getSubDepartment().getId()
        );
    }

    private PayrollDetailResponse calculatePayrollDetail(SalaryContract salaryContract, List<Attendance> attendanceList, List<SalaryAdjustment> salaryAdjustments, List<AllowanceRule> allowance, PayrollCycleResponse cycle) {
        return payrollCalculator.calculatePayrollDetail(
                salaryContract.getBaseSalary(),
                attendanceList,
                salaryAdjustments,
                allowance,
                cycle
        );
    }

    public BigDecimal getTotalPayrollAmountByStatus(PayrollApprovalRequest request) {
        String monthStr = String.format("%04d-%02d", request.getYear(), request.getMonth());

        return payrollRepository
                .sumTotalSalaryByMonthAndStatusAndIsDeletedFalse(monthStr, request.getStatus())
                .orElse(BigDecimal.ZERO);
    }

    private Payroll buildAndSavePayroll(Employee employee, PayrollRequest request, PayrollDetailResponse payrollDetail) {
        Payroll payroll = Payroll.builder()
                .employee(employee)
                .month(YearMonth.of(request.getYear(), request.getMonth()))
                .baseSalary(payrollDetail.baseSalaryTotal())
                .allowance(payrollDetail.totalAllowance())
                .overtime(payrollDetail.otTotal().doubleValue())
                .bonus(payrollDetail.totalBonus())
                .penalty(payrollDetail.totalPenalty())
                .totalSalary(payrollDetail.totalSalary())
                .status(PayrollStatus.DRAFT)
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        return payrollRepository.save(payroll);
    }

    private PayrollResponse buildPayrollResponse(
            PayrollRequest request,
            Employee employee,
//            SalaryContract salaryContract,
            List<Attendance> attendanceList,
            List<SalaryAdjustment> salaryAdjustments,
//            List<AllowanceRule> allowance,
//            PayrollCycleResponse cycle,
            PayrollDetailResponse payrollDetail,
            String payrollId,
            PayrollStatus status) {
        return PayrollResponse.builder()
                .payrollId(payrollId)
                .period(payrollResponseMapper.toPeriodResponse(request))
                .employee(payrollResponseMapper.toEmployeeResponse(employee))
                .attendanceSummary(
                        payrollResponseMapper.toAttendanceSummary(attendanceList, payrollDetail)
                )
                .earnings(
                        payrollResponseMapper.toEarningsResponse(
                              payrollDetail, salaryAdjustments
                        )
                )
                .deductions(
                        payrollResponseMapper.toDeductionsResponse(
                                salaryAdjustments, payrollDetail
                        )
                )
                .summary(
                        payrollResponseMapper.toPayrollSummary(
                                payrollResponseMapper.toEarningsResponse(
                                       payrollDetail, salaryAdjustments
                                ),
                                payrollResponseMapper.toDeductionsResponse(
                                        salaryAdjustments, payrollDetail
                                )
                        )
                )
                .metadata(payrollResponseMapper.toMetadata())
                .status(status)
                .build();
    }

    /**
     * Export payroll data to Excel file
     * @param page page number (0-based)
     * @param size page size
     * @return byte array of Excel file
     */
    public byte[] exportPayrollToExcel(int page, int size) throws IOException {
        Page<Payroll> payrollPage = payrollRepository.findByIsDeletedFalse(PageRequest.of(page, size));

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Payroll Data");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Employee Code", "Employee Name", "Month", "Base Salary", "Allowance", "Overtime", "Bonus", "Penalty", "Unpaid Leave", "Total Salary", "Note", "Status"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Fill data rows
        int rowNum = 1;
        for (Payroll payroll : payrollPage.getContent()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(payroll.getEmployee().getCode());
            row.createCell(1).setCellValue(payroll.getEmployee().getFirstName() + " " + payroll.getEmployee().getLastName());
            row.createCell(2).setCellValue(payroll.getMonth().toString());
            row.createCell(3).setCellValue(payroll.getBaseSalary() != null ? payroll.getBaseSalary().doubleValue() : 0);
            row.createCell(4).setCellValue(payroll.getAllowance() != null ? payroll.getAllowance().doubleValue() : 0);
            row.createCell(5).setCellValue(payroll.getOvertime() != null ? payroll.getOvertime() : 0);
            row.createCell(6).setCellValue(payroll.getBonus() != null ? payroll.getBonus().doubleValue() : 0);
            row.createCell(7).setCellValue(payroll.getPenalty() != null ? payroll.getPenalty().doubleValue() : 0);
            row.createCell(8).setCellValue(payroll.getUnpaidLeave() != null ? payroll.getUnpaidLeave().doubleValue() : 0);
            row.createCell(9).setCellValue(payroll.getTotalSalary() != null ? payroll.getTotalSalary().doubleValue() : 0);
            row.createCell(10).setCellValue(payroll.getNote() != null ? payroll.getNote() : "");
            row.createCell(11).setCellValue(payroll.getStatus().toString());
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
     * Import payroll data from Excel file
     * @param file the Excel file to import
     * @return import response with results and errors
     */
    public PayrollImportResponse importPayrollFromExcel(MultipartFile file) throws IOException {
        List<PayrollImportDto> importData = parseExcelFile(file);
        List<ImportErrorDto> errors = new ArrayList<>();
        List<String> importedIds = new ArrayList<>();
        int successCount = 0;

        for (PayrollImportDto dto : importData) {
            try {
                validateImportDto(dto);
                Payroll payroll = createPayrollFromImportDto(dto);
                payrollRepository.save(payroll);
                importedIds.add(payroll.getId());
                successCount++;
            } catch (Exception e) {
                errors.add(ImportErrorDto.builder()
                        .rowNumber(dto.getRowNumber())
                        .message(e.getMessage())
                        .build());
            }
        }

        return PayrollImportResponse.builder()
                .totalRows(importData.size())
                .successCount(successCount)
                .errorCount(errors.size())
                .errors(errors)
                .importedPayrollIds(importedIds)
                .build();
    }

    private List<PayrollImportDto> parseExcelFile(MultipartFile file) throws IOException {
        List<PayrollImportDto> data = new ArrayList<>();

        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        // Skip header row
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            PayrollImportDto dto = PayrollImportDto.builder()
                    .rowNumber(i + 1)
                    .employeeCode(getCellValueAsString(row.getCell(0)))
                    .month(getCellValueAsString(row.getCell(2)))
                    .baseSalary(getCellValueAsBigDecimal(row.getCell(3)))
                    .allowance(getCellValueAsBigDecimal(row.getCell(4)))
                    .overtime(getCellValueAsDouble(row.getCell(5)))
                    .bonus(getCellValueAsBigDecimal(row.getCell(6)))
                    .penalty(getCellValueAsBigDecimal(row.getCell(7)))
                    .unpaidLeave(getCellValueAsBigDecimal(row.getCell(8)))
                    .note(getCellValueAsString(row.getCell(10)))
                    .build();

            data.add(dto);
        }

        workbook.close();
        return data;
    }

    private void validateImportDto(PayrollImportDto dto) {
        if (dto.getEmployeeCode() == null || dto.getEmployeeCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Employee code is required");
        }
        if (dto.getMonth() == null || dto.getMonth().trim().isEmpty()) {
            throw new IllegalArgumentException("Month is required");
        }
        // Validate month format YYYY-MM
        if (!dto.getMonth().matches("\\d{4}-\\d{2}")) {
            throw new IllegalArgumentException("Month must be in format YYYY-MM");
        }

        // Check if employee exists
        Employee employee = employeeRepository.findByCodeAndIsDeletedFalse(dto.getEmployeeCode())
                .orElseThrow(() -> new IllegalArgumentException("Employee with code " + dto.getEmployeeCode() + " not found"));

        // Check if payroll already exists
        YearMonth yearMonth = YearMonth.parse(dto.getMonth());
        if (payrollRepository.existsByEmployeeIdAndMonthAndIsDeletedFalse(employee.getId(), yearMonth)) {
            throw new IllegalArgumentException("Payroll already exists for employee " + dto.getEmployeeCode() + " in month " + dto.getMonth());
        }
    }

    private Payroll createPayrollFromImportDto(PayrollImportDto dto) {
        Employee employee = employeeRepository.findByCodeAndIsDeletedFalse(dto.getEmployeeCode())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        YearMonth yearMonth = YearMonth.parse(dto.getMonth());

        // Calculate total salary
        BigDecimal totalSalary = calculateTotalSalary(dto);

        return Payroll.builder()
                .employee(employee)
                .month(yearMonth)
                .baseSalary(dto.getBaseSalary() != null ? dto.getBaseSalary() : BigDecimal.ZERO)
                .allowance(dto.getAllowance() != null ? dto.getAllowance() : BigDecimal.ZERO)
                .overtime(dto.getOvertime() != null ? dto.getOvertime() : 0.0)
                .bonus(dto.getBonus() != null ? dto.getBonus() : BigDecimal.ZERO)
                .penalty(dto.getPenalty() != null ? dto.getPenalty() : BigDecimal.ZERO)
                .unpaidLeave(dto.getUnpaidLeave() != null ? dto.getUnpaidLeave() : BigDecimal.ZERO)
                .totalSalary(totalSalary)
                .note(dto.getNote())
                .status(PayrollStatus.DRAFT)
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
    }

    private BigDecimal calculateTotalSalary(PayrollImportDto dto) {
        BigDecimal base = dto.getBaseSalary() != null ? dto.getBaseSalary() : BigDecimal.ZERO;
        BigDecimal allowance = dto.getAllowance() != null ? dto.getAllowance() : BigDecimal.ZERO;
        BigDecimal overtime = dto.getOvertime() != null ? BigDecimal.valueOf(dto.getOvertime()) : BigDecimal.ZERO;
        BigDecimal bonus = dto.getBonus() != null ? dto.getBonus() : BigDecimal.ZERO;
        BigDecimal penalty = dto.getPenalty() != null ? dto.getPenalty() : BigDecimal.ZERO;
        BigDecimal unpaidLeave = dto.getUnpaidLeave() != null ? dto.getUnpaidLeave() : BigDecimal.ZERO;

        return base.add(allowance).add(overtime).add(bonus).subtract(penalty).subtract(unpaidLeave);
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

    private BigDecimal getCellValueAsBigDecimal(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            return BigDecimal.valueOf(cell.getNumericCellValue());
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                return new BigDecimal(cell.getStringCellValue());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private Double getCellValueAsDouble(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                return Double.valueOf(cell.getStringCellValue());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
