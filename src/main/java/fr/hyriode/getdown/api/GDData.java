package fr.hyriode.getdown.api;

import fr.hyriode.api.mongodb.MongoDocument;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.model.IHyriPlayerData;
import fr.hyriode.getdown.HyriGetDown;

import java.util.UUID;

public class GDData implements IHyriPlayerData {

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

    }

    @Override
    public void load(MongoDocument document) {

    }

}
