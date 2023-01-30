package fr.hyriode.getdown.game;

public enum GDAchievement {

    NO_DEATHS(0, "no-deaths", 800),
    NO_DAMAGES(1, "no-damages", 400),
    ;

    private final int id;
    private final String name;
    private final int coins;

    GDAchievement(int id, String name, int coins) {
        this.id = id;
        this.name = name;
        this.coins = coins;
    }

    public static GDAchievement getById(int id) {
        for (GDAchievement achievement : GDAchievement.values()) {
            if (achievement.getId() == id) {
                return achievement;
            }
        }
        return null;
    }

    public int getId() {
        return this.id;
    }

    public String getKey() {
        return "achievement." + this.name + ".name";
    }

    public int getCoins() {
        return this.coins;
    }
}
