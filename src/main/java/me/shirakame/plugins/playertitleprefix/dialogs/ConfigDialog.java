package me.shirakame.plugins.playertitleprefix.dialogs;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import io.papermc.paper.registry.set.RegistrySet;
import me.shirakame.plugins.playertitleprefix.PlayerTitlePrefix;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//コンフィグのダイアログに関するクラス
public class ConfigDialog {

    private final PlayerTitlePrefix plugin;
    private final Dialog dialog;
    private final RegistrySet<Dialog> configDialogs;
    private final List<String> colors = new ArrayList<>(NamedTextColor.NAMES.keys());
    private final List<SingleOptionDialogInput.OptionEntry> selection_colors = new ArrayList<>();

    public ConfigDialog(PlayerTitlePrefix plugin){
        this.plugin = plugin;
        ConfigurationSection titles = plugin.getConfig().getConfigurationSection("Titles");
        if(!titles.getKeys(false).isEmpty()) {
            //ConfigのTitlesセクションに称号情報がある場合
            configDialogs = RegistrySet.valueSet(RegistryKey.DIALOG, List.of(createAddTitleDialog(), createRemoveTitleDialog()));
            this.dialog = createConfigDialog();
        }else{
            //ConfigのTitlesセクションに称号情報が何もない場合
            configDialogs = RegistrySet.valueSet(RegistryKey.DIALOG, List.of(createAddTitleDialog()));
            this.dialog = createConfigDialog();
        }
    }

    public void openConfigDialog(Player player){
        player.showDialog(dialog);
    }

    //コンフィグ画面のダイアログを作成する
    public Dialog createConfigDialog(){
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(plugin.lang().get("config_dialog"))
                        .body(
                                java.util.List.of(
                                        DialogBody.plainMessage(plugin.lang().get("config_dialog_body")),
                                        DialogBody.plainMessage(Component.text("---------------------------------"))
                                )
                        )
                        .build())
                .type(DialogType.dialogList(
                        configDialogs,
                        ActionButton.create(
                                plugin.lang().get("dialog_cancel"),
                                plugin.lang().get("dialog_cancel_desc"),
                                200,
                                null
                        ),
                        1,
                        200
                )));
    }

    //称号追加の画面のダイアログを作成する
    private Dialog createAddTitleDialog(){
        for(String key: colors){
            selection_colors.add(SingleOptionDialogInput.OptionEntry.create(key, Component.text(key).color(NamedTextColor.NAMES.value(key)),false));
        }

         return Dialog.create(builder -> builder.empty()
                 .base(DialogBase.builder(plugin.lang().get("add_title_dialog"))
                         .body(
                                 java.util.List.of(
                                         DialogBody.plainMessage(plugin.lang().get("add_title_body")),
                                         DialogBody.plainMessage(Component.text("---------------------------------"))
                                 )
                         )
                         .inputs(
                                 java.util.List.of(
                                         DialogInput.text("title_key", plugin.lang().get("title_key_desc"))
                                                 .maxLength(30)
                                                 .build(),
                                         DialogInput.text("title_name", plugin.lang().get("title_name_desc"))
                                                 .maxLength(30)
                                                 .build(),
                                         DialogInput.text("title_permission", plugin.lang().get("title_permission_desc"))
                                                 .maxLength(30)
                                                 .build(),
                                         DialogInput.singleOption("title_color", plugin.lang().get("title_color_desc"), selection_colors)
                                                 .build(),
                                         DialogInput.bool("title_is_admin", plugin.lang().get("title_is_admin"))
                                                 .build()
                         ))
                         .build())
                 .type(DialogType.confirmation(
                         ActionButton.create(
                                 plugin.lang().get("dialog_confirm"),
                                 plugin.lang().get("dialog_confirm_desc"),
                                 100,
                                 DialogAction.customClick(Key.key("playertitleprefix:add_title_confirm"), null)
                         ),

                         ActionButton.create(
                                 plugin.lang().get("dialog_cancel"),
                                 plugin.lang().get("dialog_cancel_desc"),
                                 100,
                                 null
                         )
                 )));
    }

    //称号削除の画面のダイアログを作成する
    private Dialog createRemoveTitleDialog(){
        List<String> titles = plugin.getTitleFileManager().getTitleKeys(false);
        Map<String, String> names = plugin.getTitleFileManager().getTitleMaps("name");
        List<SingleOptionDialogInput.OptionEntry> title_names = new ArrayList<>();

        for (String key : titles) {
            title_names.add(SingleOptionDialogInput.OptionEntry.create(key, Component.text(names.get(key)), false));
        }

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(plugin.lang().get("remove_title_dialog"))
                        .body(
                                java.util.List.of(
                                        DialogBody.plainMessage(plugin.lang().get("remove_title_body")),
                                        DialogBody.plainMessage(Component.text("---------------------------------"))
                                )
                        )
                        .inputs(
                                java.util.List.of(
                                        DialogInput.singleOption(
                                    "remove_title",
                                        plugin.lang().get("title_dialog_select"),
                                        title_names
                                        ).build()
                                )
                        )
                        .build())
                .type(DialogType.confirmation(
                        ActionButton.create(
                                plugin.lang().get("dialog_confirm"),
                                plugin.lang().get("dialog_confirm_desc"),
                                100,
                                DialogAction.customClick(Key.key("playertitleprefix:remove_title_confirm"), null)
                        ),

                        ActionButton.create(
                                plugin.lang().get("dialog_cancel"),
                                plugin.lang().get("dialog_cancel_desc"),
                                100,
                                null
                        )
                )));
    }
}
