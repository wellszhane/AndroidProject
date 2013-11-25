package com.zw.app;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by weizhang on 11/24/13.
 */
public class MutFragment extends Fragment {
    Question mquestion;
    TextView textView,checkanswer,nowscore;
    Bitmap bitmap;
    ImageView imageView;
    SoundPool soundPool;
    Button button;
    int music,musicF;
    RadioGroup radioGroup;
    RadioButton radioButton0,radioButton1,radioButton2,radioButton3;
    boolean flag=false,flag1=false,flag2=true;
    ArrayList<String> strings;
    int count;
    ArrayList<Integer> ints;
    SharedPreferences sharedPreferences;
    String highscore,temp;
    public void setMquestion(Question question){
        mquestion=question;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.mutfragment, container, false);
        textView=(TextView)v.findViewById(R.id.question1);
        textView.setText(mquestion.getQuestion());
        checkanswer=(TextView)v.findViewById(R.id.checkView);
        nowscore=(TextView)v.findViewById(R.id.nowscore);
        imageView=(ImageView)v.findViewById(R.id.imageView);
        button=(Button)v.findViewById(R.id.button);
        getshared();
        initSound();
        init();

        radioGroup=(RadioGroup)v.findViewById(R.id.answerGroup);
        radioButton0=(RadioButton)v.findViewById(R.id.answer0);
        radioButton1=(RadioButton)v.findViewById(R.id.answer1);
        radioButton2=(RadioButton)v.findViewById(R.id.answer2);
        radioButton3=(RadioButton)v.findViewById(R.id.answer3);
        radioButton0.setText(getvalue());
        radioButton1.setText(getvalue());
        radioButton2.setText(getvalue());
        radioButton3.setText(getvalue());
        button.setOnClickListener(listener);
        radioGroup.setOnCheckedChangeListener(ocl);

        if(mquestion.getImageUrl().equals("null")){

        }else {
            ImageLoader imageLoader=new ImageLoader();
            imageLoader.execute(mquestion.getImageUrl());
            try{
                bitmap=imageLoader.get();
                imageView.setImageBitmap(bitmap);
            }catch (Exception e){
                e.printStackTrace();
            }
        }


        return v;

    }
    public void getshared(){
        sharedPreferences=getActivity().getSharedPreferences("Mydata",0);
        highscore=sharedPreferences.getString("highscore","0");
        temp=sharedPreferences.getString("temp","0");
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("checked",false);
        editor.commit();
        nowscore.setText(sharedPreferences.getString("temp","0"));
    }

    public void init(){
        count=4;
        strings=new ArrayList<String>();
        for(int i=0;i<mquestion.incorrectAnswers.size();i++){
            strings.add(mquestion.incorrectAnswers.get(i));
        }
       strings.add(mquestion.getCorrectAnswer());
        ints=new ArrayList<Integer>();
        for(int i=0;i<strings.size();i++){
            ints.add(i);
        }

    }
    public String getvalue(){
        String s=null;
        if(count>0){
            int id=(int)(Math.random()*count);
            s= strings.get(ints.get(id));
            ints.remove(id);
            count--;

        }else {
            Toast.makeText(getActivity().getApplicationContext(), "something wrong try agin", Toast.LENGTH_LONG).show();
           init();
        }



        return s;
    }
    public void initSound(){
        soundPool=new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        music = soundPool.load(getActivity(), R.raw.rightanswer, 1);
        musicF =soundPool.load(getActivity(),R.raw.water001,1);

    }
    class ImageLoader extends AsyncTask<String,Integer,Bitmap>{
        Bitmap bitmap;
        @Override
        protected Bitmap doInBackground(String... params) {
            try{
            URL url = new URL(params[0]);
            URLConnection conn = url.openConnection();

            conn.connect();

            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bitmap = BitmapFactory.decodeStream(bis);
            }
            catch (Exception e){
              e.printStackTrace();
            }
            return bitmap;
        }

    }
    public void bold(){
    if(radioButton0.getText().equals(mquestion.getCorrectAnswer())){
        radioButton0.setTextColor(Color.rgb(0, 155, 55));
    }else if(radioButton1.getText().equals(mquestion.getCorrectAnswer())){
            radioButton1.setTextColor(Color.rgb(0, 155, 55));
        }else if(radioButton2.getText().equals(mquestion.getCorrectAnswer())){
        radioButton2.setTextColor(Color.rgb(0, 155, 55));
    }else if(radioButton3.getText().equals(mquestion.getCorrectAnswer())){
            radioButton3.setTextColor(Color.rgb(0, 155, 55));
        }
    }
    Button.OnClickListener listener =new Button.OnClickListener(){

        @Override
        public void onClick(View arg0) {
            bold();
            // TODO Auto-generated method stub
            if(flag1&&flag2){
                flag2=false;
            if(flag){
            soundPool.play(music, 1, 1, 0, 0, 1);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                if(Integer.valueOf(highscore)<Integer.valueOf(temp)+1){
                    editor.putString("temp",String.valueOf(Integer.valueOf(temp)+1));
                    editor.putString("highscore",String.valueOf(Integer.valueOf(temp)+1));
                    editor.putBoolean("checked",true);
                    editor.commit();
                    checkanswer.setText(R.string.trightanswer);
                    nowscore.setText(sharedPreferences.getString("temp","0"));
                }else {
                    editor.putString("temp",String.valueOf(Integer.valueOf(temp)+1));
                    editor.putBoolean("checked",true);
                    editor.commit();
                    checkanswer.setText(R.string.trightanswer);
                    nowscore.setText(sharedPreferences.getString("temp","0"));
                }

            }else {
                soundPool.play(musicF,1,1,0,0,1);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putBoolean("checked",true);
                editor.commit();
                checkanswer.setText(R.string.twronganswer);
                nowscore.setText(sharedPreferences.getString("temp","0"));

            }
        }else {
                Toast.makeText(getActivity().getApplicationContext(), "select an answer,if you already selected and submited, please click next", Toast.LENGTH_LONG).show();
            }
        }
        };
    RadioGroup.OnCheckedChangeListener ocl =new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            int radioButtonId = radioGroup.getCheckedRadioButtonId();
            RadioButton radioButton=(RadioButton)getView().findViewById(radioButtonId);
            flag1=true;
            if(radioButton.getText().equals(mquestion.getCorrectAnswer())){
                flag=true;
            }else {
                flag=false;
            }
        }
    };

}