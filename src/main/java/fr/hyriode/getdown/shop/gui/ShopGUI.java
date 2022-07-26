package fr.hyriode.getdown.shop.gui;

import fr.hyriode.getdown.language.GDMessage;
import fr.hyriode.getdown.shop.ShopItem;
import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AstFaster
 * on 23/07/2022 at 23:29
 */
public abstract class ShopGUI extends HyriInventory {

    public static class Manager {

        private static Manager instance;

        private final List<Category> categories;

        public Manager() {
            instance = this;
            this.categories = new ArrayList<>();

            this.registerCategory(new Category(Material.GOLD_SWORD, GDMessage.GUI_SHOP_CATEGORY_WEAPONS_NAME, 0, WeaponsGUI.class));
            this.registerCategory(new Category(Material.LEATHER_CHESTPLATE, GDMessage.GUI_SHOP_CATEGORY_LEATHER_EQUIPMENT_NAME, 1, LeatherEquipmentGUI.class));
            this.registerCategory(new Category(Material.IRON_CHESTPLATE, GDMessage.GUI_SHOP_CATEGORY_IRON_EQUIPMENT_NAME, 2, IronEquipmentGUI.class));
            this.registerCategory(new Category(Material.DIAMOND_CHESTPLATE, GDMessage.GUI_SHOP_CATEGORY_DIAMOND_EQUIPMENT_NAME, 3, DiamondEquipmentGUI.class));
            this.registerCategory(new Category(Material.EXP_BOTTLE, GDMessage.GUI_SHOP_CATEGORY_LEVELS_NAME, 4, LevelsGUI.class));
            this.registerCategory(new Category(Material.ENCHANTMENT_TABLE, GDMessage.GUI_SHOP_CATEGORY_ENCHANTMENT_NAME, 5, EnchantGUI.class));
            this.registerCategory(new Category(Material.COOKED_BEEF, GDMessage.GUI_SHOP_CATEGORY_FOOD_NAME, 6, FoodGUI.class));
            this.registerCategory(new Category(Material.POTION, GDMessage.GUI_SHOP_CATEGORY_POTIONS_NAME, 7, PotionsGUI.class));
        }

        public void registerCategory(Category category) {
            this.categories.add(category);
        }

        public List<Category> getCategories() {
            return this.categories;
        }

        static Manager get() {
            return instance == null ? new Manager() : instance;
        }

    }

    public static class Category {

        private final ItemStack item;

        private final GDMessage name;
        private final int slot;

        private final Class<? extends ShopGUI> guiClass;

        public Category(ItemStack item, GDMessage name, int slot, Class<? extends ShopGUI> guiClass) {
            if (slot >= 9) {
                throw new IllegalArgumentException("Slot must be less than 9!");
            }
            this.item = item;
            this.name = name;
            this.slot = slot;
            this.guiClass = guiClass;
        }

        public Category(Material material, GDMessage name, int slot, Class<? extends ShopGUI> guiClass) {
            this(new ItemStack(material), name, slot, guiClass);
        }

        public ItemStack getItem() {
            return this.item;
        }

        public GDMessage getName() {
            return this.name;
        }

        public int getSlot() {
            return this.slot;
        }

        public Class<? extends ShopGUI> getGuiClass() {
            return this.guiClass;
        }

    }

    public ShopGUI(Player owner) {
        super(owner, name(owner, "gui.shop.name"), 2 * 9);

        for (Category category : Manager.get().getCategories()) {
            this.setItem(category.getSlot(), new ItemBuilder(category.getItem())
                    .withName(category.getName().asString(this.owner))
                    .withAllItemFlags()
                    .build(),
                    event -> {
                        this.owner.playSound(this.owner.getLocation(), Sound.CLICK, 0.6F, 1.0F);

                        this.openSubGUI(category.getGuiClass());
                    });
        }
    }

    public void addItem(int slot, ShopItem shopItem) {
        this.setItem(9 + slot, shopItem.createItem(this.owner), event -> shopItem.buy(this.owner));
    }

    private void openSubGUI(Class<? extends ShopGUI> guiClass) {
        if (this.getClass() == guiClass) {
            return;
        }

        try {
            final Constructor<? extends ShopGUI> constructor = guiClass.getDeclaredConstructor(Player.class);
            final ShopGUI gui = constructor.newInstance(this.owner);

            gui.open();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
