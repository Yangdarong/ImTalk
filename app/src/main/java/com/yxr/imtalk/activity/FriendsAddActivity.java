package com.yxr.imtalk.activity;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.yxr.imtalk.R;
import com.yxr.imtalk.manager.ConnectionManager;
import com.yxr.imtalk.manager.RosterManager;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jxmpp.util.XmppStringUtils;

import java.util.ArrayList;
import java.util.List;

public class FriendsAddActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private LinearLayout addfriend_layout_back;
    private EditText et_search_friends;
    private Button btn_search;

    private ListView listView1;

    private ArrayAdapter<String> adapter;

    private AbstractXMPPConnection connection = ConnectionManager.getConnection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_add);

        initView();
    }

    private void initView() {
        et_search_friends = findViewById(R.id.et_search_friends);
        addfriend_layout_back = findViewById(R.id.addfriend_layout_back);
        btn_search = findViewById(R.id.btn_search);
        listView1 = findViewById(R.id.listView1);
        // 点击搜索出来的好友发送请求
        listView1.setOnItemClickListener(this);

        btn_search.setOnClickListener(this);
        addfriend_layout_back.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addfriend_layout_back: {
                // 返回上一级页面
                finish();
                break;
            }

            case R.id.btn_search: {
                // 按下查询
                search();
                break;
            }
        }
    }

    /**
     *  按关键字模糊查询
     */
    public void search() {
        new SearchTask().execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //点击搜索出来的联系人， 发送添加好友请求
        String name = adapter.getItem(position);

        // 转化为JID (king@域名)
        final String addToJId = XmppStringUtils.completeJidFrom(name, connection.getServiceName());

        // 弹出提示对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("添加好友");
        builder.setMessage("确定添加 " + name + " 为好友吗?");

        // 确定 取消按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                RosterManager rosterManager = RosterManager.getInstance(connection);
                String nickname = XmppStringUtils.parseLocalpart(addToJId);

                // 添加好友到“Friends” 分组
                rosterManager.addEntry(addToJId, nickname, "Friends");
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    class SearchTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {

            // 获取kw
            String searchText = et_search_friends.getText().toString();

            // 获取搜索管理器
            UserSearchManager searchManager = new UserSearchManager(connection);

            // 构建查询表单
            String searchService = "search." + connection.getServiceName();
            try {
                Form searchForm = searchManager.getSearchForm(searchService);

                // 构建结果表单
                Form answerFrom = searchForm.createAnswerForm();

                // 根据用户名进行检索
                answerFrom.setAnswer("Username", true);

                // 指定搜索的关键字
                answerFrom.setAnswer("search", searchText.trim());

                // 获取查询结果
                ReportedData result = searchManager.getSearchResults(answerFrom, searchService);

                List<ReportedData.Row> rows = result.getRows();
                List<String> list = new ArrayList<>();
                for (ReportedData.Row row : rows) {
                    String username = row.getValues("Username").get(0);
                    list.add(username);
                }

                // 展示查询结果
                adapter = new ArrayAdapter<>(FriendsAddActivity.this, R.layout.friend_message_item, R.id.textView_name, list);
                //Toast.makeText(this, "查询到了" + list.size(), Toast.LENGTH_SHORT).show();
                //listView1.setAdapter(adapter);

            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
                return false;
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
                return false;
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                listView1.setAdapter(adapter);
                Toast.makeText(FriendsAddActivity.this, "找到以下用户", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(FriendsAddActivity.this, "用户未找到", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
