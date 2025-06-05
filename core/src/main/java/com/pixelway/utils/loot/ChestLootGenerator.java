package com.pixelway.utils.loot;

import com.badlogic.gdx.Gdx;
import com.pixelway.database.PlayerData; // Импортируем PlayerData, чтобы использовать InventorySlot и ItemType
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Генерирует случайные предметы для сундуков в игре.
 * Все сундуки используют одинаковые шансы и пул предметов.
 */
public class ChestLootGenerator {

    private static final Random random = new Random();
    private static final int MAX_CHEST_ITEMS = 3; // Максимум предметов в сундуке

    // Общий пул всех возможных предметов, которые могут выпасть из любого сундука
    private List<LootEntry> allPossibleLoot;

    public ChestLootGenerator() {
        initializeAllPossibleLoot();
    }

    /**
     * Инициализирует общий пул всех возможных предметов, которые могут выпасть.
     * Здесь ты определяешь все предметы в игре, которые могут быть в сундуках,
     * а также их шансы выпадения и количество.
     */
    private void initializeAllPossibleLoot() {
        allPossibleLoot = new ArrayList<>();

        // Пример предметов:
        // LootEntry(String name, PlayerData.ItemType effectType, int effectValue,
        //           String itemDesc, String imagePath,
        //           float dropChance, int minQuantity, int maxQuantity)

        allPossibleLoot.add(new LootEntry("Медаль рыцаря", PlayerData.ItemType.POWER, 3, "Медаль XVI века, судя по всему\nиспользовалась рыцарями.", "imgs/items/strmedal.png", 0.3f, 1, 1));
        allPossibleLoot.add(new LootEntry("Компот здоровья", PlayerData.ItemType.HP, 10, "Сладкий и очень полезный компот", "imgs/items/HPpoi.png", 0.5f, 1, 2));
        allPossibleLoot.add(new LootEntry("Зелье силы", PlayerData.ItemType.POWER, 10, "Странновато выглядит...", "imgs/items/strpoi.png", 0.5f, 1, 1));
        allPossibleLoot.add(new LootEntry("Меч короля Артура", PlayerData.ItemType.SHIELD, 10, "Меч того самого?", "imgs/items/sword.png", 0.2f, 1, 1));
//        allPossibleLoot.add(new LootEntry("Медаль лекаря", PlayerData.ItemType.POWER, 5, "Пахнет больницой", "imgs/items/hpmedal.png", 0.3f, 1, 1));


    }

    /**
     * Генерирует список предметов для одного сундука.
     * Это главный метод, который ты будешь вызывать.
     *
     * @return Список объектов InventorySlot, выпавших из сундука.
     */
    public List<PlayerData.InventorySlot> generateLootForChest() {
        List<PlayerData.InventorySlot> droppedItems = new ArrayList<>();

        // 1. Определяем количество предметов в сундуке
        int numItemsToDrop = determineNumberOfItems();

        if (numItemsToDrop == 0) {
            return droppedItems; // Сундук пуст
        }

        // 2. Для каждого слота пытаемся выбрать предмет из общего пула
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
