package Crypto;

import Utils.Utils;

import java.io.Serializable;

public class Coin implements Serializable {
    
    private int id;
    private int owner;
    private float value;

    public Coin(float value, int owner, int counter) {
        this.id = counter;
        this.owner = owner;
        this.value = value;
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

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String toString() {
        return "Coin with id " + id + " and value " + value;
    }
}
