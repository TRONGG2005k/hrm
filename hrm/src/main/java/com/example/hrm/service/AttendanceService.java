package com.example.hrm.service;


import com.example.hrm.dto.request.AttendanceRequest;
import com.example.hrm.dto.response.AttendanceResponse;
import com.example.hrm.repository.AttendanceRepository;
import com.example.hrm.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    public AttendanceResponse attendance(AttendanceRequest request){
        var attendance = attendanceRepository.findByEmployeeId()
    }

    public Object checkIn(){
        return null;
    }

    public Object chekIn(){
        return null;
    }
}
