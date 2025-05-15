package com.pixelway.models;

import java.util.ArrayList;
import java.util.List;

public class DialogData {
    public String text;
    public String name;
    public String imagePath;
    public String option1 = null;
    public String option2 = null;
    public DialogData newDialogData = null;

}

class SuperDialogData extends DialogData {
    public List<String> options;
    public List<SuperDialogData> nextDialogs;

    public SuperDialogData() {
        this.options = new ArrayList<>();
        this.nextDialogs = new ArrayList<>();

    }
}

