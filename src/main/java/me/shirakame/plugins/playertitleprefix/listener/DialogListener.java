package me.shirakame.plugins.playertitleprefix.listener;

import io.papermc.paper.connection.PlayerGameConnection;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import me.shirakame.plugins.playertitleprefix.PlayerTitlePrefix;
import me.shirakame.plugins.playertitleprefix.team.TeamEditor;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.Team;

import java.util.Objects;

//ダイアログから情報が送信された時の処理をまとめたクラス
public class DialogListener implements Listener {

    private final PlayerTitlePrefix plugin;

    public DialogListener(PlayerTitlePrefix plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void DialogResponse(PlayerCustomClickEvent event){
        DialogResponseView view = event.getDialogResponseView();
        TeamEditor team_editor = new TeamEditor(plugin);
        if(view == null)return;

        //送信されたのが称号を追加する画面の情報の時の処理
        if(event.getIdentifier().equals(Key.key("playertitleprefix:add_title_confirm"))){
            String title_key = view.getText("title_key");
            String title_name = view.getText("title_name");
            String title_permission = view.getText("title_permission");
            String title_color = view.getText("title_color");
            Boolean title_is_admin = view.getBoolean("title_is_admin");
            if(Objects.equals(title_permission, "")) title_permission = "none";

            if(event.getCommonConnection() instanceof PlayerGameConnection conn) {
                Player player = conn.getPlayer();

                ConfigurationSection titles = plugin.getConfig().getConfigurationSection("Titles");
                if(titles.getKeys(false).contains(title_key)){
                    player.sendMessage(plugin.lang().get("the_key_already_exist"));
                    return;
                }

                if (title_key != null && title_name != null) {
                    plugin.getConfig().set("Titles." + title_key + ".name", title_name);
                    plugin.getConfig().set("Titles." + title_key + ".permission", title_permission);
                    plugin.getConfig().set("Titles." + title_key + ".color", title_color);
                    plugin.getConfig().set("Titles." + title_key + ".isAdmin", title_is_admin);
                    plugin.saveConfig();
                    team_editor.createTeam(title_key);
                    Component msg = plugin.lang().get("plugin_name").append(plugin.lang().get("add_title_input_successfully"));
                    msg = msg.replaceText(builder -> builder.matchLiteral("<title>").replacement(Component.text(title_name, NamedTextColor.NAMES.value(title_color))));
                    player.sendMessage(msg);
                } else {
                    player.sendMessage(plugin.lang().get("plugin_name").append(plugin.lang().get("add_title_input_wrong")));
                }
            }
        //送信されたのがコンフィグの称号削除の情報だった場合の処理
        }else if(event.getIdentifier().equals(Key.key("playertitleprefix:remove_title_confirm"))){
            String key = view.getText("remove_title");
            if(event.getCommonConnection() instanceof PlayerGameConnection conn) {
                Player player = conn.getPlayer();
                if(key != null) {
                    plugin.getConfig().set("Titles." + key, null);
                    plugin.saveConfig();
                    Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("ptp_" + key);
                    Objects.requireNonNull(team).unregister();
                    player.sendMessage(plugin.lang().get("plugin_name").append(plugin.lang().get("remove_title_successfully")));
                }
            }
        }
    }
}
