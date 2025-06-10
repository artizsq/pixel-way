package com.pixelway.utils.loot;

import com.pixelway.database.PlayerData;

public class LootEntry {
    public String name;
    public PlayerData.ItemType effectType;
    public int effectValue;
    public String itemDesc;
    public String imagePath;
    public float dropChance;
    public int minQuantity;
    public int maxQuantity;

    public LootEntry(String name, PlayerData.ItemType effectType, int effectValue,
                     String itemDesc, String imagePath,
                     float dropChance, int minQuantity, int maxQuantity) {
        this.name = name;
        this.effectType = effectType;
        this.effectValue = effectValue;
        this.itemDesc = itemDesc;
        this.imagePath = imagePath;
        this.dropChance = dropChance;
        this.minQuantity = minQuantity;
        this.maxQuantity = maxQuantity;
    }
}
