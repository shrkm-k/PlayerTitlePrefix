package me.shirakame.plugins.playertitleprefix.inventorygui;

import me.shirakame.plugins.playertitleprefix.data.PlayerTitleData;
import me.shirakame.plugins.playertitleprefix.PlayerTitlePrefix;
import me.shirakame.plugins.playertitleprefix.filemanager.TitleFileManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
    private final TitleFileManager TitleFileManager;

    public TitleInventoryGui(PlayerTitlePrefix plugin){
        this.plugin = plugin;
        this.TitleFileManager = plugin.getTitleFileManager();
    }

    public void openInv(Player player, Inventory inv){
        player.openInventory(inv);
    }

    public Inventory createTitleInventory(Player player, TitleGUIInvHolder guiHolder) throws IOException {

        Inventory inv = Bukkit.createInventory(guiHolder, 54, plugin.lang().get("titles_inv_name"));
        ItemStack black_stained_glass_pane = create_item(Material.BLACK_STAINED_GLASS_PANE, Component.text(""));

        PlayerTitleData data = new PlayerTitleData(plugin);

        if(TitleFileManager.getTitleKeys(false).isEmpty()){
            ItemStack oak_sign = create_item(Material.OAK_SIGN, plugin.lang().get("no_exist_title"));
            for(int i = 0; i < inv.getSize(); i++){
                if(i == 4) inv.setItem(i, oak_sign);
                else inv.setItem(i, black_stained_glass_pane);
            }
            data.savePlayerTitleData(player.getUniqueId(), player.getName(), 0.0, new ArrayList<>());
            return inv;
        }

        Team now_team = Bukkit.getScoreboardManager().getMainScoreboard().getEntityTeam(player);
        String now_player_title = now_team == null ? null : PlainTextComponentSerializer.plainText().serialize(now_team.prefix()).replace("【", "").replace("】", "");

        List<String> keys = TitleFileManager.getTitleKeys(false);
        List<String> admin_titles = TitleFileManager.getTitleKeys(true);
        Map<String, String> titleNames = TitleFileManager.getTitleMaps("name");
        Map<String, String> titlePermissions = TitleFileManager.getTitleMaps("permission");
        Map<String, String> titleColors = TitleFileManager.getTitleMaps("color");
        int player_have_title_num = 0;
        int titles_num = keys.size();
        int admin_title_num = TitleFileManager.getTitleKeys(true).size();
        int max_title_num = titles_num - admin_title_num;
        List<String> have_titles = new ArrayList<>();

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
                inv.setItem(i, black_stained_glass_pane);
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

        ItemStack iron_block = create_item(Material.IRON_BLOCK, plugin.lang().get("title_page").append(Component.text(now_page+1, NamedTextColor.YELLOW)));
        inv.setItem(49, iron_block);

        //プレイヤーの称号データの保存
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
