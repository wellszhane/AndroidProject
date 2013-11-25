package com.zw.app;


import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;



public class MainActivity extends ActionBarActivity {

  String url="https://dl.dropboxusercontent.com/u/8606210/trivia.json";
  TextView textView;
  Button button;
  SharedPreferences sharedPreferences;
  String highscore;
  String temp="0";
  int count=4;
  ArrayList<Question> questions=new ArrayList<Question>();
  boolean flag=true;
  ArrayList<Integer> ints;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new basefragment())
                    .commit();
        }


        getsharedPreferences();
        textView=(TextView)findViewById(R.id.highscore);
        textView.setText(highscore);
        button=(Button)findViewById(R.id.button);
        button.setText(R.string.play);
        button.setOnClickListener(butn);



    }
    @Override

    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
    }
    public void getsharedPreferences(){
        sharedPreferences=getSharedPreferences("Mydata",0);
        highscore=sharedPreferences.getString("highscore","0");
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("temp","0");
        editor.putBoolean("checked",true);
        editor.commit();
    }
    public void updatehighscore(){
        temp=sharedPreferences.getString("highscore","0");
        if(Integer.valueOf(highscore)<Integer.valueOf(temp)){
            textView.setText(temp);
        }
    }
    public void setInts(){
        ints=new ArrayList<Integer>();
        for (int i = 0; i <= 3; i++) {
            ints.add(i);
        }
    }

    Button.OnClickListener butn=new Button.OnClickListener(){
        boolean aBoolean;
        @Override
        public void onClick(View view) {
            aBoolean=sharedPreferences.getBoolean("checked",true);
            if(flag){
            android.app.Fragment f1;
            f1=new startfragment();
            FragmentTransaction tran = getFragmentManager().beginTransaction();
            tran.replace(R.id.container,f1);
            tran.commit();
            flag=false;
            button.setText(R.string.start);
            setInts();
            }else {
                if(aBoolean){
                button.setText(R.string.next);
                if(count>0){
                int id=(int)(Math.random()*count);
                Question usequestion=questions.get(ints.get(id));
                ints.remove(id);
                    count--;

                if(usequestion.getQuestionType().equals("multipleChoice")){
                    MutFragment f1=new MutFragment();
                    f1.setMquestion(usequestion);
                    FragmentTransaction tran = getFragmentManager().beginTransaction();
                    tran.replace(R.id.container,f1);
                    tran.commit();
                    updatehighscore();
                }else {
                    ToFFaragment f1=new ToFFaragment();
                    f1.setMquestion(usequestion);
                    FragmentTransaction tran = getFragmentManager().beginTransaction();
                    tran.replace(R.id.container,f1);
                    tran.commit();
                    updatehighscore();
                }}else {
                    button.setText(R.string.startagin);
                    setInts();
                    count=4;
                    resultfragment f1=new resultfragment();
                    FragmentTransaction tran = getFragmentManager().beginTransaction();
                    tran.replace(R.id.container,f1);
                    tran.commit();
                    updatehighscore();

                }
            }else {
                    Toast.makeText(getApplicationContext(), "please select a answer and submit", Toast.LENGTH_LONG).show();
                }
            }

        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */



   public   class startfragment extends android.app.Fragment{
        TextView textView;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v=inflater.inflate(R.layout.fragment_main, container, false);
            textView=(TextView)v.findViewById(R.id.startlogn);
            textView.setText(R.string.startlogn);
            PageTask a =new PageTask(getActivity());
            a.execute(url);

            return v;
        }
        class PageTask extends AsyncTask<String, Integer, String> {
            ProgressDialog pdialog;
            public PageTask(Context context){
                pdialog = new ProgressDialog(context, 0);
                pdialog.setButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                });
                pdialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                });
                pdialog.setCancelable(true);
                pdialog.setMax(100);
                pdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pdialog.show();


            }

            // doinbackgroud here get the return of webpage
            @Override
            protected String doInBackground(String... params) {

                try{

                    HttpClient client = new DefaultHttpClient();
                    HttpGet get = new HttpGet(params[0]);
                    HttpResponse response = client.execute(get);
                    HttpEntity entity = response.getEntity();
                    long length = entity.getContentLength();
                    InputStream is = entity.getContent();
                    String s = null;

                    if(is != null) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] buf = new byte[128];
                        int ch = -1;
                        int count = 0;
                        while((ch = is.read(buf)) != -1) {
                            baos.write(buf, 0, ch);
                            count += ch;
                            if(length > 0) {
                                // get the length and the update the process
                                publishProgress((int) ((count / (float) length) * 100));
                            }

                            // sleep the thread 100ms
                            Thread.sleep(100);
                        }
                        s = new String(baos.toByteArray());
                    }else {
                        Toast.makeText(getApplicationContext(), "something wrong", Toast.LENGTH_LONG).show();
                    }

                    return s;
                } catch(Exception e) {

                    e.printStackTrace();

                }

                return null;

            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
            }

            // choose what to present  accroding the sharedpreference..
            @Override
            protected void onPostExecute(String result) {
                pdialog.dismiss();
                try {
                    String f="done";
                    JSONArray jsonObjs = new JSONObject(result).getJSONArray("questions");
                    Log.v("aaaaaaaaaaaaa",f);
                    for(int i = 0; i < jsonObjs.length(); i++){
                        Question question=new Question();
                        JSONObject jsonObj = ((JSONObject)jsonObjs.opt(i));
                        question.setQuestion(jsonObj.getString("question"));
                        Log.d("bbbb", question.getQuestion());
                        question.setQuestionType(jsonObj.getString("questionType"));
                        Log.d("ccc", question.getQuestionType());
                        question.setImageUrl(jsonObj.getString("imageUrl"));
                        Log.d("ddd",question.getImageUrl());
                        question.setCorrectAnswer(jsonObj.getString("correctAnswer"));
                        Log.d("eee",question.getCorrectAnswer());
                        if(question.getQuestionType().equals("multipleChoice")){

                            JSONArray incorrectAnswers=jsonObj.getJSONArray("incorrectAnswers");
                            for(int j=0;j<incorrectAnswers.length();j++){
                                question.incorrectAnswers.add(incorrectAnswers.getString(j));

                            }

                        }else {
                            question.setAnswer(jsonObj.getString("answer"));
                            Log.d("ffff",question.getAnswer());
                        }

                        questions.add(question);


                    }


                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"invaild enter",Toast.LENGTH_LONG).show();
                    Log.e("aaaaaaaaaaaaa",e.getMessage());

                    e.printStackTrace();
                }



            }

            @Override
            protected void onPreExecute() {

            }

            @Override
            protected void onProgressUpdate(Integer... values) {

                pdialog.setProgress(values[0]);
            }

        }


   }



   public class basefragment extends android.app.Fragment{
       TextView textView;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v=inflater.inflate(R.layout.fragment_main, container, false);
             textView=(TextView)v.findViewById(R.id.startlogn);
             textView.setText(R.string.playlogn);

            return v;
        }

    }
    public class resultfragment extends android.app.Fragment{
        TextView textView;
        String htemp=null;
        SharedPreferences sharedPreferences;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v=inflater.inflate(R.layout.resultlayout, container, false);
            textView=(TextView)v.findViewById(R.id.textViewresult);
            sharedPreferences=getActivity().getSharedPreferences("Mydata",0);
            highscore=sharedPreferences.getString("highscore","0");
            htemp=sharedPreferences.getString("temp","0");
            textView.setText(htemp);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString("temp","0");
            editor.commit();

            return v;
        }

    }




}
