package fr.hyriode.getdown.world.jump;

import fr.hyriode.api.language.HyriLanguageMessage;

/**
 * Created by AstFaster
 * on 26/07/2022 at 11:29
 */
public enum GDJumpDifficulty {

    HARD("hard", 0.01),
    MEDIUM("medium", 0.016),
    EASY("easy", 0.025);

    private HyriLanguageMessage displayName;

    private final String name;
    private final double blocksPercentage;

    GDJumpDifficulty(String name, double blocksPercentage) {
        this.name = name;
        this.blocksPercentage = blocksPercentage;
    }

    public double getBlocksPercentage() {
        return this.blocksPercentage;
    }

    public HyriLanguageMessage getDisplayName() {
        return this.displayName == null ? this.displayName = HyriLanguageMessage.get("difficulty." + this.name + ".display") : this.displayName;
    }

}
