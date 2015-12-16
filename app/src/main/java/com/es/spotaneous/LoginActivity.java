package com.es.spotaneous;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.es.spotaneous.fragments.LoginFragment;
import com.es.spotaneous.fragments.SurfaceFragment;
import com.es.spotaneous.fragments.TagsFragment;
import com.es.spotaneous.utils.InternetConnectionReceiver;
import com.es.spotaneous.utils.SharedConfigs;

import java.util.ArrayList;

import me.drakeet.materialdialog.MaterialDialog;


public class LoginActivity extends FragmentActivity {

    ImageView b;
    TextView t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        String cookie =  SharedConfigs.getStringValue("iduser",this);
        if(cookie.equals("")){
            b = (ImageView) findViewById(R.id.next);
            t = (TextView) findViewById(R.id.text);
            t.setVisibility(View.GONE);
            b.setVisibility(View.GONE);

            InternetConnectionReceiver.addInternetConnectionListener(this, new InternetConnectionReceiver.Listener() {
                @Override
                public void onNetworkConnected() {
                    showFragment(new SurfaceFragment());

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            t.setVisibility(View.VISIBLE);
                            b.setVisibility(View.VISIBLE);
                            showFragment(new TagsFragment());
                        }
                    }, 12000);
                }
                @Override
                public void onNetworkDisconnected() {
                    internetStateDialog();
                }
            });
        }else{
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }
    }

    private void internetStateDialog(){
        MaterialDialog mMaterialDialog = new MaterialDialog(this)
                .setTitle("Ligação à internet")
                .setCanceledOnTouchOutside(false)
                .setMessage("Ligue-se à internet para conseguir usufruir da aplicação!");
        mMaterialDialog.show();
    }

    public void showFragment(Fragment f) {
        FragmentTransaction ft = this.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, f);
        ft.commitAllowingStateLoss( );
    }

    public void onTagsDone(View view) {
        ArrayList<String> list = SharedConfigs.getList("listtags", this);
        Log.e("List", list.toString()+"");
        t.setVisibility(View.GONE);
        b.setVisibility(View.GONE);
        showFragment(new LoginFragment());
    }
}
