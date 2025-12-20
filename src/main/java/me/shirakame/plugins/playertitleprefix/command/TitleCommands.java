package me.shirakame.plugins.playertitleprefix.command;

import me.shirakame.plugins.playertitleprefix.PlayerTitlePrefix;
import me.shirakame.plugins.playertitleprefix.dialogs.ConfigDialog;
import me.shirakame.plugins.playertitleprefix.inventorygui.TitleGUIInvHolder;
import me.shirakame.plugins.playertitleprefix.inventorygui.TitleInventoryGui;
import me.shirakame.plugins.playertitleprefix.team.TeamEditor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

//このプラグインに関するコマンドをまとめたクラス
public class TitleCommands implements CommandExecutor {

    private final PlayerTitlePrefix plugin;

    public TitleCommands(PlayerTitlePrefix plugin){
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        //アーギュメントなしの場合、このプラグインで追加されたコマンド一覧を表示。
        if(args.length == 0){
            //コマンドを管理者のみ使用可能にする。
            if(!(sender.isOp())) return true;
            sender.sendMessage(plugin.lang().get("show_all_commands"));
            sender.sendMessage(Component.text("/playertitleprefix reload",NamedTextColor.YELLOW));
            sender.sendMessage(Component.text("/playertitleprefix change",NamedTextColor.YELLOW));
            sender.sendMessage(Component.text("/playertitleprefix config",NamedTextColor.YELLOW));
            sender.sendMessage(Component.text("/playertitleprefix lang <languages>",NamedTextColor.YELLOW));
            return true;
        }

        //アーギュメント毎に処理を記載。
        switch(args[0].toLowerCase()){
            case "reload":
                //リロードコマンド
                //コマンドを管理者のみ使用可能にする。
                if(!(sender.isOp())) return true;
                if(args.length > 1){
                    sender.sendMessage(plugin.lang().get("plugin_name").append(plugin.lang().get("wrong_args")));
                    return true;
                }
                plugin.reloadConfig();
                plugin.lang().load(plugin.getConfig().getString("language", "en"));
                TeamEditor teamEditor = new TeamEditor(plugin);
                teamEditor.setupTeam();
                sender.sendMessage(plugin.lang().get("plugin_name").append(plugin.lang().get("plugin_reload")));
                break;
            case "change":
                //変更画面表示コマンド
                if(args.length > 1){
                    sender.sendMessage(plugin.lang().get("plugin_name").append(plugin.lang().get("wrong_args")));
                    return true;
                }
                TitleInventoryGui gui = new TitleInventoryGui(plugin);
                TitleGUIInvHolder gui_holder = new TitleGUIInvHolder();
                Inventory inv = null;
                try {
                    inv = gui.createTitleInventory((Player) sender,gui_holder);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                gui.openInv((Player) sender, inv);
                break;
            case "config":
                //コンフィグ画面表示コマンド
                //コマンドを管理者のみ使用可能にする。
                if(!(sender.isOp())) return true;
                if(args.length > 1){
                    sender.sendMessage(plugin.lang().get("plugin_name").append(plugin.lang().get("wrong_args")));
                    return true;
                }
                ConfigDialog configDialog = new ConfigDialog(plugin);
                configDialog.openConfigDialog((Player) sender);
                break;
            case "lang":
                //言語設定を変更するコマンド
                //コマンドを管理者のみ使用可能にする。
                if(!(sender.isOp())) return true;
                if(args.length < 2){
                    sender.sendMessage(plugin.lang().get("plugin_name").append(plugin.lang().get("wrong_args")));
                    return true;
                }
                if(args.length > 2){
                    sender.sendMessage(plugin.lang().get("plugin_name").append(plugin.lang().get("wrong_args")));
                    return true;
                }
                switch(args[1].toLowerCase()){
                    case "en":
                        plugin.getConfig().set("language", "en");
                        plugin.saveConfig();
                        plugin.lang().load(plugin.getConfig().getString("language", "en"));
                        sender.sendMessage(plugin.lang().get("plugin_name").append(plugin.lang().get("lang_change_successfully")));
                        break;
                    case "ja":
                        plugin.getConfig().set("language", "ja");
                        plugin.saveConfig();
                        plugin.lang().load(plugin.getConfig().getString("language", "en"));
                        sender.sendMessage(plugin.lang().get("plugin_name").append(plugin.lang().get("lang_change_successfully")));
                        break;
                    default:
                        sender.sendMessage(plugin.lang().get("plugin_name").append(plugin.lang().get("wrong_args")));
                        break;
                }
                break;
            default:
                //設定していないアーギュメントの場合
                if(!(sender.isOp())) return true;
                sender.sendMessage(plugin.lang().get("plugin_name").append(plugin.lang().get("wrong_args")));
                break;
        }
        return true;
    }
}
