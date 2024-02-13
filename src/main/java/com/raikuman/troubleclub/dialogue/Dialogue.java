package com.raikuman.troubleclub.dialogue;

import com.raikuman.troubleclub.Club;

import java.util.List;

public class Dialogue {

    private int chance;
    private List<Line> lines;

    public int getChance() {
        return chance;
    }

    public List<Line> getLines() {
        return lines;
    }

    public void setChance(int chance) {
        this.chance = chance;
    }

    public void setLines(List<Line> lines) {
        this.lines = lines;
    }

    public class Line {

        private Club actor;
        private String sticker, reaction, line;
        private long targetChannel;
        private double typeSpeed, readSpeed;

        public Club getActor() {
            return actor;
        }

        public String getSticker() {
            return sticker;
        }

        public String getReaction() {
            return reaction;
        }

        public String getLine() {
            return line;
        }

        public long getTargetChannel() {
            return targetChannel;
        }

        public double getTypeSpeed() {
            return typeSpeed;
        }

        public double getReadSpeed() {
            return readSpeed;
        }

        public void setActor(Club actor) {
            this.actor = actor;
        }

        public void setSticker(String sticker) {
            this.sticker = sticker;
        }

        public void setReaction(String reaction) {
            this.reaction = reaction;
        }

        public void setLine(String line) {
            this.line = line;
        }

        public void setTargetChannel(long targetChannel) {
            this.targetChannel = targetChannel;
        }

        public void setTypeSpeed(double typeSpeed) {
            this.typeSpeed = typeSpeed;
        }

        public void setReadSpeed(double readSpeed) {
            this.readSpeed = readSpeed;
        }
    }
}
