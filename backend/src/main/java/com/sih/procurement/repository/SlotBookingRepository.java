package com.sih.procurement.repository;

import com.sih.procurement.entity.SlotBooking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SlotBookingRepository extends JpaRepository<SlotBooking, Long> {

    long countByCentreIdAndBookingDateAndSlotHour(Long centreId, LocalDate date, Integer slotHour);

    List<SlotBooking> findByCentreIdAndBookingDateOrderBySlotHourAsc(Long centreId, LocalDate date);
}
