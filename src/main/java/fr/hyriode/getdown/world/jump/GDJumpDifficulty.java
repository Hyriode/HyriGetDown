package fr.hyriode.getdown.world.jump;

import fr.hyriode.api.language.HyriLanguageMessage;

/**
 * Created by AstFaster
 * on 26/07/2022 at 11:29
 */
public enum GDJumpDifficulty {

    HARD("hard", 0.01, 200),
    MEDIUM("medium", 0.016, 250),
    EASY("easy", 0.025, 300);

    private HyriLanguageMessage displayName;

    private final String name;
    private final double blocksPercentage;
    private final int coinsReward;

    GDJumpDifficulty(String name, double blocksPercentage, int coinsReward) {
        this.name = name;
        this.blocksPercentage = blocksPercentage;
        this.coinsReward = coinsReward;
    }

    public double getBlocksPercentage() {
        return this.blocksPercentage;
    }

    public HyriLanguageMessage getDisplayName() {
        return this.displayName == null ? this.displayName = HyriLanguageMessage.get("difficulty." + this.name + ".display") : this.displayName;
    }

    public int getCoinsReward() {
        return this.coinsReward;
    }

}
