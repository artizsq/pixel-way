package com.pixelway.database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;


public class ChestData {

    public Map<String, List<PlayerData.InventorySlot>> allChestsState = new HashMap<>();

    public ChestData() {}


    public List<PlayerData.InventorySlot> getChestContents(String chestId) {
        return allChestsState.getOrDefault(chestId, new ArrayList<>());
    }

    public void setChestContents(String chestId, List<PlayerData.InventorySlot> contents) {
        allChestsState.put(chestId, contents);
    }
}
