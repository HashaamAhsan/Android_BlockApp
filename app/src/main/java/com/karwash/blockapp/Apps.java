package com.karwash.blockapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;

public class Apps extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    Button submitblock;
    EditText appname;
    TextView errormsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps);
        submitblock=(Button) findViewById(R.id.block_button);
        appname = (EditText) findViewById(R.id.appname);
        errormsg = (TextView) findViewById(R.id.errormsg);
        sharedPreferences = getSharedPreferences("BlockingApp", this.MODE_PRIVATE);
        final Context c = this;
        final Intent service_intent = new Intent(this, BlockService.class);

        submitblock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!appname.getText().toString().equals("")){
                    errormsg.setText("");
                    if (InstalledAppcheck(c,appname.getText().toString())==true){
                        service_intent.putExtra("AppName",appname.getText().toString());
                        //stopService(service_intent);
                        errormsg.setText("Blocked "+appname.getText()+" !");
                        startService(service_intent);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        errormsg.setText("App Not Found!");
                    }

                    //myEdit.putString("name", appname.getText().toString());
                    //System.out.println("hello");
                }
                else{
                    errormsg.setText("Enter App Name First!");
                }
            }
        });

    }

    public boolean InstalledAppcheck(Context context,String s){
        List<ApplicationInfo> packages;
        PackageManager pm;
        pm = getPackageManager();
        //get a list of installed apps.
        packages = pm.getInstalledApplications(0);

        ActivityManager mActivityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);

        for (ApplicationInfo packageInfo : packages) {
            if((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM)==1)continue;
            if(packageInfo.packageName.equals("com.karwash.blockapp")) continue;
            if (packageInfo.packageName.indexOf(s)>0){
                return true;
            }
        }
        return false;
    }
}
