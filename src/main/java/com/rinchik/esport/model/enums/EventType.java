package com.rinchik.esport.model.enums;

import java.util.List;

public enum EventType {
    TEAM_BUILDING,
    CORPORATE_PARTY,
    BUSINESS_MEETING,
    TRAINING,
    TOURNAMENT;

    public static List<EventType> getAll() {
        return List.of(TEAM_BUILDING,
                CORPORATE_PARTY,
                BUSINESS_MEETING,
                TRAINING,
                TOURNAMENT);
    }
}
