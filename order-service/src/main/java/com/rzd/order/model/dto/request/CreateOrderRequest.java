package com.rzd.order.model.dto.request;


import com.rzd.common.enums.CargoType;
import com.rzd.common.enums.PackagingType;
import com.rzd.common.enums.WagonType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    @NotBlank(message = "Departure station is required")
    private String departureStation;

    @NotBlank(message = "Destination station is required")
    private String destinationStation;

    @NotNull(message = "Wagon type is required")
    private WagonType requestedWagonType;

    @NotNull(message = "Cargo information is required")
    @Valid
    private CargoDto cargo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CargoDto {

        @NotNull(message = "Cargo type is required")
        private CargoType cargoType;

        @NotNull(message = "Weight is required")
        @Min(value = 1)
        private Integer weightKg;

        @NotNull(message = "Volume is required")
        @Min(value = 1)
        private Integer volumeM3;

        @NotNull(message = "Packaging type is required")
        private PackagingType packagingType;
    }
}