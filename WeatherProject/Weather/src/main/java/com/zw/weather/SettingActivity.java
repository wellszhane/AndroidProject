package com.zw.weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

public class SettingActivity extends Activity {

    CheckBox settingsps;
    EditText settingcity, settingstate,settingzip;
    RadioGroup settingdays,settingtem;
    Button okbtn;
    boolean gpschecked=false,temflag=true;
    SharedPreferences sharedPreferences;
    String days;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        settingsps=(CheckBox)findViewById(R.id.setting_gps);
        settingcity=(EditText)findViewById(R.id.setting_city);
        settingstate=(EditText)findViewById(R.id.setting_state);
        settingzip=(EditText)findViewById(R.id.setting_zip);
        settingdays=(RadioGroup)findViewById(R.id.setting_days);
        settingtem=(RadioGroup)findViewById(R.id.setting_tem);
        okbtn=(Button)findViewById(R.id.setting_okbtn);
        settingsps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    gpsuncheck();
                }else{
                    gpscheck();
                }
            }
        });
        settingdays.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.setting_1day:
                        days="1";
                        break;
                    case R.id.setting_2day:
                        days="2";
                        break;
                    case R.id.setting_3day:
                        days="3";
                        break;
                    default:
                        days="1";
                        break;
                }
            }
        });
        settingtem.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.setting_tems:
                        temflag=true;
                        break;
                    case R.id.setting_temh:
                        temflag=false;
                        break;
                    default:
                        temflag=true;
                        break;
                }
            }
        });
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(gpschecked){
                    if(temflag){
                        sharedPreferences=getSharedPreferences("Mydata",0);
                        SharedPreferences.Editor editor=sharedPreferences.edit();

                        editor.putString("days",days);
                        editor.putBoolean("gps",true);
                        editor.putBoolean("tem",true);
                        editor.commit();
                    }else {
                        sharedPreferences=getSharedPreferences("Mydata",0);
                        SharedPreferences.Editor editor=sharedPreferences.edit();

                        editor.putString("days",days);
                        editor.putBoolean("gps",true);
                        editor.putBoolean("tem",false);
                        editor.commit();
                    }

                }else {
                    String city=settingcity.getText().toString();
                    String state=settingstate.getText().toString();
                    String zip=settingzip.getText().toString();
                    if(city.equals("")&&zip.equals("")){
                        Toast.makeText(getApplicationContext(),"please enter a city or use gps",Toast.LENGTH_LONG).show();
                    }else {
                        if(state.equals("")&&zip.equals("")){
                            Toast.makeText(getApplicationContext(),"please enter a state or use gps",Toast.LENGTH_LONG).show();

                        }else{
                            if(temflag){
                                sharedPreferences=getSharedPreferences("Mydata",0);
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putString("city",city);
                                editor.putString("state",state);
                                editor.putString("zip",zip);
                                editor.putString("days",days);
                                editor.putBoolean("gps",false);
                                editor.putBoolean("tem",true);
                                editor.commit();

                            }else {
                                sharedPreferences=getSharedPreferences("Mydata",0);
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putString("city",city);
                                editor.putString("state",state);
                                editor.putString("zip",zip);
                                editor.putString("days",days);
                                editor.putBoolean("gps",false);
                                editor.putBoolean("tem",false);
                                editor.commit();

                            }

                        }

                    }


                }
            }
        });










    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.action_gobackset:
                Intent intent=new Intent(SettingActivity.this,MainActivity.class);
                startActivityForResult(intent,0);
                break;
            case R.id.action_landset:
                if(getRequestedOrientation()== ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }else if (getRequestedOrientation()==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                }
        }


        return true;
    }

    public void gpscheck(){
        settingcity.setEnabled(true);
        settingstate.setEnabled(true);
        settingzip.setEnabled(true);
        gpschecked=false;

    }

    public void gpsuncheck(){
        settingcity.setEnabled(false);
        settingstate.setEnabled(false);
        settingzip.setEnabled(false);
        gpschecked=true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setting, menu);
        return true;
    }
    
}
