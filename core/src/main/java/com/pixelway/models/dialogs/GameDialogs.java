package com.pixelway.models.dialogs;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.pixelway.MainClass;
import com.pixelway.database.PlayerData;
import com.pixelway.models.characters.Player;
import com.pixelway.screens.location.StartIslandScreen;
import com.pixelway.screens.minigames.BossBattleScreen;
import com.pixelway.utils.DialogAction;
import com.pixelway.windows.TeleportWindow;

public class GameDialogs {
    private PlayerData playerData;
    private MainClass game;
    public GameDialogs(MainClass game){
        playerData = game.getPlayerData();
        this.game = game;
    }

    public DialogData starikDialog(){
        DialogData fifthNode = new DialogData();
        fifthNode.name = "Старик";
        fifthNode.text = "Вот тебе немного денег, купи себе что-нибудь";
        fifthNode.imagePath = "starik.png";
        fifthNode.option1 = "Спасибо!";
        fifthNode.dialogAction = new DialogAction() {
            @Override
            public void execute() {
                playerData.addMoney(50);
            }
        };

        DialogData forthNode = new DialogData();
        forthNode.name = "Старик";
        forthNode.text = "Клан Тонель разрушил ВСЁ! Мы сумели восстановить только главное здание, обязательно зайди туда !";
        forthNode.imagePath = "starik.png";
        forthNode.option1 = "Хорошо";
        forthNode.option2 = "Не";
        if(!playerData.dialogIDS.contains("starik")){
            forthNode.newDialogData = fifthNode;
            playerData.dialogIDS.add("starik");
        }


        DialogData thirdNode = new DialogData();
        thirdNode.name = "Старик";
        thirdNode.text = "Ты находишься в деревне Лолокек! Правда, сейчас оно почти разрушено...";
        thirdNode.imagePath = "starik.png";
        thirdNode.option1 = "Что случилось?";
        thirdNode.option2 = "Ладно";
        thirdNode.newDialogData = forthNode;

        DialogData secondNode = new DialogData();
        secondNode.name = "Старик";
        secondNode.text = playerData.playerName + " ? Интересное имя, очень необычное в наших краях.";
        secondNode.imagePath = "starik.png";
        secondNode.option1 = "Что за края?";
        secondNode.option2 = "Спс...";
        secondNode.newDialogData = thirdNode;

        DialogData firstNode = new DialogData();
        firstNode.name = "Старик";
        firstNode.text = "Здравствуй, юноша. Хм, я раньше тебя не видел, кто ты?";
        firstNode.imagePath = "starik.png";
        firstNode.option1 = "Меня зовут...";
        firstNode.option2 = "Неважно.";
        firstNode.newDialogData = secondNode;
        return firstNode;
    }

    public DialogData fishmanDialog(){
        DialogData dialog3 = new DialogData();
        dialog3.name = "Рыбак";
        dialog3.imagePath = "fishmap.png";
        dialog3.option1 = "Хорошо";
        dialog3.text = "Удочка стоит напротив меня, пристумай к делу.";
        dialog3.dialogAction = new DialogAction() {
            @Override
            public void execute() {
                playerData.activeMissions.add("fishing");
            }
        };


        DialogData dialog2 = new DialogData();
        dialog2.name = "Рыбак";
        dialog2.imagePath = "fishmap.png";
        dialog2.option1 = "Я согласен.";
        dialog2.option2 = "Не согласен";
        dialog2.text = "Я скуплю у тебя рыбу. Каждая рыба будет стоит 2 монеты, идет?";
        dialog2.newDialogData = dialog3;



        DialogData dialog1 = new DialogData();
        dialog1.name = "Рыбак";
        dialog1.imagePath = "fishman.png";
        dialog1.option1 = "Давай";
        dialog1.option2 = "Нет.";
        dialog1.text = "Приветствую, хочешь заработать денег?";
        dialog1.newDialogData = dialog2;

        return dialog1;
    }

    public DialogData failFishmanDialog(){
        DialogData dialog1 = new DialogData();
        dialog1.name = "Рыбак";
        dialog1.imagePath = "fishman.png";
        dialog1.text = "Ну что? Я жду.";
        dialog1.option1 = "Продолжить";
        return dialog1;
    }

    public DialogData successFishmanDialog(){
        DialogData dialog1 = new DialogData();
        dialog1.name = "Рыбак";
        dialog1.imagePath = "fishman.png";
        dialog1.text = "Отличный улов! Я дам тебе " + playerData.fishCount * 2 + " монеты за всю партию, согласен?";
        dialog1.option1 = "Да";
        dialog1.option2 = "Нет";
        dialog1.dialogAction = new DialogAction() {
            @Override
            public void execute() {
                playerData.addMoney(playerData.fishCount * 2);
                playerData.activeMissions.remove("fishing");
                playerData.fishCount = 0;
            }
        };


        return dialog1;
    }

