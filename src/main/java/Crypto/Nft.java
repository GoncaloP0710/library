package Crypto;

import Utils.Utils;

public class Nft {

    private int id;
    private int owner;
    private String name;
    private String uri;
    private float value;

    public Nft(String name, String uri, float value, int owner) {
        this.id = Utils.generateId(owner);
        this.owner = owner;
        this.name = name;
        this.uri = uri;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public int getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String toString() {
        return "Nft with id " + id + " and value " + value + " and name " + name + " and uri " + uri + "\n";
    }

}
