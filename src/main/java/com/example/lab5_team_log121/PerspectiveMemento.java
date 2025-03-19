package com.example.lab5_team_log121;

public class PerspectiveMemento {
    private PerspectiveState state;

    public PerspectiveMemento(PerspectiveState state) {
        this.state = state;
        
    }

    public PerspectiveState getState() {
        return state;
    }
}
