package me.shirakame.plugins.playertitleprefix.data;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record PlayerTitleDataRecord(UUID uuid, String name, double percent, List<String> titles){
    public PlayerTitleDataRecord{
        titles = titles == null ? List.copyOf(new ArrayList<>()) : List.copyOf(titles);
    }
}
