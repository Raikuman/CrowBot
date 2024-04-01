package com.raikuman.troubleclub.tamagopet;

public class TamagopetData {

    private int happiness, points, health;
    private boolean isEnraged;

    public TamagopetData() {
        this.happiness = 0;
        this.points = 0;
        this.health = 100;
        this.isEnraged = false;
    }

    public int getHappiness() {
        return happiness;
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
        this.points += points;
    }

    public void changeHealth(int healthMod) {
        this.health += healthMod;
        if (this.health < 0) {
            this.health = 0;
        }
    }
}
