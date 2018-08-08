package com.luoye.phoneinfo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.luoye.phoneinfo.activity.InfoActivity;

import java.util.ArrayList;

import rebus.permissionutils.AskAgainCallback;
import rebus.permissionutils.FullCallback;
import rebus.permissionutils.PermissionEnum;
import rebus.permissionutils.PermissionManager;

/**
 * Created by zyw on 18-8-7.
 */

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(1024,1024);
        setContentView(R.layout.activity_main);

        init();

    }

    private  void init(){
        PermissionManager.Builder()
                .permission(PermissionEnum.READ_PHONE_STATE,PermissionEnum.ACCESS_FINE_LOCATION,PermissionEnum.ACCESS_COARSE_LOCATION,PermissionEnum.WRITE_EXTERNAL_STORAGE)
                .askAgain(true)
                .askAgainCallback(new AskAgainCallback() {
                    @Override
                    public void showRequestPermission(UserResponse response) {

                    }
                })
                .callback(new FullCallback() {
                    @Override
                    public void result(ArrayList<PermissionEnum> permissionsGranted, ArrayList<PermissionEnum> permissionsDenied, ArrayList<PermissionEnum> permissionsDeniedForever, ArrayList<PermissionEnum> permissionsAsked) {

                        Handler handler=new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                Intent intent=new Intent(MainActivity.this, InfoActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        },3000);
                    }
                })
                .ask(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handleResult(this, requestCode, permissions, grantResults);
    }


}

