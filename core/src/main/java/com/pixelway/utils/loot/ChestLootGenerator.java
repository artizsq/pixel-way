package com.pixelway.utils.loot;

import com.badlogic.gdx.Gdx;
import com.pixelway.database.PlayerData;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class ChestLootGenerator {

    private static final Random random = new Random();

    private List<LootEntry> allPossibleLoot;

    public ChestLootGenerator() {
        initializeAllPossibleLoot();
    }


    private void initializeAllPossibleLoot() {
        allPossibleLoot = new ArrayList<>();


        allPossibleLoot.add(new LootEntry("Медаль рыцаря", PlayerData.ItemType.POWER, 3, "Медаль XVI века, судя по всему\nиспользовалась рыцарями.", "imgs/items/strmedal.png", 0.3f, 1, 1));
        allPossibleLoot.add(new LootEntry("Зелье здоровья", PlayerData.ItemType.HP, 10, "Сладкое и очень полезное", "imgs/items/HPpoi.png", 0.5f, 1, 2));
        allPossibleLoot.add(new LootEntry("Зелье силы", PlayerData.ItemType.POWER, 10, "Странновато выглядит...", "imgs/items/strpoi.png", 0.5f, 1, 1));
        allPossibleLoot.add(new LootEntry("Меч короля Артура", PlayerData.ItemType.SHIELD, 10, "Меч того самого?", "imgs/items/sword.png", 0.2f, 1, 1));


    }


    public List<PlayerData.InventorySlot> generateLootForChest() {
        List<PlayerData.InventorySlot> droppedItems = new ArrayList<>();

        int numItemsToDrop = determineNumberOfItems();

        if (numItemsToDrop == 0) {
            return droppedItems;
        }

        for (int i = 0; i < numItemsToDrop; i++) {
            LootEntry chosenEntry = chooseRandomLootEntry(allPossibleLoot);

            if (chosenEntry != null) {
                int quantity = random.nextInt(chosenEntry.maxQuantity - chosenEntry.minQuantity + 1) + chosenEntry.minQuantity;

                PlayerData.InventorySlot slot = new PlayerData.InventorySlot(
                    chosenEntry.name,
                    chosenEntry.effectType,
                    chosenEntry.effectValue,
                    quantity,
                    chosenEntry.itemDesc,
                    chosenEntry.imagePath
                );
                droppedItems.add(slot);
            }
        }
        return droppedItems;
    }

    private int determineNumberOfItems() {
        float roll = random.nextFloat();

        if (roll < 0.05f) {
            return 0;
        } else if (roll < 0.10f) {
            return 3; //
        } else if (roll < 0.25f) {
            return 2;
        } else {
            return 1;
        }
    }


    private LootEntry chooseRandomLootEntry(List<LootEntry> lootTable) {
        float totalWeight = 0;
        for (LootEntry entry : lootTable) {
            totalWeight += entry.dropChance;
        }

        if (totalWeight == 0) {
            return null;
        }

        float randomValue = random.nextFloat() * totalWeight;
        float currentWeight = 0;

        for (LootEntry entry : lootTable) {
            currentWeight += entry.dropChance;
            if (randomValue < currentWeight) {
                return entry;
            }
        }
        return null;
    }
}
