package nl.dsw234.deur.gcm;

import java.util.Map;

public interface MessageListener {
    public void handleMessage(Map<String, Object> message);
}
