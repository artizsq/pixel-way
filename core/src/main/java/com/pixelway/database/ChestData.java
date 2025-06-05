package com.pixelway.database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * Класс для хранения состояния ВСЕХ сундуков в игре.
 * Сериализуется как один объект.
 */
public class ChestData {

    // Ключ: chestID (уникальный идентификатор сундука на карте)
    // Значение: List<PlayerData.InventorySlot> - список предметов, которые в данный момент находятся в этом сундуке.
    public Map<String, List<PlayerData.InventorySlot>> allChestsState = new HashMap<>();

    // Конструктор по умолчанию для LibGDX Json сериализации/десериализации
    public ChestData() {}

    /**
     * Возвращает текущее содержимое сундука по его ID.
     * Если сундук ранее не был записан, возвращает новый пустой ArrayList.
     * @param chestId Уникальный ID сундука.
     * @return Список предметов в сундуке.
     */
    public List<PlayerData.InventorySlot> getChestContents(String chestId) {
        // Если сундука нет в Map, возвращаем новый пустой список, чтобы не было NullPointerException.
        // Это также сигнал для GameStateManager, что сундук еще не генерировался.
        return allChestsState.getOrDefault(chestId, new ArrayList<>());
    }

    /**
     * Устанавливает (или обновляет) содержимое сундука по его ID.
     * @param chestId Уникальный ID сундука.
     * @param contents Список предметов, который нужно сохранить для этого сундука.
     */
    public void setChestContents(String chestId, List<PlayerData.InventorySlot> contents) {
        allChestsState.put(chestId, contents);
    }
}
