package com.dubroushchyk.clever.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.dubroushchyk.clever.utils.Constants.DATE_FORMAT_yyyy_MM_dd_HH_mm_ss;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientResponseDto {
    private String agency;
    private String guid;
    private String firstName;
    private String lastName;
    private String status;
    @JsonProperty("dob")
    private LocalDate dateOfBirthday;
    @JsonFormat(pattern = DATE_FORMAT_yyyy_MM_dd_HH_mm_ss)
    private LocalDateTime createdDateTime;
}
