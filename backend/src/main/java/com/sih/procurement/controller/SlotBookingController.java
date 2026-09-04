package com.sih.procurement.controller;

import com.sih.procurement.dto.BookSlotRequest;
import com.sih.procurement.dto.SlotAvailability;
import com.sih.procurement.entity.Centre;
import com.sih.procurement.entity.SlotBooking;
import com.sih.procurement.repository.CentreRepository;
import com.sih.procurement.repository.SlotBookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/centres")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SlotBookingController {

    // Decided with the team: 1-hour slots, 10 farmers per slot
    private static final int SLOT_CAPACITY = 10;
    private static final int OPEN_HOUR = 8;   // centre opens 8 AM
    private static final int CLOSE_HOUR = 17; // centre closes 5 PM -> last slot starts at 16 (4-5 PM)

    private final SlotBookingRepository slotBookingRepository;
    private final CentreRepository centreRepository;

    // Farmer checks this from home before deciding when to come
    @GetMapping("/{centreId}/slots")
    public ResponseEntity<List<SlotAvailability>> getSlots(
            @PathVariable Long centreId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<SlotAvailability> slots = new ArrayList<>();

        for (int hour = OPEN_HOUR; hour < CLOSE_HOUR; hour++) {
            long booked = slotBookingRepository.countByCentreIdAndBookingDateAndSlotHour(centreId, date, hour);
            String label = String.format("%02d:00 - %02d:00", hour, hour + 1);
            slots.add(new SlotAvailability(hour, label, booked, SLOT_CAPACITY, booked < SLOT_CAPACITY));
        }

        return ResponseEntity.ok(slots);
    }

    // Farmer books a specific slot after seeing availability
    @PostMapping("/{centreId}/slots/book")
    public ResponseEntity<?> bookSlot(@PathVariable Long centreId, @RequestBody BookSlotRequest request) {

        long booked = slotBookingRepository.countByCentreIdAndBookingDateAndSlotHour(
                centreId, request.getDate(), request.getHour());

        if (booked >= SLOT_CAPACITY) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("This slot is full. Please choose another time.");
        }

        Centre centre = centreRepository.findById(centreId)
                .orElseThrow(() -> new IllegalArgumentException("Centre not found: " + centreId));

        SlotBooking booking = new SlotBooking();
        booking.setCentre(centre);
        booking.setFarmerName(request.getFarmerName());
        booking.setMobileNumber(request.getMobileNumber());
        booking.setBookingDate(request.getDate());
        booking.setSlotHour(request.getHour());

        return ResponseEntity.ok(slotBookingRepository.save(booking));
    }

    // Operator side: "who is expected today, and when"
    @GetMapping("/{centreId}/slots/bookings")
    public ResponseEntity<List<SlotBooking>> getBookings(
            @PathVariable Long centreId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(
                slotBookingRepository.findByCentreIdAndBookingDateOrderBySlotHourAsc(centreId, date));
    }
}
