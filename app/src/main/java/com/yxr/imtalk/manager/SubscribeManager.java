package com.yxr.imtalk.manager;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Presence;

public class SubscribeManager {

    /**
     * 请求通过
     *
     * @param connection
     */
    public static void subscribed(AbstractXMPPConnection connection) {
        try {
            Presence presence = new Presence(Presence.Type.subscribed);
            connection.sendStanza(presence);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 请求被拒绝
     *
     * @param connection
     */
    public static void unsubscribe(AbstractXMPPConnection connection) {
        try {
            Presence presence = new Presence(Presence.Type.unsubscribe);
            connection.sendStanza(presence);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }
}
