package com.sih.procurement.controller;

import com.sih.procurement.entity.Centre;
import com.sih.procurement.repository.CentreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/centres")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CentreController {

    private final CentreRepository centreRepository;

    @PostMapping
    public ResponseEntity<Centre> createCentre(@RequestBody Centre centre) {
        return ResponseEntity.ok(centreRepository.save(centre));
    }

    @GetMapping
    public ResponseEntity<List<Centre>> listCentres() {
        return ResponseEntity.ok(centreRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Centre> getCentre(@PathVariable Long id) {
        return centreRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Lets an admin set/update the procurement schedule info for a centre
    @PatchMapping("/{id}/schedule")
    public ResponseEntity<Centre> updateSchedule(
            @PathVariable Long id,
            @RequestBody ScheduleUpdateRequest request) {

        Centre centre = centreRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Centre not found: " + id));

        centre.setCurrentCommodity(request.getCurrentCommodity());
        centre.setMspRatePerQuintal(request.getMspRatePerQuintal());
        centre.setScheduleInfo(request.getScheduleInfo());

        return ResponseEntity.ok(centreRepository.save(centre));
    }

    // Small inline DTO - kept here since it's only used by this one endpoint
    public static class ScheduleUpdateRequest {
        private String currentCommodity;
        private Double mspRatePerQuintal;
        private String scheduleInfo;

        public String getCurrentCommodity() { return currentCommodity; }
        public void setCurrentCommodity(String v) { this.currentCommodity = v; }
        public Double getMspRatePerQuintal() { return mspRatePerQuintal; }
        public void setMspRatePerQuintal(Double v) { this.mspRatePerQuintal = v; }
        public String getScheduleInfo() { return scheduleInfo; }
        public void setScheduleInfo(String v) { this.scheduleInfo = v; }
    }
}
