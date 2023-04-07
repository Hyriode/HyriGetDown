package fr.hyriode.getdown.game.achievement;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.getdown.HyriGetDown;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.item.HyriItem;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created by AstFaster
 * on 05/04/2023 at 16:18
 */
public class AchievementsItem extends HyriItem<HyriGetDown> {

    public AchievementsItem(HyriGetDown plugin) {
        super(plugin, "achievements", () -> HyriLanguageMessage.get("item.achievements.name"), null, Material.BOOK);
    }

    @Override
    public void onRightClick(IHyrame hyrame, PlayerInteractEvent event) {
        new AchievementsGUI(event.getPlayer()).open();
    }

}
