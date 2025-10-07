package com.rinchik.esport.exception;

import com.rinchik.esport.model.enums.Game;
import com.rinchik.esport.model.enums.TeamRole;

public class RoleNotMatchesGameException extends RuntimeException {
    public RoleNotMatchesGameException(TeamRole role, Game game) {
        super("Role " + role + " does not match game " + game);
    }
}
