package com.yxr.imtalk.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yxr.imtalk.R;
import com.yxr.imtalk.beans.IMessage;
import com.yxr.imtalk.util.AudioUtils;
import com.yxr.imtalk.util.Constants;

import java.util.List;

public class ChatListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<IMessage> msgList;

    public ChatListAdapter(Context context, List<IMessage> msgList) {
        this.context = context;
        this.msgList = msgList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return msgList.size();
    }

    @Override
    public Object getItem(int position) {
        return msgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final IMessage message = msgList.get(position);
        // 收到的消息
        if (message.getMsgType() == IMessage.MESSAGE_TYPE_IN) {
            convertView = inflater.inflate(R.layout.chat_intput, null);
        } else {
            convertView = inflater.inflate(R.layout.chat_output, null);
        }

        TextView tv_sender = convertView.findViewById(R.id.tv_name);
        tv_sender.setText(message.getSender());

        TextView tv_date = convertView.findViewById(R.id.tv_time);
        tv_date.setText(message.getDate());

        TextView tv_content = convertView.findViewById(R.id.tv_content);
        tv_content.setText(message.getContent());

        // 这是一条语音消息, 注册点击事件监听
        if (message.getMsgModle() == IMessage.MESSAGE_MODEL_AUDIO) {
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 发送成功
                    Log.i("方法执行", "已经走到这边了");
                    if (message.getStatus() == IMessage.MESSAGE_STATUS_SUCCESS) {
                        // 播放声音
                        String path = Constants.AUDIO_DIR.getAbsolutePath() + "/" + message.getFileName();
                        Log.i("正在播放:", path);
                        AudioUtils.play(path, context);
                    }

                }
            });
        }

        return convertView;
    }
}
