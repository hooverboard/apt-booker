package com.aptBooker.backend.appointment.dto.response;

import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
public class AvailableTimesResponse {
    private List<LocalTime> availableTimes;
}
