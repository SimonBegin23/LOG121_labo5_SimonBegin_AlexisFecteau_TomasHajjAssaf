package com.example.lab5_team_log121;

import java.util.Stack;

public class PerspectiveCaretaker {
    Perspective originator = new Perspective();

    Stack<PerspectiveMemento> history = new Stack<>();

    public void saveChange(PerspectiveMemento memento) {
        PerspectiveMemento m = originator.saveState();
        history.push(m);
        System.out.println("État sauvegardé");
    }
    public void undo() {
        if (!history.isEmpty()) {
            PerspectiveMemento m = history.pop();
            originator.restoreState(m);
            System.out.println("Annulation éffectuée");
        }
        if (history.isEmpty()) {
            System.out.println("Aucun état à restaurer");
        }
    }
}
