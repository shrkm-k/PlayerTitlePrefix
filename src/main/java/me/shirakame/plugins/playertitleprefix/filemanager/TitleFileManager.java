package me.shirakame.plugins.playertitleprefix.filemanager;

import me.shirakame.plugins.playertitleprefix.PlayerTitlePrefix;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class TitleFileManager {
    private final PlayerTitlePrefix plugin;
    private final Map<String, String> titleNames = new HashMap<>();
    private final Map<String, String> titlePermissions = new HashMap<>();
    private final Map<String, String> titleColors = new HashMap<>();
    private final List<String> keys = new ArrayList<>();
    private final List<String> admin_titles = new ArrayList<>();

    public TitleFileManager(PlayerTitlePrefix plugin){
        this.plugin = plugin;
    }

    public void loadTitleConfig(){
        initialFields();

        ConfigurationSection titles = plugin.getConfig().getConfigurationSection("Titles");
        if(titles == null || titles.getKeys(false).isEmpty()){
            plugin.getLogger().warning("No Titles were found.");
            return;
        }

        for(String key: titles.getKeys(false)){
            keys.add(key);
            titleNames.put(key, titles.getString(key + ".name"));
            titlePermissions.put(key, titles.getString(key + ".permission"));
            titleColors.put(key, titles.getString(key + ".color"));
            if(titles.getBoolean(key + ".isAdmin", false)){
                admin_titles.add(key);
            }
        }
        keys.removeAll(admin_titles);
        keys.addAll(admin_titles);

        plugin.getLogger().info("TitleConfig was successfully loaded.");
        plugin.getLogger().info("Load Titles: " + keys.size() + " (Including Admin Titles: " + admin_titles.size() + ")");
    }

    public Map<String, String> getTitleMaps(String category){
        return switch(category){
            case "name" -> titleNames;
            case "permission" -> titlePermissions;
            case "color" -> titleColors;
            default -> new HashMap<>();
        };
    }

    public List<String> getTitleKeys(boolean isAdmin){
        if(isAdmin) return admin_titles;
        else return keys;
    }

    private void initialFields(){
        titleNames.clear();
        titlePermissions.clear();
        titleColors.clear();
        keys.clear();
        admin_titles.clear();
    }

}
