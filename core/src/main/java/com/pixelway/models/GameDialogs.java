package com.pixelway.models;

import com.badlogic.gdx.Game;
import com.pixelway.MainClass;
import com.pixelway.database.PlayerData;
import com.pixelway.utils.DialogAction;

public class GameDialogs {
    private PlayerData playerData;
    public GameDialogs(MainClass game){
        playerData = game.getPlayerData();
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
        forthNode.text = "Клан Тонель разрушил ВСЁ! Мы сумели сохранить лишь наш главный дом, зайди туда как-нибудь.";
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
        secondNode.text = playerData.playerName + "? Интересное имя, очень необычное в наших краях.";
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


}
