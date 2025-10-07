package com.rinchik.esport.model.enums;

import java.util.List;

public enum Game {
    CS,
    DOTA,
    VALORANT,
    MOBILE_LEGEND;

    public static List<Game> getAll() {
        return List.of(CS,
                DOTA,
                VALORANT,
                MOBILE_LEGEND);
    }
}
