package com.raikuman.troubleclub.tamagopet;

import net.dv8tion.jda.api.entities.User;

public class TamagopetStats {

    private final User user;
    private final int food, bath, spell, physical, magic;

    public TamagopetStats(User user, int food, int bath, int spell, int physical, int magic) {
        this.user = user;
        this.food = food;
        this.bath = bath;
        this.spell = spell;
        this.physical = physical;
        this.magic = magic;
    }

    public User getUser() {
        return user;
    }

    public int getFood() {
        return food;
    }

    public int getBath() {
        return bath;
    }

    public int getSpell() {
        return spell;
    }

    public int getPhysical() {
        return physical;
    }

    public int getMagic() {
        return magic;
    }
}
