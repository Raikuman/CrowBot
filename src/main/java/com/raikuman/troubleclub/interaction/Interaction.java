package com.raikuman.troubleclub.interaction;

import com.raikuman.troubleclub.dialogue.Dialogue;

import java.util.List;

public class Interaction {

    private int reqWords;
    private List<String> words;
    private List<Dialogue> dialogues;

    public int getReqWords() {
        return reqWords;
    }

    public List<String> getWords() {
        return words;
    }

    public List<Dialogue> getDialogues() {
        return dialogues;
    }

    public void setReqWords(int reqWords) {
        this.reqWords = reqWords;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    public void setDialogues(List<Dialogue> dialogues) {
        this.dialogues = dialogues;
    }
}
