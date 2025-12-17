package me.shirakame.plugins.playertitleprefix.lang;

import me.shirakame.plugins.playertitleprefix.PlayerTitlePrefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

//言語に関するクラス
public class LanguageManager {

    private final PlayerTitlePrefix plugin;
    private FileConfiguration lang;


    public LanguageManager(PlayerTitlePrefix plugin) {
        this.plugin = plugin;
    }

    public void load(String langName){
        File file = new File(plugin.getDataFolder(), "lang/" + langName + ".yml");

        if(!(file.exists())){
            plugin.saveResource("lang/" + langName + ".yml", false);
        }

        lang = YamlConfiguration.loadConfiguration(file);
    }


    public Component get(String key){
        String msg = lang.getString(key);

        if(msg == null){
            String template = lang.getString("plugin_name") + " " +  lang.getString("not_found", "%key% not found");
            return MiniMessage.miniMessage().deserialize(template.replace("%key%", key));
        }

        return MiniMessage.miniMessage().deserialize(msg);
    }

}
