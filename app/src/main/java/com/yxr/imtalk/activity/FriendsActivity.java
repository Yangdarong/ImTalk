package com.yxr.imtalk.activity;

import android.content.DialogInterface;
import android.content.Intent;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yxr.imtalk.R;
import com.yxr.imtalk.adapter.FriendsExpandableListAdapter;
import com.yxr.imtalk.beans.FriendInfo;
import com.yxr.imtalk.manager.ConnectionManager;
import com.yxr.imtalk.manager.RosterManager;
import com.yxr.imtalk.manager.SubscribeManager;
import com.yxr.imtalk.window.ConfirmPopWindow;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jxmpp.util.XmppStringUtils;

import java.util.Collection;

public class FriendsActivity extends AppCompatActivity implements View.OnClickListener, RosterListener, ExpandableListView.OnChildClickListener {

    private AbstractXMPPConnection connection;
    private static final int MENU_ADD_FRIRND = 1;

    private LinearLayout friend_layout_back, add_friend_layout_enter;

    private static final int MESSAGE_ALERT_REQUEST_FRIEND = 1;
    private static final int MESSAGE_RELOAD_FRIENDS = 2;

    private FriendsExpandableListAdapter adapter;

    private ExpandableListView elv_friends;

    private RosterManager rosterManager;

    private Roster roster;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MENU_ADD_FRIRND:
                    alertInvestDialog(msg.obj.toString());
                    break;

                case MESSAGE_RELOAD_FRIENDS:
                    adapter.setData(roster.getGroups());
                    adapter.notifyDataSetChanged();
                    break;

                    default:
                        break;
            }
        }
    };

    /**
     * 弹出添加好友提示的对话框
     *
     * @param inverstorJid
     */
    protected void alertInvestDialog(final String inverstorJid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("好友申请");
        builder.setMessage("【" + inverstorJid + "】向你发来好友申请, 是否添加对方为好友?");

        builder.setPositiveButton("接受", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 添加到我的好友
                String nickname = XmppStringUtils.parseLocalpart(inverstorJid);
                rosterManager.addEntry(inverstorJid, nickname, "Friends");

                SubscribeManager.subscribed(connection);
            }
        });

        builder.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 拒绝
                SubscribeManager.unsubscribe(connection);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        connection = ConnectionManager.getConnection();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        TextView tv_name = findViewById(R.id.tv_name);

        String loggedUser = connection.getUser();

        tv_name.setText(XmppStringUtils.parseBareJid(loggedUser));

        // 获取联系人列表
        roster = Roster.getInstanceFor(connection);

        // 将联系人列表展示
        elv_friends = findViewById(R.id.list_view_friends);

        adapter = new FriendsExpandableListAdapter(this, roster.getGroups());

        elv_friends.setAdapter(adapter);
        elv_friends.setOnChildClickListener(this);
        // 初始化布局
        initView();

        rosterManager = RosterManager.getInstance(connection);

        // 监听服务器转发的消息 (请求加好友的信息)
        connection.addAsyncStanzaListener(new StanzaListener() {

            @Override
            public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
                if (packet instanceof Presence) {
                    Presence presence = (Presence) packet;
                    String inverstorJId = packet.getFrom();

                    // 获取请求者
                    if (presence.getType() == Presence.Type.subscribe) {

                        // 如果已经添加好友,就不用提示添加消息
                        RosterEntry entry = roster.getEntry(inverstorJId);

                        if (entry == null) {
                            // 收到了添加好友的提示框
                            Message message = handler.obtainMessage(MESSAGE_ALERT_REQUEST_FRIEND, inverstorJId);

                            // 弹出提示框
                            handler.sendMessage(message);
                        }
                    }
                }
            }
        }, new StanzaFilter() {
            @Override
            public boolean accept(Stanza stanza) {
                // 接受所有请求
                return true;
            }
        });
    }

    private void initView() {
        friend_layout_back = findViewById(R.id.friend_layout_back);
        add_friend_layout_enter = findViewById(R.id.add_friend_layout_enter);

        friend_layout_back.setOnClickListener(this);
        add_friend_layout_enter.setOnClickListener(this);
        roster.addRosterListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.friend_layout_back: {
                // 用户登出
                try {
                    ConnectionManager.release();
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }

            case R.id.add_friend_layout_enter: {
                // 跳转到 添加好友的页面
                /*Intent intent = new Intent(FriendsActivity.this, FriendsAddActivity.class);
                startActivity(intent);*/
                new ConfirmPopWindow(FriendsActivity.this).showAtBottom(add_friend_layout_enter);
                break;
            }
        }
    }

    // 联系人已经添加
    @Override
    public void entriesAdded(Collection<String> addresses) {
        // 刷新好友列表
        handler.sendEmptyMessage(MESSAGE_RELOAD_FRIENDS);
    }

    @Override
    public void entriesUpdated(Collection<String> addresses) {
        // 刷新好友列表
        handler.sendEmptyMessage(MESSAGE_RELOAD_FRIENDS);
    }

    @Override
    public void entriesDeleted(Collection<String> addresses) {
        // 刷新好友列表
        handler.sendEmptyMessage(MESSAGE_RELOAD_FRIENDS);
    }

    @Override
    public void presenceChanged(Presence presence) {
        // 刷新好友列表
        handler.sendEmptyMessage(MESSAGE_RELOAD_FRIENDS);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
        // 获取被选中好友
        FriendInfo info = (FriendInfo) adapter.getChild(groupPosition, childPosition);

        String chattoJid = info.getJid();

        // 跳转到聊天界面
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("chattoJid", chattoJid);
        startActivity(intent);
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}
