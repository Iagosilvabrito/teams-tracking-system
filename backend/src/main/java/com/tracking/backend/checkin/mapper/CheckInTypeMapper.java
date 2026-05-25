package com.tracking.backend.checkin.mapper;

import com.tracking.backend.checkin.entity.CheckInType;

public class CheckInTypeMapper {

    public static CheckInType fromExternal(String externalType) {
        if (externalType == null) return CheckInType.CHECKIN;
        return switch (externalType.toLowerCase()) {
            case "check_in"        -> CheckInType.CHECKIN;
            case "check_out"       -> CheckInType.CHECKOUT;
            case "visit_completed" -> CheckInType.VISIT_COMPLETED;
            case "stop_detected"   -> CheckInType.STOP_DETECTED;
            case "stop_ended"      -> CheckInType.STOP_ENDED;
            case "signal_lost"     -> CheckInType.SIGNAL_LOST;
            case "signal_restored" -> CheckInType.SIGNAL_RESTORED;
            case "low_battery"     -> CheckInType.LOW_BATTERY;
            default                -> CheckInType.CHECKIN;
        };
    }
}
