package me.shirakame.plugins.playertitleprefix.filemanager;

import me.shirakame.plugins.playertitleprefix.PlayerTitlePrefix;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LangFileManager{
    private final PlayerTitlePrefix plugin;
    private final List<String> langFiles;
    private final String[] default_languages;

    public LangFileManager(PlayerTitlePrefix plugin){
        this.plugin = plugin;
        langFiles = new ArrayList<>();
        default_languages = new String[]{"en", "ja"};
    }

    public void loadLangFiles(){
        File langFolder = new File(plugin.getDataFolder(), "lang");

        if (!langFolder.exists() && !langFolder.mkdirs()) {
            plugin.getLogger().warning(PlainTextComponentSerializer.plainText().serialize(plugin.lang().get("langFolder_create_failed")));
            return;
        }

        for (String langName : default_languages) {
            File langFile = new File(plugin.getDataFolder(), "lang/" + langName + ".yml");
            if (!langFile.exists()) {
                plugin.saveResource("lang/" + langName + ".yml", false);
            }
        }

        File[] files = langFolder.listFiles(((dir, name) -> name.endsWith(".yml")));
        if(files != null){
            for(File file: files){
                langFiles.add(file.getName().replace(".yml", ""));
            }
        }
    }

    public List<String> getLangFiles(){
        return langFiles;
    }
}
