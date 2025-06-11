package com.pixelway.database;
import com.pixelway.utils.HPChangeListener;
import com.pixelway.utils.MoneyChangeListener;

import java.util.ArrayList;
import java.util.List;

public class PlayerData {
    public String playerName = "Player";
    public float musicVolume = 0.5f;
    public float soundVolume = 1.0f;

    public float x = 527;
    public float y = 540;

    public ArrayList<InventorySlot> inventory = new ArrayList<>(6);
    public int strength = 2;
    public int fishCount = 0;
    public int berryCount = 0;
    public int hp = 30;
    public int shield = 0;
    public int money = 50;
    public String currentMap = "";
    public ArrayList<String> activeMissions = new ArrayList<>();
    public ArrayList<String> dialogIDS = new ArrayList<>();
    public ArrayList<String> chestItems = new ArrayList<>();
    public ArrayList<String> reqTP_items = new ArrayList<>();


    public boolean isGameStarted = false;


    public PlayerData(){}

    public static class InventorySlot {

        public String name;
        public ItemType effectType;
        public int effectValue;
        public int quantity;
        public String itemDesc;
        public String imagePath;

        @Override
        public String toString() {
            return "InventorySlot{" +
                "name='" + name + '\'' +
                ", effectType=" + effectType +
                ", effectValue=" + effectValue +
                ", quantity=" + quantity +
                ", itemDesc='" + itemDesc + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
        }
        public InventorySlot(String name, ItemType effectType, int effectValue, int quantity, String itemDesc, String imagePath) {
            this.name = name;
            this.effectType = effectType;
            this.effectValue = effectValue;
            this.quantity = quantity;
            this.itemDesc = itemDesc;
            this.imagePath = imagePath;
        }
        public InventorySlot(){}
    }


    public enum ItemType {
        HP,
        POWER,
        SHIELD
    }

    public void addItem(String name, ItemType type, int effectValue, int quantity, String desc, String imagePath) {
        int firstFreeSlot = -1;
        for (int i = 0; i < 6; i++) {
            if (i >= inventory.size() || inventory.get(i) == null) {
                firstFreeSlot = i;
                break;
            }
        }

        if (firstFreeSlot == -1) {
            return;
        }

        InventorySlot slot = new InventorySlot();
        slot.name = name;
        slot.effectType = type;
        slot.effectValue = effectValue;
        slot.quantity = quantity;
        slot.itemDesc = desc;
        slot.imagePath = imagePath;

        while (inventory.size() <= firstFreeSlot) {
            inventory.add(null);
        }

        inventory.set(firstFreeSlot, slot);
    }



    private transient List<MoneyChangeListener> moneyListeners = new ArrayList<>();

    public void addMoneyChangeListener(MoneyChangeListener listener) {
        moneyListeners.add(listener);
    }

    public void removeMoneyChangeListener(MoneyChangeListener listener) {
        moneyListeners.remove(listener);
    }


    public void addMoney(int amount) {
        this.money += amount;
        notifyMoneyChanged();
    }

    public void subtractMoney(int amount) {
        this.money -= amount;
        notifyMoneyChanged();
    }

    private void notifyMoneyChanged() {
        for (MoneyChangeListener listener : moneyListeners) {
            listener.onMoneyChanged(this.money);
        }
    }

    private transient List<HPChangeListener> hpChangeListeners = new ArrayList<>();
    public void addHPListener(HPChangeListener listener){
        hpChangeListeners.add(listener);
    }

    public void removeHPListener(HPChangeListener listener){
        hpChangeListeners.remove(listener);
    }
    public void notifyHPListener(){
        for (HPChangeListener listener : hpChangeListeners) {
            listener.onHPchanged(this.hp);
        }
    }
    public void substractHP(int HP){
        this.hp -= HP;
        notifyHPListener();
    }
}
