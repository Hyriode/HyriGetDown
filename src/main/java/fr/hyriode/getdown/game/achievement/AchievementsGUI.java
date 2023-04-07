package fr.hyriode.getdown.game.achievement;

import fr.hyriode.getdown.HyriGetDown;
import fr.hyriode.getdown.language.GDMessage;
import fr.hyriode.hyrame.inventory.pagination.PaginatedInventory;
import fr.hyriode.hyrame.inventory.pagination.PaginatedItem;
import fr.hyriode.hyrame.inventory.pagination.PaginationArea;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.utils.Pagination;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by AstFaster
 * on 05/04/2023 at 16:10
 */
public class AchievementsGUI extends PaginatedInventory {

    public AchievementsGUI(Player owner) {
        super(owner, name(owner, "gui.achievements.name"), 6 * 9);
        this.paginationManager.setArea(new PaginationArea(20, 33));

        this.setHorizontalLine(0, 8, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 9).withName(" ").build());
        this.setHorizontalLine(45, 53, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 9).withName(" ").build());

        this.setItem(4, new ItemBuilder(Material.BOOK)
                .withName(GDMessage.GUI_ITEM_ACHIEVEMENTS_NAME.asString(this.owner))
                .withLore(GDMessage.GUI_ITEM_ACHIEVEMENTS_LORE.asList(this.owner))
                .build());

        this.addItems();
    }

    private void addItems() {
        final Pagination<PaginatedItem> pagination = this.paginationManager.getPagination();

        pagination.clear();

        for (GDAchievement achievement : GDAchievement.values()) {
            final boolean completed = HyriGetDown.get().getGame().getPlayer(this.owner).getData().getCompletedAchievements().contains(achievement);
            final ItemBuilder builder = new ItemBuilder(achievement.getIcon())
                    .withName(achievement.getDisplay(this.owner))
                    .withLore(achievement.getDescription(this.owner))
                    .appendLore(
                            "",
                            GDMessage.LINE_ACHIEVEMENT_BONUS.asString(this.owner).replace("%coins%", String.valueOf(achievement.getCoins())),
                            "",
                            (completed ? GDMessage.LINE_ACHIEVEMENT_COMPLETED : GDMessage.LINE_ACHIEVEMENT_NOT_COMPLETED).asString(this.owner));

            if (completed) {
                builder.withGlow();
            }

            pagination.add(PaginatedItem.from(builder.build()));
        }

        this.paginationManager.updateGUI();
    }

    @Override
    public void updatePagination(int page, List<PaginatedItem> items) {
        this.addDefaultPagesItems(27, 35);
    }

}
