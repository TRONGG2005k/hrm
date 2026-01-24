package com.example.hrm.modules.employee.excel.mapper;

import com.example.hrm.modules.employee.entity.Address;
import com.example.hrm.modules.employee.entity.Employee;
import com.example.hrm.modules.employee.excel.dto.EmployeeExcelImportDto;
import com.example.hrm.modules.employee.repository.PositionRepository;
import com.example.hrm.modules.organization.repository.SubDepartmentRepository;
import com.example.hrm.modules.employee.service.AddressResolverService;
import com.example.hrm.shared.enums.EmployeeStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmployeeExcelMapper {

    private final EnumMapper enumMapper;
    private final AddressResolverService addressResolverService;
    private final SubDepartmentRepository subDepartmentRepository;
    private final PositionRepository positionRepository;

    public Employee toEntity(EmployeeExcelImportDto dto) {
        Employee employee = new Employee();

        // ===== Required fields =====
        employee.setCode(dto.getCode());
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());

        // ===== Optional fields =====
        employee.setDateOfBirth(dto.getDateOfBirth());
        employee.setPhone(dto.getPhone());
        employee.setGender(enumMapper.mapGender(dto.getGender()));
        employee.setStatus(
                dto.getStatus() != null
                        ? enumMapper.mapEmployeeStatus(dto.getStatus())
                        : EmployeeStatus.ACTIVE
        );
        employee.setJoinDate(dto.getJoinDate() != null ? dto.getJoinDate() : employee.getJoinDate());

        // ===== Address =====
        Address address = addressResolverService.resolveAddress(
                dto.getProvince(),
                dto.getDistrict(),
                dto.getWard(),
                dto.getStreet()
        );
        employee.setAddress(address);

        // ===== Organization =====
        employee.setSubDepartment(
                subDepartmentRepository.findByNameAndIsDeletedFalse(dto.getDepartmentName())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng ban: " + dto.getDepartmentName()))
        );

        employee.setPosition(
                positionRepository.findByNameAndIsDeletedFalse(dto.getPositionName())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy chức vụ: " + dto.getPositionName()))
        );

        // ===== Defaults =====
        employee.setShiftType(null); // nếu Excel chưa có ca làm
        employee.setIsDeleted(false);

        return employee;
    }
}
