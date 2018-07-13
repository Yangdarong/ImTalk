package com.yxr.imtalk.manager;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

public class ConnectionManager {


    private static XMPPTCPConnection connection;
    private static String host = "10.22.255.75";
    private static int port = 5222;
    private static String serviceName = "desktop-m028420";

    public static AbstractXMPPConnection getConnection() {
        if (connection == null) {
            openConnection();
        }
        return connection;
    }

    // 打开连接
    private static void openConnection() {

        XMPPTCPConnectionConfiguration.Builder builder
                = XMPPTCPConnectionConfiguration.builder();

        builder.setHost(host);
        builder.setPort(port);
        builder.setServiceName(serviceName);
        builder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);

        connection = new XMPPTCPConnection(builder.build());

        try {
            connection.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 关闭连接
    public static void release() {
        if (connection != null) {
            connection.disconnect();
            connection = null;
        }
    }
}
