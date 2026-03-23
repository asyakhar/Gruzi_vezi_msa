package com.rzd.common.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class WagonDTO {
    private UUID id;
    private String wagonNumber;
    private String wagonType;
    private Integer maxWeightKg;
    private Integer maxVolumeM3;
    private String currentStation;
    private String status;
}