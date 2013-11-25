package com.zw.app;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by weizhang on 11/24/13.
 */
public class ToFFaragment extends Fragment {
    Question mquestion;
    TextView textView,rightanswer,rightanswershow,tcheckanswer,tnowscore;
    SoundPool soundPool;
    Button button;
    RadioGroup radioGroup;
    int music,musicF;
    String highscore,temp;
    boolean flag=false,flag1=false,flag2=true;
    SharedPreferences sharedPreferences;
    public void setMquestion(Question question){
        mquestion=question;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.toffragment, container, false);
        textView=(TextView)v.findViewById(R.id.question2);
        textView.setText(mquestion.getQuestion());
        button=(Button)v.findViewById(R.id.tbutton);
        tcheckanswer=(TextView)v.findViewById(R.id.tcheckAnswer);
        tnowscore=(TextView)v.findViewById(R.id.tnowscore);
        radioGroup=(RadioGroup)v.findViewById(R.id.tAnswerGroup);
        rightanswer=(TextView)v.findViewById(R.id.rightAnswer);
        rightanswershow=(TextView)v.findViewById(R.id.rightAnswershow);
        getshared();

        initSound();

        button.setOnClickListener(listener);
        radioGroup.setOnCheckedChangeListener(ocl);

        return v;

    }
    public void initSound(){
        soundPool=new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        music = soundPool.load(getActivity(), R.raw.rightanswer, 1);
        musicF =soundPool.load(getActivity(),R.raw.water001,1);

    }

   Button.OnClickListener listener =new Button.OnClickListener(){

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            if(flag1&&flag2){

            if(flag){
                flag2=false;
            soundPool.play(music, 1, 1, 0, 0, 1);
                rightanswer.setText(R.string.rightanswer);
                rightanswershow.setText(mquestion.getAnswer());
                SharedPreferences.Editor editor=sharedPreferences.edit();
                if(Integer.valueOf(highscore)<Integer.valueOf(temp)+1){
                    editor.putString("temp",String.valueOf(Integer.valueOf(temp)+1));
                    editor.putString("highscore",String.valueOf(Integer.valueOf(temp)+1));
                    editor.putBoolean("checked",true);
                    editor.commit();
                    tcheckanswer.setText(R.string.trightanswer);
                    tnowscore.setText(sharedPreferences.getString("temp","0"));
                }else {
                    editor.putString("temp",String.valueOf(Integer.valueOf(temp)+1));
                    editor.putBoolean("checked",true);
                    editor.commit();
                    tcheckanswer.setText(R.string.trightanswer);
                    tnowscore.setText(sharedPreferences.getString("temp","0"));
                }

            }else {
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putBoolean("checked",true);
                editor.commit();
                soundPool.play(musicF,1,1,0,0,1);
                rightanswer.setText(R.string.rightanswer);
                rightanswershow.setText(mquestion.getAnswer());
                tcheckanswer.setText(R.string.twronganswer);
                tnowscore.setText(sharedPreferences.getString("temp","0"));
            }}else {
                Toast.makeText(getActivity().getApplicationContext(), "select an answer,if you already selected and sumbitted click next ", Toast.LENGTH_LONG).show();

            }
        }
   };
    RadioGroup.OnCheckedChangeListener ocl =new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            int radioButtonId = radioGroup.getCheckedRadioButtonId();
            RadioButton radioButton=(RadioButton)getView().findViewById(radioButtonId);
            flag1=true;
            if(radioButton.getText().equals("True")||radioButton.getText().equals("对")){
                flag=false;

            }else {
                flag=true;

            }
        }
    };
    public void getshared(){
        sharedPreferences=getActivity().getSharedPreferences("Mydata",0);
        highscore=sharedPreferences.getString("highscore","0");
        temp=sharedPreferences.getString("temp","0");
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("checked",false);
        editor.commit();
        tnowscore.setText(sharedPreferences.getString("temp","0"));
    }
}