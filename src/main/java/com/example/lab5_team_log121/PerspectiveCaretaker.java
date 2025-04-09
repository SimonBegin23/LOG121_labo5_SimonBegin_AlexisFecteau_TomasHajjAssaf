package com.example.lab5_team_log121;

import java.util.Stack;

public class PerspectiveCaretaker {

    private Stack<PerspectiveMemento> history;

    private static PerspectiveCaretaker instance;

    private PerspectiveMemento currentMemento;

    private PerspectiveCaretaker(){
        this.history = new Stack<>();
    }

    public static PerspectiveCaretaker getInstance(){
        if (instance == null){
            instance = new PerspectiveCaretaker();
        }
        return instance;
    }

    public void undoCurrent() {
        
        if (!history.isEmpty() && currentMemento != null) {
            //reculer au mémento précédent sans dépiler le stack
            int indicePrecedent = history.indexOf(currentMemento) -1;
            if (indicePrecedent > 0){
                currentMemento = history.get(indicePrecedent);

                //rétablir la perspective affectée 
                Perspective originator = currentMemento.getOriginator();
                originator.restoreState(currentMemento);
                System.out.println("Annulation éffectuée");
            } else {
                System.out.println("Vous êtes à l'état initial");
            }
        }
        if (history.isEmpty()) {
            System.out.println("Aucun état à annuler");
        }
    }

    public void redoNext(){
        if (!history.isEmpty() && currentMemento != null){
            //avancer au prochain mémento si le mémento présent n'est pas le top de la pile
            int indiceProchain = history.indexOf(currentMemento) +1;
            if (indiceProchain < history.size() && indiceProchain > 0){
                currentMemento = history.get(indiceProchain);
                
                //rétablir la perspective affectée 
                Perspective originator = currentMemento.getOriginator();
                originator.restoreState(currentMemento);
                System.out.println("État restauré");
            }
        }
        if (currentMemento == history.peek()){
            System.out.println("Aucun état à restaurer");
        }
    }

    public void pushNewMemento(PerspectiveMemento memento){
        //tronquer l'historique à l'état présent et ajouter la prochaine action
        crushHistoryToCurrent();
        history.push(memento);
        currentMemento = memento;
        System.out.println("État sauvegardé");
    }

    private void crushHistoryToCurrent(){
        if (history.contains(currentMemento)){
            //dépiler jusqu'à ce que l'état présent soit le top de la pile
            while (history.peek() != currentMemento){
                history.pop();
            }
        }
        
    }

    public void flushHistory(){
        history.removeAllElements();
    }
}
