package me.shirakame.plugins.playertitleprefix.data;

import me.shirakame.plugins.playertitleprefix.PlayerTitlePrefix;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

//プレイヤーの称号データを処理するクラス
public class PlayerTitleData {

    private final PlayerTitlePrefix plugin;

    public PlayerTitleData(PlayerTitlePrefix plugin){
        this.plugin = plugin;

        //dataフォルダの作成
        File folder = new File(plugin.getDataFolder(), "data");
        if(!folder.exists()){
            if(!folder.mkdirs()){
                plugin.getLogger().warning("data folder can't create.");
            }
        }
    }

    public void createNewDataFile(UUID uuid, String player_name) throws IOException {
        File new_file = new File(plugin.getDataFolder(), "data/" + uuid + ".yml");
        if(!new_file.exists()){
            if(!new_file.createNewFile()){
                plugin.getLogger().warning("Creating New File was failed.");
                return;
            }

            YamlConfiguration data = YamlConfiguration.loadConfiguration(new_file);
            data.set("PlayerName", player_name);
            data.set("TitlePercent", 0.0);
            data.set("Titles", new ArrayList<>());
            data.save(new_file);
        }
    }

    public void savePlayerTitleData(UUID uuid, String player_name, double percent, List<String> have_titles){
        try {
            File save_file = new File(plugin.getDataFolder(), "data/" + uuid + ".yml");
            if (!save_file.exists()) createNewDataFile(uuid, player_name);
            YamlConfiguration data = YamlConfiguration.loadConfiguration(save_file);
            data.set("PlayerName", player_name);
            data.set("TitlePercent", percent);
            data.set("Titles", have_titles);
            data.save(save_file);
        }catch(IOException e){
            plugin.getLogger().log(Level.WARNING, "data save failed : " + uuid, e);
        }
    }
}
