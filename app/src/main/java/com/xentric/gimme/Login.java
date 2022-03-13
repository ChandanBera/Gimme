package com.xentric.gimme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.xentric.gimme.beanmodelhelper.UserModel;
import com.xentric.gimme.dbhelper.DBHelper;
import com.xentric.gimme.debughandler.LogCollection;
import com.xentric.gimme.sharedhelper.UserShared;

public class Login extends AppCompatActivity
implements View.OnClickListener{

    private EditText tv_username,tv_password;
    private Button btn_login;
    private TextView tv_forgot_password;
    private LogCollection logCollection;
    private DBHelper db;
    private String username,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tv_username = (EditText) findViewById(R.id.tv_username);
        tv_password = (EditText) findViewById(R.id.tv_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        tv_forgot_password = (TextView) findViewById(R.id.tv_forgot_password);
        logCollection = new LogCollection(Login.this);
        db = new DBHelper(Login.this);

        //Check loged in
        if (new UserShared(Login.this).isLoggedin()){
            Intent intent = new Intent(Login.this,Dashboard.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        }

        btn_login.setOnClickListener(this);
        tv_forgot_password.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                //LOGIN Attempt
                username = tv_username.getText().toString();
                password = tv_password.getText().toString();
                UserModel user = db.getUser(username, password);
                if (user!=null){
                    new UserShared(Login.this).setLogin(
                            String.valueOf(user.getId()),
                            user.getUsername(),
                            user.getPassword(),
                            user.getEmail(),
                            user.getFname(),
                            user.getLname(),
                            user.getPhonenumber());
                    Intent intent = new Intent(Login.this,Dashboard.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
                }else {
                    logCollection.showToastShort("Username or Password not found");
                }
                break;
            case R.id.tv_forgot_password:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
    }
}
