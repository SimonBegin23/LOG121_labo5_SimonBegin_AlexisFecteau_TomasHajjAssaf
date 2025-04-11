package com.example.lab5_team_log121;

public class HistoryController{

    //HistoryController devrait, en théorie, gérer les fonctions undo et redo sur l'interface graphique.
    //Plusieurs interfaces peuvent s'y connecter tant qu'elles ont les mêmes fonctions undo et redo

    private Command undo;
    private Command redo;

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
