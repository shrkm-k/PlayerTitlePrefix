package me.shirakame.plugins.playertitleprefix;

import me.shirakame.plugins.playertitleprefix.command.TitleCommandTabs;
import me.shirakame.plugins.playertitleprefix.command.TitleCommands;
import me.shirakame.plugins.playertitleprefix.filemanager.TitleFileManager;
import me.shirakame.plugins.playertitleprefix.lang.LanguageManager;
import me.shirakame.plugins.playertitleprefix.listener.DialogListener;
import me.shirakame.plugins.playertitleprefix.listener.InventoryGuiListener;
import me.shirakame.plugins.playertitleprefix.listener.PlayerJoinListener;
import me.shirakame.plugins.playertitleprefix.team.TeamEditor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public final class PlayerTitlePrefix extends JavaPlugin {

    private LanguageManager lang;
    private TitleFileManager TitleFileManager;
    private TeamEditor TeamEditor;

    @Override
    public void onEnable() {

        //Configをロード。ない場合は生成。
        saveDefaultConfig();

        //言語ファイルの初期設定
        String[] languages = {"en", "ja"};
        File langFolder = new File(getDataFolder(), "lang");
        if(!langFolder.exists() && !langFolder.mkdirs()){
            getLogger().warning(PlainTextComponentSerializer.plainText().serialize(lang().get("langFolder_create_failed")));
        }
        for(String langName: languages){
            File langFiles = new File(getDataFolder(),"lang/" + langName + ".yml");
            if(!langFiles.exists()){
                saveResource("lang/" + langName + ".yml", false);
            }
        }

        //プレイヤーの称号データファイルの作成
        File data_folder = new File(getDataFolder(),"data");
        if(!data_folder.exists() && !data_folder.mkdirs()){
            getLogger().warning("data folder can't create.");
        }

        TitleFileManager = new TitleFileManager(this);
        TitleFileManager.loadTitleConfig();

        //翻訳の初期設定
        lang = new LanguageManager(this);
        lang.load(getConfig().getString("language","en"));

        //称号の初期設定
        TeamEditor = new TeamEditor(this);
        TeamEditor.setupTeam();

        //コマンドを読み込み。
        PluginCommand cmd = Objects.requireNonNull(getCommand("playertitleprefix"));
        cmd.setExecutor(new TitleCommands(this));
        cmd.setTabCompleter(new TitleCommandTabs(this));

        Bukkit.getPluginManager().registerEvents(new DialogListener(this),this);
        Bukkit.getPluginManager().registerEvents(new InventoryGuiListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this),this);

        getLogger().info(PlainTextComponentSerializer.plainText().serialize(lang().get("load_plugin")));

    }

    @Override
    public void onDisable() {

        getLogger().info(PlainTextComponentSerializer.plainText().serialize(lang().get("disabled_plugin")));

    }


    public LanguageManager lang(){
        return lang;
    }

    public TitleFileManager getTitleFileManager(){
        return TitleFileManager;
    }

    public TeamEditor getTeamEditor(){
        return TeamEditor;
    }

}
