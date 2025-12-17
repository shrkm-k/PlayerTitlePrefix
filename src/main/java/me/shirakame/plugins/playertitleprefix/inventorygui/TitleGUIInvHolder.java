package me.shirakame.plugins.playertitleprefix.inventorygui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class TitleGUIInvHolder implements InventoryHolder {

    private int lime_slot = -1;
    private int now_page = 0;
    private int lime_slot_page = 0;

    @Override
    public @NotNull Inventory getInventory(){
        return null;
    }

    public void setLimeSlot(int page, int slot){
        this.lime_slot_page = page;
        this.lime_slot = slot;
    }

    public int getLimeSlot(){
        return lime_slot;
    }

    public int getNowPage(){
        return now_page;
    }

    public int getLimeSlotPage(){
        return lime_slot_page;
    }

    public void setNowPage(int page){
        this.now_page = page;
    }
}
