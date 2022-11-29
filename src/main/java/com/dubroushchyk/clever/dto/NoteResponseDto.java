package com.dubroushchyk.clever.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.dubroushchyk.clever.utils.Constants.DATE_FORMAT_yyyy_MM_dd_HH_mm_ss;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoteResponseDto {
    private String comments;
    private String guid;
    @JsonFormat(pattern = DATE_FORMAT_yyyy_MM_dd_HH_mm_ss)
    private LocalDateTime modifiedDateTime;
    private String clientGuid;
    @JsonFormat(pattern = DATE_FORMAT_yyyy_MM_dd_HH_mm_ss)
    private LocalDateTime createdDateTime;
    private String loggedUser;
}
