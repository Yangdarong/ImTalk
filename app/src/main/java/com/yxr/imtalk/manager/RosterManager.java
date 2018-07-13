package com.yxr.imtalk.manager;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.roster.Roster;

/**
 *  联系人列表管理
 */
public class RosterManager {

    private static RosterManager instance;
    private Roster roster;

    private RosterManager() {

    }

    public static RosterManager getInstance(AbstractXMPPConnection connection) {
        instance = new RosterManager();
        instance.roster = Roster.getInstanceFor(connection);

        return instance;
    }

    /**
     *
     * @param jid
     * @param nickname
     * @param group
     */
    public void addEntry(String jid, String nickname, String group) {
        try {
            roster.createEntry(jid, nickname, new String[] {group});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
