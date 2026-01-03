package com.example.hrm.entity;

import com.example.hrm.enums.PenaltyType;

public interface PenaltySource {
    PenaltyType getPenaltyType();

    int getValue();           // phút / ngày / lần
}
