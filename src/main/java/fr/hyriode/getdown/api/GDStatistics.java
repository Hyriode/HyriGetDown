package fr.hyriode.getdown.api;

import fr.hyriode.api.mongodb.MongoDocument;
import fr.hyriode.api.mongodb.MongoSerializable;
import fr.hyriode.api.mongodb.MongoSerializer;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.model.IHyriStatistics;
import fr.hyriode.getdown.HyriGetDown;
import fr.hyriode.getdown.game.GDGameType;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GDStatistics implements IHyriStatistics {

    private final Map<GDGameType, Data> dataMap = new HashMap<>();

    public Map<GDGameType, Data> getData() {
        return this.dataMap;
    }

    @Override
    public void save(MongoDocument document) {
        for (Map.Entry<GDGameType, Data> entry : this.dataMap.entrySet()) {
            document.append(entry.getKey().name(), MongoSerializer.serialize(entry.getValue()));
        }
    }

    @Override
    public void load(MongoDocument document) {
        for (Map.Entry<String, Object> entry : document.entrySet()) {
            final MongoDocument dataDocument = MongoDocument.of((Document) entry.getValue());
            final Data data = new Data();

            data.load(dataDocument);

            this.dataMap.put(GDGameType.valueOf(entry.getKey()), data);
        }
    }

    public Data getData(GDGameType gameType) {
        return this.dataMap.merge(gameType, new Data(), (oldValue, newValue) -> oldValue);
    }

    public void update(IHyriPlayer account) {
        account.getStatistics().add(HyriGetDown.ID, this);
        account.update();
    }

    public static GDStatistics get(IHyriPlayer account) {
        GDStatistics statistics = account.getStatistics().read(HyriGetDown.ID, new GDStatistics());

        if (statistics == null) {
            statistics = new GDStatistics();
            statistics.update(account);
        }
        return statistics;
    }

    public static GDStatistics get(UUID playerId) {
        return get(IHyriPlayer.get(playerId));
    }

    public static class Data implements MongoSerializable {

        private long kills;
        private long jumpDeaths;
        private long deathmatchDeaths;

        private long successfulJumps;
        private long earnedCoins;

        private long victories;
        private long gamesPlayed;

        @Override
        public void save(MongoDocument document) {
            document.append("kills", this.kills);
            document.append("jumpDeaths", this.jumpDeaths);
            document.append("deathmatchDeaths", this.deathmatchDeaths);
            document.append("successfulJumps", this.successfulJumps);
            document.append("earnedCoins", this.earnedCoins);
            document.append("victories", this.victories);
            document.append("gamesPlayed", this.gamesPlayed);
        }

        @Override
        public void load(MongoDocument document) {
            this.kills = document.getLong("kills");
            this.jumpDeaths = document.getLong("jumpDeaths");
            this.deathmatchDeaths = document.getLong("deathmatchDeaths");
            this.successfulJumps = document.getLong("successfulJumps");
            this.earnedCoins = document.getLong("earnedCoins");
            this.victories = document.getLong("victories");
            this.gamesPlayed = document.getLong("gamesPlayed");
        }

        public long getKills() {
            return this.kills;
        }

        public void addKills(long kills) {
            this.kills += kills;
        }

        public long getJumpDeaths() {
            return this.jumpDeaths;
        }

        public void addJumpDeaths(long jumpDeaths) {
            this.jumpDeaths += jumpDeaths;
        }

        public long getDeathmatchDeaths() {
            return this.deathmatchDeaths;
        }

        public void addDeathmatchDeaths(long deathmatchDeaths) {
            this.deathmatchDeaths += deathmatchDeaths;
        }

        public long getSuccessfulJumps() {
            return this.successfulJumps;
        }

        public void addSuccessfulJumps(long successfulJumps) {
            this.successfulJumps += successfulJumps;
        }

        public long getEarnedCoins() {
            return this.earnedCoins;
        }

        public void addEarnedCoins(long earnedCoins) {
            this.earnedCoins += earnedCoins;
        }

        public long getVictories() {
            return this.victories;
        }

        public void addVictories(long victories) {
            this.victories += victories;
        }

        public long getGamesPlayed() {
            return this.gamesPlayed;
        }

        public void addGamesPlayed(long gamesPlayed) {
            this.gamesPlayed += gamesPlayed;
        }

    }

}
