package intol.bftmap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class BFTMapMessage<K,V> implements Serializable {
    private BFTMapRequestType type;
    private K key;
    private V value;
    private HashSet<K> keySet;
    private int size;

    public BFTMapMessage() {
    }

    public static <K,V> byte[] toBytes(BFTMapMessage<K,V> message) throws IOException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
        objOut.writeObject(message);

        objOut.flush();
        byteOut.flush();

        return byteOut.toByteArray();
    }

    @SuppressWarnings("unchecked")
    public static <K,V> BFTMapMessage<K,V> fromBytes(byte[] rep) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(rep);
        ObjectInputStream objIn = new ObjectInputStream(byteIn);
        return (BFTMapMessage<K,V>) objIn.readObject();
    }

    public BFTMapRequestType getType() {
        return type;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @SuppressWarnings("unchecked")
    public void setKey(Object key) {
        this.key = (K)key;
    }

    @SuppressWarnings("unchecked")
    public void setValue(Object value) {
        this.value = (V)value;
    }

    @SuppressWarnings("unchecked")
    public void setKeySet(Object keySet) {
        this.keySet = new HashSet<>((Set<K>)keySet);
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setType(BFTMapRequestType type) {
        this.type = type;
    }

    public Set<K> getKeySet() {
        return keySet;
    }

    public int getSize() {
        return size;
    }
}
