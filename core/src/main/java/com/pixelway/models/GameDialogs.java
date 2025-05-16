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
        forthNode.text = "Клан Тонель разрушил ВСЁ! Мы сумели сохранить лишь главный дом, зайди туда как-нибудь.";
        forthNode.imagePath = "starik.png";
        forthNode.option1 = "Хорошо";
        forthNode.option2 = "Не";
        forthNode.newDialogData = fifthNode;


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

}
