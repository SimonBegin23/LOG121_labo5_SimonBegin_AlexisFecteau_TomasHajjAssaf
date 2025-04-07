package com.example.lab5_team_log121;

public class HistoryController{

    //HistoryController devrait, en théorie, gérer les boutons undo et redo sur l'interface graphique.
    //Deux boutons, dont chacun lance une commande.

    private Command undo = new UndoCommand();
    private Command redo = new RedoCommand();

    public HistoryController(Command undo, Command redo){
        this.undo = undo;
        this.redo = redo;
    }

    public void callUndo(){
        this.undo.execute();
    }

    public void callRedo(){
        this.redo.execute();
    }
}
