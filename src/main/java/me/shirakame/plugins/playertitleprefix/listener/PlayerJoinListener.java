package me.shirakame.plugins.playertitleprefix.listener;

import me.shirakame.plugins.playertitleprefix.data.PlayerTitleData;
import me.shirakame.plugins.playertitleprefix.PlayerTitlePrefix;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;

public class PlayerJoinListener implements Listener {

    private final PlayerTitlePrefix plugin;

    public PlayerJoinListener(PlayerTitlePrefix plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        try{
            PlayerTitleData playerTitleData = new PlayerTitleData(plugin);
            playerTitleData.createNewDataFile(event.getPlayer().getUniqueId(), event.getPlayer().getName());
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
