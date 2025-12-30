package me.shirakame.plugins.playertitleprefix.listener;

import me.shirakame.plugins.playertitleprefix.PlayerTitlePrefix;
import me.shirakame.plugins.playertitleprefix.team.TeamEditor;
import me.shirakame.plugins.playertitleprefix.inventorygui.TitleGUIInvHolder;
import me.shirakame.plugins.playertitleprefix.inventorygui.TitleInventoryGui;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.IOException;
import java.util.Objects;

public class InventoryGuiListener implements Listener {

    private final PlayerTitlePrefix plugin;

    public InventoryGuiListener(PlayerTitlePrefix plugin){
        this.plugin = plugin;
    }


    @EventHandler
    public void onClick(InventoryClickEvent e) throws IOException {

        if(e.getView().title().equals(plugin.lang().get("titles_inv_name"))){

            if(!(e.getInventory().getHolder() instanceof TitleGUIInvHolder holder)) return;
            e.setCancelled(true);

            Player player = (Player) e.getWhoClicked();
            Inventory inv = e.getInventory();
            int slot = e.getRawSlot();
            ItemStack clicked_item = e.getCurrentItem();

            if(slot < 0 || slot >= inv.getSize()) return;
            if(clicked_item == null) return;

            ItemMeta clicked_item_meta = clicked_item.getItemMeta();
            NamespacedKey key_id = new NamespacedKey(plugin, "title_key");
            TeamEditor team = plugin.getTeamEditor();

            int now_page = holder.getNowPage();

            if(clicked_item.getType() == Material.LIME_DYE){
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                team.leaveTeam(player);
                ItemStack new_item = new ItemStack(Material.RED_DYE);
                ItemMeta new_item_meta = new_item.getItemMeta();
                String key = clicked_item_meta.getPersistentDataContainer().get(key_id, PersistentDataType.STRING);
                new_item_meta.customName(clicked_item.getItemMeta().customName());
                new_item_meta.getPersistentDataContainer().set(key_id, PersistentDataType.STRING, Objects.requireNonNull(key));
                new_item.setItemMeta(new_item_meta);
                inv.setItem(slot, new_item);
                holder.setLimeSlot(now_page,-1);
            }else if(clicked_item.getType() == Material.RED_DYE){
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                String key = clicked_item_meta.getPersistentDataContainer().get(key_id, PersistentDataType.STRING);
                team.applyTeam(player, key);
                //黄緑の染料から赤い染料への変更
                int lime_slot = holder.getLimeSlot();
                if(lime_slot != -1 && now_page == holder.getLimeSlotPage()) {
                    String lime_item_key = inv.getItem(lime_slot).getPersistentDataContainer().get(key_id, PersistentDataType.STRING);
                    ItemStack new_red_item = new ItemStack(Material.RED_DYE);
                    ItemMeta new_red_item_meta = new_red_item.getItemMeta();
                    new_red_item_meta.customName(inv.getItem(lime_slot).getItemMeta().customName());
                    new_red_item_meta.getPersistentDataContainer().set(key_id, PersistentDataType.STRING, lime_item_key);
                    new_red_item.setItemMeta(new_red_item_meta);
                    inv.setItem(lime_slot, new_red_item);
                }
                //赤い染料から黄緑の染料への変更
                ItemStack new_lime_item = new ItemStack(Material.LIME_DYE);
                ItemMeta new_lime_item_meta = new_lime_item.getItemMeta();
                new_lime_item_meta.customName(clicked_item.getItemMeta().customName());
                new_lime_item_meta.getPersistentDataContainer().set(key_id, PersistentDataType.STRING, Objects.requireNonNull(key));
                new_lime_item.setItemMeta(new_lime_item_meta);
                inv.setItem(slot, new_lime_item);
                holder.setLimeSlot(now_page, slot);
            }else if(clicked_item.getType() == Material.ARROW){
                String arrow_key = clicked_item_meta.getPersistentDataContainer().get(key_id, PersistentDataType.STRING);
                TitleInventoryGui new_inv_gui = new TitleInventoryGui(plugin);
                if(Objects.equals(arrow_key, "pre")){
                    holder.setNowPage(now_page - 1);
                    Inventory new_inv = new_inv_gui.createTitleInventory(player, holder);
                    player.openInventory(new_inv);
                }else{
                    holder.setNowPage(now_page + 1);
                    Inventory new_inv = new_inv_gui.createTitleInventory(player, holder);
                    player.openInventory(new_inv);
                }
            }
        }

    }

}