    public DialogData trade1Dialog(){
        DialogData dialog5 = new DialogData();
        dialog5.name = "Торговец";
        dialog5.imagePath = "trade1.png";
        dialog5.text = "Слушай, раз ты человек из легенды, вот тебе подарок от меня, надеюсь он тебе поможет.";
        dialog5.option1 = "...";
        dialog5.dialogAction = new DialogAction() {
                @Override
                public void execute() {
                    playerData.dialogIDS.add("trade1");
                    playerData.inventory.add(
                        new PlayerData.InventorySlot("Медаль Жизни", PlayerData.ItemType.HP, 5, 1, "Запах очень сильно напоминает аптеку.", "imgs/items/hpmedal.png")
                    );
                }
            };


        DialogData dialog4 = new DialogData();
        dialog4.name = "Торговец";
        dialog4.imagePath = "trade1.png";
        dialog4.text = "Ходят легенды о человеке, который прибудет сюда из другого мира, он спасет нас от клана \"Тонель\"";
        dialog4.option1 = "Ого";
        dialog4.option2 = "Мне все равно";
        dialog4.newDialogData = dialog5;


        DialogData dialog3 = new DialogData();
        dialog3.imagePath = "trade1.png";
        dialog3.name = "Торговец";
        dialog3.text = "А ты не знаешь? Сейчас на всех островах орудует клан \"Тонель\". Неужели ты...";
        dialog3.option1 = "...?";
        dialog3.option2 = "Ок.";
        dialog3.newDialogData = dialog4;


        DialogData dialog2 = new DialogData();
        dialog2.imagePath = "trade1.png";
        dialog2.name = "Торговец";
        dialog2.text = "Сейчас ты находишься на острове Разаб, тут продаются разные и уникальные вещи!";
        dialog2.option1 = "Почему так пусто?";
        dialog2.option2 = "Понял.";
        dialog2.newDialogData = dialog3;


        DialogData dialog1 = new DialogData();
        dialog1.imagePath = "trade1.png";
        dialog1.name = "Торговец Ро";
        dialog1.text = "Путник, что тебя сюда привело?";
        dialog1.option1 = "Что это за место?";
        dialog1.option2 = "Неважно.";
        dialog1.newDialogData = dialog2;
        return dialog1;
    }

    public DialogData failtrade1Dialog(){
        DialogData dialog1 = new DialogData();
        dialog1.imagePath = "trade1.png";
        dialog1.name = "Торговец Ро";
        dialog1.text = "Что-то случилось?";
        dialog1.option1 = "Нет.";
        return dialog1;
    }

    public DialogData trade2Dialog(){
        DialogData dialog3fail = new DialogData();
        dialog3fail.name = "Торговец По";
        dialog3fail.imagePath = "trade2.png";
        dialog3fail.text = "У тебя не хватает денег, вернись как накопишь.";
        dialog3fail.option1 = "...";




        DialogData dialog3 = new DialogData();
        dialog3.name = "Торговец По";
        dialog3.imagePath = "trade2.png";
        dialog3.text = "Вот твой товар. Говорят, он позволяет попасть на остров клана \"Тонель\", тебе стоит проверить!";
        dialog3.option1 = "Хорошо";
        dialog3.dialogAction = new DialogAction() {
            @Override
            public void execute() {
                playerData.subtractMoney(100);
                playerData.reqTP_items.add("winterKey");
                playerData.dialogIDS.add("trade2");
            }
        };



        DialogData dialog2 = new DialogData();
        dialog2.name = "Торговец По";
        dialog2.imagePath = "trade2.png";
        dialog2.text = "Отлично, у меня есть Winter Key, для тебя он обойдется в 100 монет, по рукам?";
        dialog2.option1 = "По рукам";
        dialog2.option2 = "Нет.";
        if(playerData.money >= 100){
            dialog2.newDialogData = dialog3;
        } else {
            dialog2.newDialogData = dialog3fail;

        }


        DialogData dialog1 = new DialogData();
        dialog1.name = "Торговец По";
        dialog1.imagePath = "trade2.png";
        dialog1.text = "Слушай, у меня есть один товар, я уверен он тебя заинтересует... Показать?";
        dialog1.option1 = "Давай";
        dialog1.option2 = "У меня нет времени.";
        dialog1.newDialogData = dialog2;


        return dialog1;
    }

    public DialogData failtrade2Dialog(){
        DialogData dialogData = new DialogData();
        dialogData.imagePath = "trade2.png";
        dialogData.name = "Торговец По";
        dialogData.text = "Тебе что-то нужно?";
        dialogData.option1 = "Нет.";

        return dialogData;
    }

    public DialogData darkDialogue(Stage stage, Player player, String currentZone){
        DialogData dialogData = new DialogData();
        dialogData.imagePath = "dark.png";
        dialogData.name = "Темный попутчик";
        dialogData.text = "Хочешь вернуться обратно?";
        dialogData.option1 = "Да";
        dialogData.option2 = "Нет";
        dialogData.dialogAction = new DialogAction() {
            @Override
            public void execute() {
                new TeleportWindow(stage, currentZone, game, player);
            }
        };
        return dialogData;
    }

    public DialogData bossDialog(){
        DialogData dialogData2 = new DialogData();
        dialogData2.name = "Голем";
        dialogData2.imagePath = "golem.png";
        dialogData2.text = "Ха-ха-ха, мне нравится твоя самоуверенность. Покажи мне, на что способен.";
        dialogData2.option1 = "Базару 0";
        dialogData2.option2 = "Я не хочу";
        dialogData2.dialogAction = new DialogAction() {
            @Override
            public void execute() {
                game.setScreen(new BossBattleScreen(game));
            }
        };



        DialogData dialogData1 = new DialogData();
        dialogData1.name = "Голем";
        dialogData1.imagePath = "golem.png";
        dialogData1.text = "Так ты и вправду пришел...Считаешь, что сможешь победить меня?";
        dialogData1.option1 = "Да";
        dialogData1.option2 = "Нет";
        dialogData1.newDialogData = dialogData2;
        return dialogData1;
    }


}
