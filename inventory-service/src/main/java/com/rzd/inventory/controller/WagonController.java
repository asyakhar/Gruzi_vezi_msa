package com.rzd.inventory.controller;

import com.rzd.common.dto.WagonDTO;
import com.rzd.inventory.model.dto.request.WagonSearchRequest;
import com.rzd.inventory.model.dto.response.WagonAvailabilityResponse;
import com.rzd.inventory.model.entity.Wagon;
import com.rzd.inventory.repository.WagonRepository;
import com.rzd.inventory.service.WagonSearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/dispatcher/wagons")
@RequiredArgsConstructor
public class WagonController {

    private final WagonSearchService wagonSearchService;
    private final WagonRepository wagonRepository;


    @PostMapping("/search")
    public ResponseEntity<List<WagonAvailabilityResponse>> searchWagons(
            @Valid @RequestBody WagonSearchRequest request) {

        List<WagonAvailabilityResponse> wagons = wagonSearchService.findAvailableWagons(request);
        return ResponseEntity.ok(wagons);
    }


    @PostMapping("/{wagonId}/reserve")
    public ResponseEntity<String> reserveWagon(
            @PathVariable UUID wagonId,
            @RequestParam UUID orderId,
            @RequestParam(defaultValue = "30") int minutes) {

        boolean reserved = wagonSearchService.reserveWagon(wagonId, orderId, minutes);
        if (reserved) {
            return ResponseEntity.ok("Вагон успешно зарезервирован на " + minutes + " минут");
        } else {
            return ResponseEntity.badRequest().body("Вагон уже зарезервирован");
        }
    }


    @PostMapping("/{wagonId}/release")
    public ResponseEntity<String> releaseWagon(@PathVariable UUID wagonId) {
        wagonSearchService.releaseWagon(wagonId);
        return ResponseEntity.ok("Вагон освобожден");
    }
    @GetMapping("/{wagonId}/dto")
    public ResponseEntity<WagonDTO> getWagonDTO(@PathVariable UUID wagonId) {
        Wagon wagon = wagonRepository.findById(wagonId)
                .orElseThrow(() -> new RuntimeException("Вагон не найден"));

        WagonDTO dto = WagonDTO.builder()
                .id(wagon.getId())
                .wagonNumber(wagon.getWagonNumber())
                .wagonType(wagon.getWagonType().name())
                .maxWeightKg(wagon.getMaxWeightKg())
                .maxVolumeM3(wagon.getMaxVolumeM3())
                .currentStation(wagon.getCurrentStation())
                .status(wagon.getStatus().name())
                .build();

        return ResponseEntity.ok(dto);
    }
}