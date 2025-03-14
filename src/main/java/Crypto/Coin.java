package Crypto;

import java.util.Random;

import java.io.Serializable;

public class Coin implements Serializable {
    
    private int id;
    private int owner;
    private float value;

    public Coin(float value, int owner) {
        this.id = generateId(owner);
        this.owner = owner;
        this.value = value;
    }

    private int generateId(int owner) {
        Random random = new Random();
        int salt = owner * 31;
        return random.nextInt(100000) + salt;
    }

    public int getId() {
        return id;
    }

    public int getOwner() {
        return owner;
    }

    public float getValue() {
        return value;
    }
}
