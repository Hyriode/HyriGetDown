package fr.hyriode.getdown.api;

import fr.hyriode.api.mongodb.MongoDocument;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.model.IHyriPlayerData;
import fr.hyriode.getdown.HyriGetDown;
import fr.hyriode.getdown.game.achievement.GDAchievement;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GDData implements IHyriPlayerData {

    private final Set<GDAchievement> completedAchievements = new HashSet<>();

    public void addCompletedAchievement(GDAchievement achievement) {
        this.completedAchievements.add(achievement);
    }

    public Set<GDAchievement> getCompletedAchievements() {
        return this.completedAchievements;
    }

    public void update(IHyriPlayer account) {
        account.getData().add(HyriGetDown.ID, this);
        account.update();
    }

    public static GDData get(UUID playerId) {
        GDData data = IHyriPlayer.get(playerId).getData().read(HyriGetDown.ID, new GDData());

        if (data == null) {
            data = new GDData();
        }
        return data;
    }

    @Override
    public void save(MongoDocument document) {
        document.appendEnums("completedAchievements", this.completedAchievements);
    }

    @Override
    public void load(MongoDocument document) {
        this.completedAchievements.addAll(document.getEnums("completedAchievements", GDAchievement.class));
    }

}
