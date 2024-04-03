package com.raikuman.troubleclub.tamagopet;

public class TamagopetData {

    private long points, totalPoints, health;
    private int happiness;
    private double noEventMulti;
    private boolean isEnraged;

    public TamagopetData() {
        this.happiness = 0;
        this.totalPoints = 0;
        this.points = 0;
        this.noEventMulti = 1;
        this.health = 100;
        this.isEnraged = false;
    }

    public int getHappiness() {
        return happiness;
    }

    public long getTotalPoints() {
        return totalPoints;
    }

    public long getPoints() {
        return points;
    }

    public double getNoEventMulti() {
        return noEventMulti;
    }

    public long getHealth() {
        return health;
    }

    public boolean isEnraged() {
        return isEnraged;
    }

    public void setHappiness(int happiness) {
        this.happiness = happiness;
    }

    public void setTotalPoints(long totalPoints) {
        this.totalPoints = totalPoints;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    public void setNoEventMulti(double noEventMulti) {
        this.noEventMulti = noEventMulti;
    }

    public void setHealth(long health) {
        this.health = health;
    }

    public void setEnraged(boolean enraged) {
        isEnraged = enraged;
    }

    public void addPoints(long points) {
        this.totalPoints += points;
        this.points += points;
    }

    public void addHappiness(int happiness) {
        this.happiness += happiness;
    }

    public void reduceHealth(long health) {
        this.health -= health;
        if (this.health < 0) {
            this.health = 0;
        }
    }

    public void resetEventMulti() {
        this.noEventMulti = 1;
    }

    public void addEventMulti(double add) {
        this.noEventMulti += add;
    }
}
