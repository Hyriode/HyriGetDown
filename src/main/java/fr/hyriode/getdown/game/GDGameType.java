package fr.hyriode.getdown.game;

import fr.hyriode.hyrame.game.HyriGameType;

/**
 * Created by AstFaster
 * on 23/07/2022 at 11:37
 */
public enum GDGameType implements HyriGameType {

    NORMAL("Normal", 8, 12)
    ;

    private final String displayName;
    private final int minPlayers;
    private final int maxPlayers;

    GDGameType(String displayName, int minPlayers, int maxPlayers) {
        this.displayName = displayName;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
    }

    @Override
    public String getName() {
        return this.name();
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public int getMinPlayers() {
        return this.minPlayers;
    }

    @Override
    public int getMaxPlayers() {
        return this.maxPlayers;
    }

}
