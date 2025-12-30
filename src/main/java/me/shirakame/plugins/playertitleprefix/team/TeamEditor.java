package me.shirakame.plugins.playertitleprefix.team;

import me.shirakame.plugins.playertitleprefix.PlayerTitlePrefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.Map;
import java.util.Objects;


//マイクラのチームを使って称号を処理するためのクラス
public class TeamEditor {

    private final PlayerTitlePrefix plugin;
    private String symbol_before_title;
    private String symbol_after_title;
    private final Scoreboard board;
    private List<String> keys;
    private Map<String, String> names;
    private Map<String, String> colors;

    public TeamEditor(PlayerTitlePrefix plugin){
        this.plugin = plugin;
        board = Bukkit.getScoreboardManager().getMainScoreboard();
    }

    //コンフィグにある各種称号の初期設定を行う
    public void setupTeam(){

        loadTitlesData();
        loadSymbols();

        if(!(keys == null)){
            //コンフィグに存在しない称号の削除
            for(Team team: board.getTeams()){
                if(team.getName().startsWith("ptp_")){
                    String key = team.getName().substring(4);
                    if(!keys.contains(key)){
                        team.unregister();
                    }
                }
            }

            //コンフィグにある称号を設定する
            for (String key : keys) {
                Team team = board.getTeam("ptp_" + key);
                NamedTextColor color = NamedTextColor.NAMES.value(colors.get(key).toLowerCase());
                if (team == null) team = board.registerNewTeam("ptp_" + key);
                team.prefix(Component.text(symbol_before_title + names.get(key) + symbol_after_title).color(color));
                team.color(NamedTextColor.WHITE);
            }
        }else{
            for(Team team: board.getTeams()){
                if(team.getName().startsWith("ptp_")) team.unregister();
            }
            plugin.getLogger().warning("There were no titles.");
        }
    }

    //プレイヤーに称号を付け変える
    public void applyTeam(Player player, String teamName){
        Team team = board.getTeam("ptp_" + teamName);
        Team nowteam = board.getEntityTeam(player);

        if(!(nowteam == null)){
            nowteam.removeEntry(player.getName());
        }
        Objects.requireNonNull(team).addEntry(player.getName());
    }

    //プレイヤーの称号を外す
    public void leaveTeam(Player player){
        Team team = board.getEntityTeam(player);
        if(!(team == null)) team.removeEntry(player.getName());
    }

    //新しい称号を作成する
    public void createTeam(String key){
        Team a_team = board.getTeam("ptp_" + key);
        //すでにその称号が存在する場合は新しい方を優先するため古い方を削除する
        if(a_team != null) a_team.unregister();
        Team team = board.registerNewTeam("ptp_" + key);
        String title_name = names.get(key);
        String title_color_name = colors.get(key);
        NamedTextColor color = NamedTextColor.NAMES.value(title_color_name.toLowerCase());
        team.prefix(Component.text(symbol_before_title + title_name + symbol_after_title,color));
        team.color(NamedTextColor.WHITE);
    }

    public void removeTeam(String key){
        Team team = board.getTeam("ptp_" + key);
        if(team == null) return;
        team.unregister();
    }

    public void loadTitlesData(){
        keys = plugin.getTitleFileManager().getTitleKeys(false);
        names = plugin.getTitleFileManager().getTitleMaps("name");
        colors = plugin.getTitleFileManager().getTitleMaps("color");
    }

    private void loadSymbols(){
        symbol_before_title = plugin.getConfig().getString("Symbol_before_Title", "");
        symbol_after_title = plugin.getConfig().getString("Symbol_after_Title", "");
    }

}
