package com.raikuman.troubleclub.tamagopet;

public class TamagopetData {

    private int happiness, totalPoints, points, health;
    private boolean isEnraged;

    public TamagopetData() {
        this.happiness = 0;
        this.totalPoints = 0;
        this.points = 0;
        this.health = 100;
        this.isEnraged = false;
    }

    public int getHappiness() {
        return happiness;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public int getPoints() {
        return points;
    }

    public int getHealth() {
        return health;
    }

    public boolean isEnraged() {
        return isEnraged;
    }

    public void setHappiness(int happiness) {
        this.happiness = happiness;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setEnraged(boolean enraged) {
        isEnraged = enraged;
    }

    public void addPoints(int points) {
        this.totalPoints += points;
        this.points += points;
    }

    public void addHappiness(int happiness) {
        this.happiness += happiness;
    }

    public void reduceHealth(int health) {
        this.health -= health;
    }
}
