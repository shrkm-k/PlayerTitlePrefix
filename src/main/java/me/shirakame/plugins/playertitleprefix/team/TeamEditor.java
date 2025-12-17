package me.shirakame.plugins.playertitleprefix.team;

import me.shirakame.plugins.playertitleprefix.PlayerTitlePrefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.Objects;
import java.util.Set;


//マイクラのチームを使って称号を処理するためのクラス
public class TeamEditor {

    private final PlayerTitlePrefix plugin;
    public TeamEditor(PlayerTitlePrefix plugin){
        this.plugin = plugin;
    }

    //コンフィグにある各種称号の初期設定を行う
    public void setupTeam(){
        var board = Bukkit.getScoreboardManager().getMainScoreboard();
        //プラグインの起動時、マイクラ内に存在する古い称号を全て削除する
        ConfigurationSection config = plugin.getConfig().getConfigurationSection("Titles");

        if(!(config == null)) {
            Set<String> keys = config.getKeys(false);

            //コンフィグに存在しない称号の削除
            for(Team team: board.getTeams()){
                if(team.getName().startsWith("ptp_")){
                    String key = team.getName().substring(4);
                    if(!keys.contains(key)){
                        team.unregister();
                    }
                }
            }

            //プラグインの起動時、コンフィグにある称号を設定する
            for (String key : config.getKeys(false)) {
                Team team = board.getTeam("ptp_" + key);
                NamedTextColor color = NamedTextColor.NAMES.value(config.getString(key + ".color", "white").toLowerCase());
                if (team == null) team = board.registerNewTeam("ptp_" + key);
                team.prefix(Component.text("【" + Objects.requireNonNull(config.getString(key + ".name")) + "】").color(color));
                team.color(NamedTextColor.WHITE);
            }
        }
    }

    //プレイヤーに称号を付け変える
    public void applyTeam(Player player, String teamName){
        var board = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = board.getTeam("ptp_" + teamName);
        Team nowteam = board.getEntityTeam(player);

        if(!(nowteam == null)){
            nowteam.removeEntry(player.getName());
        }
        Objects.requireNonNull(team).addEntry(player.getName());
    }

    //プレイヤーの称号を外す
    public void leaveTeam(Player player){
        var board = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = board.getEntityTeam(player);
        if(!(team == null)) team.removeEntry(player.getName());
    }

    //新しい称号を作成する
    public  void createTeam(String key){
        var board = Bukkit.getScoreboardManager().getMainScoreboard();
        Team a_team = board.getTeam(key);
        //すでにその称号が存在する場合は新しい方を優先するため古い方を削除する
        if(a_team != null) a_team.unregister();
        Team team = board.registerNewTeam("ptp_" + key);
        String title_name = plugin.getConfig().getString("Titles." + key + ".name");
        String title_color_name = plugin.getConfig().getString("Titles." + key + ".color");
        NamedTextColor color = NamedTextColor.NAMES.value(Objects.requireNonNull(title_color_name).toLowerCase());
        team.prefix(Component.text("【" + title_name + "】",color));
        team.color(NamedTextColor.WHITE);
    }

}
