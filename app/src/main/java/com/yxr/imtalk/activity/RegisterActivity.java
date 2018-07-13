package com.yxr.imtalk.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.yxr.imtalk.R;
import com.yxr.imtalk.manager.ConnectionManager;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smackx.iqregister.AccountManager;

public class RegisterActivity extends Activity implements View.OnClickListener{

    private EditText et_user, et_pwd;
    private TextView tv_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
    }

    private void initView() {
        et_user = findViewById(R.id.register_editText_user);
        et_pwd = findViewById(R.id.register_editText_password);

        tv_register = findViewById(R.id.register_textview_enter);
        tv_register.setOnClickListener(this);
    }

    public void register() {
        new RegTask().execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_textview_enter:
                register();
                break;
        }
    }


    /**
     * 注册任务
     */
    class RegTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            String username = et_user.getText().toString();
            String password = et_pwd.getText().toString();

            try {
                AbstractXMPPConnection connection = ConnectionManager.getConnection();

                // 用户管理
                AccountManager manager = AccountManager.getInstance(connection);

                // 创建账户
                manager.createAccount(username, password);

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();

                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finish();
            } else {
                Toast.makeText(RegisterActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

