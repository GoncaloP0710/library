/**
 * BFT Map implementation (client side).
 *
 */
package bftsmart.intol.bftmap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Crypto.Coin;
import Crypto.Nft;
import bftsmart.tom.ServiceProxy;

public class BFTMap<K, V> implements Map<K, V> {
    private final Logger logger = LoggerFactory.getLogger("bftsmart");
    private final ServiceProxy serviceProxy;

    public BFTMap(int id) {
        serviceProxy = new ServiceProxy(id);
    }

    /**
     *
     * @param key The key associated to the value
     * @return value The value previously added to the map
     */
    @Override
    public V get(Object key) {
        byte[] rep;
        try {
            BFTMapMessage<K,V> request = new BFTMapMessage<>();
            request.setType(BFTMapRequestType.GET);
            request.setKey(key);

            //invokes BFT-SMaRt
            rep = serviceProxy.invokeUnordered(BFTMapMessage.toBytes(request));
        } catch (IOException e) {
            logger.error("Failed to send GET request");
            return null;
        }

        if (rep.length == 0) {
            return null;
        }
        try {
            BFTMapMessage<K,V> response = BFTMapMessage.fromBytes(rep);
            return response.getValue();
        } catch (ClassNotFoundException | IOException ex) {
            logger.error("Failed to deserialized response of GET request");
            return null;
        }
    }

    /**
     *
     * @param key The key associated to the value
     * @param value Value to be added to the map
     */
    @Override
    public V put(K key, V value) {
        byte[] rep;
        try {
            BFTMapMessage<K,V> request = new BFTMapMessage<>();
            request.setType(BFTMapRequestType.PUT);
            request.setKey(key);
            request.setValue(value);

            //invokes BFT-SMaRt
            rep = serviceProxy.invokeOrdered(BFTMapMessage.toBytes(request));
        } catch (IOException e) {
            logger.error("Failed to send PUT request");
            return null;
        }
        if (rep.length == 0) {
            return null;
        }

        try {
            BFTMapMessage<K,V> response = BFTMapMessage.fromBytes(rep);
            return response.getValue();
        } catch (ClassNotFoundException | IOException ex) {
            logger.error("Failed to deserialized response of PUT request");
            return null;
        }
    }

    @Override
    public int size() {
        byte[] rep;
        try {
            BFTMapMessage<K,V> request = new BFTMapMessage<>();
            request.setType(BFTMapRequestType.SIZE);

            //invokes BFT-SMaRt
            rep = serviceProxy.invokeUnordered(BFTMapMessage.toBytes(request));
        } catch (IOException e) {
            logger.error("Failed to send SIZE request");
            return -1;
        }

        if (rep.length == 0) {
            return -1;
        }
        try {
            BFTMapMessage<K,V> response = BFTMapMessage.fromBytes(rep);
            return response.getSize();
        } catch (ClassNotFoundException | IOException ex) {
            logger.error("Failed to deserialized response of SIZE request");
            return -1;
        }
    }

    @Override
    public V remove(Object key) {

        byte[] rep;
        try {
            BFTMapMessage<K,V> request = new BFTMapMessage<>();
            request.setType(BFTMapRequestType.REMOVE);
            request.setKey(key);

            //invokes BFT-SMaRt
            rep = serviceProxy.invokeOrdered(BFTMapMessage.toBytes(request));
        } catch (IOException e) {
            logger.error("Failed to send REMOVE request");
            return null;
        }

        if (rep.length == 0) {
            return null;
        }
        try {
            BFTMapMessage<K,V> response = BFTMapMessage.fromBytes(rep);
            return response.getValue();
        } catch (ClassNotFoundException | IOException ex) {
            logger.error("Failed to deserialized response of REMOVE request");
            return null;
        }

    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException("You are supposed to implement this method :)");
    }

    @Override
    public boolean containsKey(Object key) {
        throw new UnsupportedOperationException("You are supposed to implement this method :)");
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException("You are supposed to implement this method :)");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("You are supposed to implement this method :)");
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException("You are supposed to implement this method :)");
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException("You are supposed to implement this method :)");
    }

    @Override
    public Collection<V> values() {
        byte[] rep;
        try {
            BFTMapMessage<K,V> request = new BFTMapMessage<>();
            request.setType(BFTMapRequestType.VALUES);

            //invokes BFT-SMaRt
            rep = serviceProxy.invokeUnordered(BFTMapMessage.toBytes(request));
        } catch (IOException e) {
            logger.error("Failed to send VALUES request");
            return null;
        }

        if (rep.length == 0) {
            return null;
        }
        try {
            BFTMapMessage<K,V> response = BFTMapMessage.fromBytes(rep);
            return response.getValues();
        } catch (ClassNotFoundException | IOException ex) {
            logger.error("Failed to deserialized response of VALUES request");
            return null;
        }

    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException("You are supposed to implement this method :)");
    }

    public ArrayList<Object> getValues(ArrayList<K> key_array) {
        ArrayList<Object> value_array = new ArrayList<>();
        for (Object key : key_array) {
            if (get(key) != null) {
                value_array.add(get(key));
            }
        }
        return value_array;
    }

    public Collection<V> values(Class<?> forName) {
        byte[] rep;
        try {
            BFTMapMessage<K,V> request = new BFTMapMessage<>();
            request.setType(BFTMapRequestType.VALUES);

            //invokes BFT-SMaRt
            rep = serviceProxy.invokeUnordered(BFTMapMessage.toBytes(request));
        } catch (IOException e) {
            logger.error("Failed to send VALUES request");
            return null;
        }

        if (rep.length == 0) {
            return null;
        }
        try {
            BFTMapMessage<K,V> response = BFTMapMessage.fromBytes(rep);
            Collection<V> values = response.getValues();
            
            // Remove elements that are not instances of the specified class
            for (Iterator<V> iterator = values.iterator(); iterator.hasNext();) {
                Object obj = iterator.next();
                if (!forName.isInstance(obj)) {
                    iterator.remove();
                }
            }          
            
            return values;
        } catch (ClassNotFoundException | IOException ex) {
            logger.error("Failed to deserialized response of VALUES request");
            return null;
        }
        
    }
}
