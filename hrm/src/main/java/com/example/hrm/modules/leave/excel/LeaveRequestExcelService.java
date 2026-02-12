package com.example.hrm.modules.leave.excel;

import com.example.hrm.modules.attendance.service.AttendanceService;
import com.example.hrm.modules.leave.entity.LeaveRequest;
import com.example.hrm.modules.leave.excel.dto.LeaveImportRequestExcelDto;
import com.example.hrm.modules.leave.excel.mapper.LeaveExcelMapper;
import com.example.hrm.modules.leave.excel.validator.LeaveExcelValidator;
import com.example.hrm.modules.leave.repository.LeaveRequestRepository;
import com.example.hrm.modules.user.entity.UserAccount;
import com.example.hrm.modules.user.repository.UserAccountRepository;
import com.example.hrm.shared.ExcelResult;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveRequestExcelService {

    private final LeaveExcelValidator validator;
    private final LeaveExcelMapper mapper;
    private final UserAccountRepository userAccountRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final AttendanceService attendanceService;

    public ExcelResult importExcel(MultipartFile file){
        List<LeaveImportRequestExcelDto> dtos = parseExcel(file);
        int successCount = 0;
        int rowNumber = 2; // nếu dòng 1 là header
        List<String> errors = new ArrayList<>();
        UserAccount user = userAccountRepository.findByUsernameAndIsDeletedFalse(
                SecurityContextHolder.getContext().getAuthentication().getName()
        ).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, 400));

        for(var dto : dtos) {
            List<String> rowErrors = validator.validateLeave(dto, rowNumber);
            errors.addAll(rowErrors);
            if (!rowErrors.isEmpty()) {
                errors.addAll(rowErrors);
                rowNumber++;
                continue;
            }

            LeaveRequest leaveRequest = mapper.toEntity(dto);
            leaveRequest.setApprovedBy(user.getEmployee());
            leaveRequest.setApprovedAt(LocalDateTime.now());
            leaveRequestRepository.save(leaveRequest);

            attendanceService.generateFromLeave(leaveRequest);

            successCount++;
            rowNumber++;
        }
        return new ExcelResult(successCount, errors);
    }

    private  List<LeaveImportRequestExcelDto> parseExcel(MultipartFile file) {
        List<LeaveImportRequestExcelDto> dtos = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            dtos.addAll(mapper.toDto(sheet));

        } catch (Exception e) {
            throw new RuntimeException("Lỗi đọc file Excel: " + e.getMessage(), e);
        }
        return dtos;
    }


}
