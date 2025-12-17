package me.shirakame.plugins.playertitleprefix.inventorygui;

import me.shirakame.plugins.playertitleprefix.data.PlayerTitleData;
import me.shirakame.plugins.playertitleprefix.PlayerTitlePrefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Team;

import java.io.IOException;
import java.util.*;

public class TitleInventoryGui {

    private final PlayerTitlePrefix plugin;
    private final Map<String, String> titleNames = new HashMap<>();
    private final Map<String, String> titlePermissions = new HashMap<>();
    private final Map<String, String> titleColors = new HashMap<>();
    private final List<String> keys = new ArrayList<>();
    private final List<String> admin_titles = new ArrayList<>();
    private int admin_title_num = 0;
    private final ConfigurationSection titles;

    public TitleInventoryGui(PlayerTitlePrefix plugin){
        this.plugin = plugin;
        titles = plugin.getConfig().getConfigurationSection("Titles");
        if(titles == null){
            plugin.getLogger().warning("No Titles were found.");
        }else if(titles.getKeys(false).isEmpty()){
            plugin.getLogger().warning("No Titles were found.");
        }else{
            for (String key : titles.getKeys(false)) {
                keys.add(key);
                titleNames.put(key, titles.getString(key + ".name"));
                titlePermissions.put(key, titles.getString(key + ".permission"));
                titleColors.put(key, titles.getString(key + ".color"));
                if(titles.getBoolean(key + ".isAdmin", false)){
                    admin_title_num++;
                    admin_titles.add(key);
                }
            }
        }
        keys.removeAll(admin_titles);
        keys.addAll(admin_titles);
    }

    public void openInv(Player player, Inventory inv){
        player.openInventory(inv);
    }

    public Inventory createTitleInventory(Player player, TitleGUIInvHolder guiHolder) throws IOException {

        if(keys.isEmpty()) return null;
        Team now_team = Bukkit.getScoreboardManager().getMainScoreboard().getEntityTeam(player);
        String now_player_title;
        if(now_team == null){
            now_player_title = null;
        }else{
            now_player_title = PlainTextComponentSerializer.plainText().serialize(now_team.prefix()).replace("【", "").replace("】", "");
        }

        int titles_num = titleNames.size();
        int player_have_title_num = 0;
        Set<String> all_titles = titles.getKeys(false);
        int max_title_num = all_titles.size() - admin_title_num;
        List<String> have_titles = new ArrayList<>();

        Inventory inv = Bukkit.createInventory(guiHolder, 54, plugin.lang().get("titles_inv_name"));

        ItemStack black_stained_glass_pane = create_item(Material.BLACK_STAINED_GLASS_PANE, Component.text(""));
        ItemStack pre_arrow = create_item(Material.ARROW, plugin.lang().get("inv_pre_arrow"), "pre");
        ItemStack next_arrow = create_item(Material.ARROW, plugin.lang().get("inv_next_arrow"), "next");

        int now_page = guiHolder.getNowPage();
        int title_item_set_num = 28 * now_page;

        for(int i = 0; i< inv.getSize(); i++){
            if((i >= 10 && i <= 16) || (i >= 19 && i <= 25) || (i >= 28 && i <= 34) || (i >= 37 && i <= 43)) {
                if (keys.size() > title_item_set_num) {
                    String key = keys.get(title_item_set_num);
                    if(admin_titles.contains(key) && !player.isOp()){
                        title_item_set_num++;
                        continue;
                    }
                    if (!player.hasPermission(titlePermissions.get(key)) && !Objects.equals(titlePermissions.get(key), "none")) {
                        ItemStack gray_dye = create_item(Material.GRAY_DYE, plugin.lang().get("have_not_yet_title"));
                        inv.setItem(i, gray_dye);
                    }else if(!Objects.equals(now_player_title, titleNames.get(key))) {
                        ItemStack red_dye = create_item(Material.RED_DYE, Component.text(titleNames.get(key), NamedTextColor.NAMES.value(titleColors.get(key))), key);
                        inv.setItem(i, red_dye);
                    }else{
                        ItemStack lime_dye = create_item(Material.LIME_DYE, Component.text(titleNames.get(key), NamedTextColor.NAMES.value(titleColors.get(key))), key);
                        inv.setItem(i, lime_dye);
                        guiHolder.setLimeSlot(now_page, i);
                    }
                    title_item_set_num++;
                }
            }else if(i == 45 && now_page > 0){
                inv.setItem(i, pre_arrow);
            }else if(i == 53 && titles_num > 28 + 28 * now_page){
                inv.setItem(i, next_arrow);
            }else{
                inv.setItem(i,black_stained_glass_pane);
            }
        }

        for (String key : keys) {
            if (player.hasPermission(titlePermissions.get(key)) || Objects.equals(titlePermissions.get(key), "none")){
                player_have_title_num++;
                have_titles.add(titleNames.get(key));
            }
        }

        if(player.isOp()) player_have_title_num -= admin_title_num;
        Double have_title_percent_num = Math.floor((10f * player_have_title_num / max_title_num)*10f);
        String have_title_percent = String.format("%.1f", have_title_percent_num);
        String num_of_have_titles = String.format("%d", player_have_title_num);
        String max_title = String.format("%d", max_title_num);

        ItemStack player_head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta phead_meta = (SkullMeta) player_head.getItemMeta();
        List<Component> phead_lore = new ArrayList<>();
        phead_lore.add(plugin.lang().get("num_of_player_have_titles").append(Component.text(" " + have_title_percent + "% (" + num_of_have_titles + " / " + max_title + ")")));
        phead_meta.setOwningPlayer(player);
        phead_meta.customName(Component.text(player.getName()).append(plugin.lang().get("player_skull_name")));
        phead_meta.lore(phead_lore);
        player_head.setItemMeta(phead_meta);
        inv.setItem(4,player_head);

        ItemStack iron_block = new ItemStack(Material.IRON_BLOCK);
        ItemMeta iron_block_meta = iron_block.getItemMeta();
        iron_block_meta.customName(plugin.lang().get("title_page").append(Component.text(now_page+1, NamedTextColor.YELLOW)));
        iron_block.setItemMeta(iron_block_meta);
        inv.setItem(49, iron_block);

        PlayerTitleData data = new PlayerTitleData(plugin);
        data.savePlayerTitleData(player.getUniqueId(), player.getName(), have_title_percent_num, have_titles);
        return inv;
    }

    private ItemStack create_item(Material material, Component item_name){
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.customName(item_name);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack create_item(Material material, Component item_name, String key){
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        NamespacedKey nsk = new NamespacedKey(plugin, "title_key");
        meta.customName(item_name);
        meta.getPersistentDataContainer().set(nsk, PersistentDataType.STRING, key);
        item.setItemMeta(meta);
        return item;
    }

}
