package com.yxr.imtalk.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.yxr.imtalk.R;
import com.yxr.imtalk.adapter.ChatListAdapter;
import com.yxr.imtalk.beans.IMessage;
import com.yxr.imtalk.manager.ConnectionManager;
import com.yxr.imtalk.util.Constants;
import com.yxr.imtalk.util.TimeUtils;
import com.yxr.imtalk.views.RecordImageView;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jxmpp.util.XmppStringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener,ChatMessageListener, ChatManagerListener, RecordImageView.OnRecordFinishedListener, FileTransferListener {

    private AbstractXMPPConnection connection = ConnectionManager.getConnection();

    private EditText editEmojicon;

    private TextView btn_send;

    private TextView tv_title;

    private RecordImageView btn_record;

    private Chat chat;

    private ChatListAdapter adapter;

    private String loggedUser;

    private String chattoJid;

    private List<IMessage> msgList = new ArrayList<>();

    private FileTransferManager fileTransferManager;

    public static final int MESSAGE_REFRESEN_CHAT_LIST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();

        Intent intent = getIntent();

        // 当前的聊天对象
        chattoJid = intent.getStringExtra("chattoJid");
        tv_title.setText(chattoJid);

        // 当前登录对象
        loggedUser = XmppStringUtils.parseBareJid(connection.getUser());

        ListView msg_listView = findViewById(R.id.msg_listView);
        adapter = new ChatListAdapter(this, msgList);
        msg_listView.setAdapter(adapter);

        // 聊天管理器
        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        // 监听对话的创建(顺序提前)
        chatManager.addChatListener(this);

        // 创建一个对话
        chat = chatManager.createChat(chattoJid, this);

        // 文件传输管理器
        fileTransferManager = FileTransferManager.getInstanceFor(connection);
        // 注册监听, 接收文件
        fileTransferManager.addFileTransferListener(this);
    }

    private void initView() {
        editEmojicon = findViewById(R.id.editEmojicon);
        tv_title = findViewById(R.id.tv_title);
        btn_send = findViewById(R.id.btn_send);
        btn_record = findViewById(R.id.iv_icon);

        btn_send.setOnClickListener(this);
        btn_record.setOnRecordFinishedListener(this);
    }

    public void closeActiviy(View layout) {
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                mSend();
                break;

                default:
                    break;
        }
    }

    /**
     * 发送消息
     */
    public void mSend() {
        String msg = editEmojicon.getText().toString();
        // 用户可以马上看到自己发送的消息
        IMessage localMsg = new IMessage(loggedUser, msg, TimeUtils.getNow(), IMessage.MESSAGE_TYPE_OUT);
        // 添加到消息列表, 更新ListView
        msgList.add(localMsg);
        adapter.notifyDataSetChanged();

        // 这条消息对于发送到的用户来说是接受的消息
        IMessage remoteMsg = new IMessage(loggedUser, msg, TimeUtils.getNow(), IMessage.MESSAGE_TYPE_IN);

        try {
            chat.sendMessage(remoteMsg.toJson());
        } catch (Exception e) {
             e.printStackTrace();
        }

        // 发送完成后清空输入框
        editEmojicon.setText("");
    }

    // 录音完成,发送音频文件
    @Override
    public void onFinished(File audioFile, int duration) {
        if (audioFile == null)
            return;

        // localMsg
        IMessage localMsg = new IMessage(loggedUser, TimeUtils.getNow(), IMessage.MESSAGE_TYPE_OUT, duration, audioFile.getName());
        msgList.add(localMsg);
        adapter.notifyDataSetChanged();

        // remoteMsg
        IMessage remoteMsg = new IMessage(loggedUser, TimeUtils.getNow(), IMessage.MESSAGE_TYPE_IN, duration, audioFile.getName());

        try {
            chat.sendMessage(remoteMsg.toJson());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 发送文件
        OutgoingFileTransfer transfer = fileTransferManager.createOutgoingFileTransfer(chattoJid+"/Smack");
        try {
            transfer.sendFile(audioFile, "send_audio");

            /*if (transfer.getStatus() == FileTransfer.Status.complete) {
                Log.i("发送的状态是", transfer.getStatus().toString());
                updateMsgStatus(audioFile);
            } else {
                Log.i("发送的状态是", transfer.getStatus().toString());
            }*/
            updateMsgStatus(audioFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void fileTransferRequest(FileTransferRequest request) {

        // 收到文件之后，存入本地
        IncomingFileTransfer transfer = request.accept();
        if (!Constants.AUDIO_DIR.exists()) {
            Constants.AUDIO_DIR.mkdirs();
        }

        File file = new File(Constants.AUDIO_DIR, transfer.getFileName());
/*        Log.i("该文件的大小是",  file.length()+ "B");
        Log.i("transfer传入的文件名字是", transfer.getFileName());*/

        try {
            // 收文件
            transfer.recieveFile(file);
            /*Log.i("收到的语音消息存放在", file.getAbsolutePath());
            // 传输成功
            if (transfer.getStatus() == FileTransfer.Status.complete) {
                Log.i("接收的状态是", transfer.getStatus().toString());
                updateMsgStatus(file);
            } else {
                Log.i("接收的状态是", transfer.getStatus().toString());
            }*/
            Log.i("收到本地", file.getAbsolutePath());
            Log.i("收到的文件大小是", file.length()+ "B");
            updateMsgStatus(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateMsgStatus(File file) {

        // 更新消息的状态
        for (IMessage msg : msgList) {
            if (file.getName().equals(msg.getFileName())) {
                msg.setStatus(IMessage.MESSAGE_STATUS_SUCCESS);
                break;
            }
        }
    }

    @Override
    public void chatCreated(Chat chat, boolean createdLocally) {
        // 添加接受消息的监听
        chat.addMessageListener(this);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MESSAGE_REFRESEN_CHAT_LIST:
                    adapter.notifyDataSetChanged();
                    break;

                    default:
                        break;
            }
            super.handleMessage(msg);
        }
    };

    // 收到好友发送的消息
    @Override
    public void processMessage(Chat chat, Message message) {
        // 当前聊天窗口要显示当前这个用户发送的消息
        String fromJid = XmppStringUtils.parseBareJid(message.getFrom());
        if(fromJid.equals(chattoJid)) {
            String json = message.getBody();
            msgList.add(IMessage.fromJson(json));

            //刷新
            handler.sendEmptyMessage(MESSAGE_REFRESEN_CHAT_LIST);
        }

    }

}
