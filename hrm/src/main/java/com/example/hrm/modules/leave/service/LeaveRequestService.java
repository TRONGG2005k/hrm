package com.example.hrm.modules.leave.service;

import com.example.hrm.modules.attendance.entity.Attendance;
import com.example.hrm.modules.attendance.repository.AttendanceRepository;
import com.example.hrm.modules.employee.entity.Employee;
import com.example.hrm.modules.employee.repository.EmployeeRepository;
import com.example.hrm.modules.leave.dto.request.LeaveRequestApprovalRequest;
import com.example.hrm.modules.leave.dto.request.LeaveRequestCreateRequest;
import com.example.hrm.modules.leave.dto.response.LeaveRequestDetailResponse;
import com.example.hrm.modules.leave.dto.response.LeaveRequestListItemResponse;
import com.example.hrm.modules.leave.entity.LeaveRequest;
import com.example.hrm.modules.leave.mapper.LeaveRequestMapper;
import com.example.hrm.modules.leave.repository.LeaveRequestRepository;
import com.example.hrm.modules.user.entity.UserAccount;
import com.example.hrm.modules.user.repository.UserAccountRepository;
import com.example.hrm.shared.enums.AttendanceEvaluation;
import com.example.hrm.shared.enums.AttendanceStatus;
import com.example.hrm.shared.enums.LeaveStatus;
import com.example.hrm.shared.enums.LeaveType;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LeaveRequestService {
    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final UserAccountRepository userAccountRepository;
    private final LeaveRequestMapper leaveRequestMapper;
    private final AttendanceRepository attendanceRepository;
    private final LeaveBalanceService leaveBalanceService;

    public  LeaveRequestDetailResponse create(LeaveRequestCreateRequest request){


        Employee employee = employeeRepository.findByIdAndIsDeletedFalse(request.getEmployeeId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, 404));

        LeaveRequest leaveRequest = leaveRequestMapper.toEntity(request, employee);
        leaveRequestRepository.save(leaveRequest);

        var response = leaveRequestMapper.toDetailResponse(leaveRequest);
        response.setFullName(employee.getLastName() + employee.getFirstName());

        return  response;
    }

    public LeaveRequestDetailResponse update(LeaveRequestCreateRequest request, String leaveId){

        LeaveRequest leaveRequest = leaveRequestRepository.findByIdAndIsDeletedFalse(leaveId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, 404, "ko tìm thấy leave request với id"
                + leaveId));

        if(leaveRequest.getStatus() != LeaveStatus.PENDING){
            throw  new AppException(ErrorCode.INVALID_TOKEN, 500 , "yêu cầu này đẵ được duyệt hoặc bị huỷ ko thể sửa");
        }

        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        Employee employee = employeeRepository.findByIdAndIsDeletedFalse(request.getEmployeeId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, 404));

        UserAccount user = userAccountRepository.findByUsernameAndIsDeletedFalse(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, 404));

        leaveRequest.setEmployee(employee);
        leaveRequest.setUpdatedAt(LocalDateTime.now());
        leaveRequest.setReason(request.getReason());
        leaveRequest.setStartDate(request.getStartDate());
        leaveRequest.setEndDate(request.getEndDate());
        leaveRequest.setType(request.getType());

        leaveRequestRepository.save(leaveRequest);

        var response = leaveRequestMapper.toDetailResponse(leaveRequest);
        response.setFullName(employee.getLastName() + employee.getFirstName());
        return  response;
    }

    public LeaveRequestDetailResponse approveLeave(
            LeaveRequestApprovalRequest request,
            String leaveId
    ) {
        LeaveRequest leaveRequest = leaveRequestRepository
                .findByIdAndIsDeletedFalse(leaveId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, 404,
                        "Không tìm thấy leave request với id " + leaveId));

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new AppException(ErrorCode.INVALID_STATE, 400,
                    "Yêu cầu này đã được xử lý, không thể cập nhật nữa");
        }

        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        UserAccount user = userAccountRepository
                .findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, 404));

        leaveRequest.setStatus(request.getStatus());
        leaveRequest.setApprovedAt(LocalDateTime.now());
        leaveRequest.setApprovedBy(user.getEmployee());

        leaveRequestRepository.save(leaveRequest);

        // 👉 Nếu là nghỉ phép năm và được duyệt → trừ phép
        if (leaveRequest.getType() == LeaveType.ANNUAL
                && request.getStatus() == LeaveStatus.APPROVED) {

            leaveBalanceService.deductLeave(
                    leaveRequest.getEmployee(),
                    leaveRequest.getStartDate(),
                    leaveRequest.getEndDate()
            );
        }

        // 👉 Nếu được duyệt → tạo bản ghi chấm công
        if (request.getStatus() == LeaveStatus.APPROVED) {
            Employee employee = leaveRequest.getEmployee();
            LocalDate start = leaveRequest.getStartDate();
            LocalDate end = leaveRequest.getEndDate();

            for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                Attendance attendance = Attendance.builder()
                        .employee(employee)
                        .workDate(date)
                        .status(AttendanceStatus.LEAVE)
                        .evaluation(AttendanceEvaluation.LEAVE_APPROVED)
                        .checkInTime(null)
                        .checkOutTime(null)
                        .build();

                attendanceRepository.save(attendance);
            }
        }

        return leaveRequestMapper.toDetailResponse(leaveRequest);
    }



    public LeaveRequestDetailResponse getById(String leaveId){

        LeaveRequest leaveRequest = leaveRequestRepository.findByIdAndIsDeletedFalse(leaveId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, 404, "ko tìm thấy leave request với id"
                        + leaveId));

        return leaveRequestMapper.toDetailResponse(leaveRequest);
    }

    public Page<LeaveRequestListItemResponse> getAll(int page, int size){
        var leaveList = leaveRequestRepository.findAllByIsDeletedFalse(PageRequest.of(page, size));
        return leaveList.map(leaveRequest -> {
            LeaveRequestListItemResponse response = leaveRequestMapper.toListItemResponse(leaveRequest);
            response.setFullName(leaveRequest.getEmployee().getLastName() + " " + leaveRequest.getEmployee().getFirstName());
            return response;
        });
    }

    public Page<LeaveRequestListItemResponse> getAllByStatus(int page, int size, LeaveStatus status){
        var leaveList = leaveRequestRepository.findAllByStatusAndIsDeletedFalse(PageRequest.of(page, size), status);
        return leaveList.map(leaveRequest -> {
            var response = leaveRequestMapper.toListItemResponse(leaveRequest);
            response.setFullName(leaveRequest.getEmployee().getLastName() + " " + leaveRequest.getEmployee().getFirstName());
            return response;
        });
    }

}
