package com.example.lab5_team_log121;

public class RedoCommand implements Command{

    private PerspectiveCaretaker receiver = PerspectiveCaretaker.getInstance();

    @Override
    public void execute() {
        this.receiver.redoNext();
    }
    
}
