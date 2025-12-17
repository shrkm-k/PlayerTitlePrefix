package me.shirakame.plugins.playertitleprefix.command;

import me.shirakame.plugins.playertitleprefix.PlayerTitlePrefix;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

//コマンド使用時の候補表示に関するクラス
public class TitleCommandTabs implements TabCompleter {

    private final PlayerTitlePrefix plugin;

    public TitleCommandTabs(PlayerTitlePrefix plugin){
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String string, String[] args){

        File langFolder = new File(plugin.getDataFolder(), "lang");
        List<String> langFiles = new ArrayList<>();

        if(langFolder.exists()){
            File[] files = langFolder.listFiles(((dir, name) -> name.endsWith(".yml")));
            if(files != null){
                for(File file: files){
                    langFiles.add(file.getName().replace(".yml", ""));
                }
            }
        }else{
            langFiles = Arrays.asList("en", "ja");
        }

        if(args.length == 1){
            return prefixFilter(args[0], Arrays.asList("reload", "change", "config", "lang"));
        }else if (args.length == 2 && Objects.equals(args[0], "lang")){
            return prefixFilter(args[1], langFiles);
        }

        return null;

    }


    //引数の入力フィルタ
    private List<String> prefixFilter(String input, List<String> base){
        input = input.toLowerCase();
        if(input.isEmpty()){
            return base;
        }

        List<String> result = new ArrayList<>();
        for(String s: base){
            if(s.toLowerCase().startsWith(input)){
                result.add(s);
            }
        }

        return result;
    }

}
