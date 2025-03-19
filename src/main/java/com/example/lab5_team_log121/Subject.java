package com.example.lab5_team_log121;

public interface Subject {
    void attach(Observer o);
    void detach(Observer o);
    void notifyObservers(Object arg);
}
