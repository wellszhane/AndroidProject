package com.zw.weather;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;



public class MainActivity extends Activity {
    Button btn;
    TextView showgps,message ;
    Boolean checkgps;
    Boolean temstyle;
    String daysstyle;
    String zipcode;
    String city;
    String state;
    Double  latitude;
    Double longitude;
    String TAG="main activity";
    String URL="http://api.wunderground.com/api/5b907dd061c30668/forecast/q/",useurl;
    String mainzip,mainstate,maincity;
    String imageurl[];


    TextView main_cityt,main_states,main_zipz,showresultv;

    SharedPreferences sharedPreferences;
    public static String filename="Mydata";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getpreference();
        message=(TextView)findViewById(R.id.textView);
        showresultv=(TextView)findViewById(R.id.showresult);
        main_cityt=(TextView)findViewById(R.id.main_city);
        main_states=(TextView)findViewById(R.id.main_state);
        main_zipz=(TextView)findViewById(R.id.main_zip);
        //start here

        if(checkgps){
            openGPSSettings();
            getLocation();
            StringBuilder a=new StringBuilder();
            useurl= a.append(URL).append(latitude+","+longitude+".json").toString();

            connect();
            Toast.makeText(getApplicationContext(),"Default Uing GPS,U can change in setting",Toast.LENGTH_LONG).show();



        }else {
                     if(city==null||state==null){
                         StringBuilder a=new StringBuilder();
                         useurl= a.append(URL).append(zipcode + ".json").toString();
                         connect();
                         main_zipz.setText(zipcode);

                     }else {
                         StringBuilder a=new StringBuilder();
                         useurl= a.append(URL).append(state+"/"+city+".json").toString();
                         connect();
                         main_cityt.setText(city);
                         main_states.setText(state);
                     }

                   }


        main_states.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                main_zipz.setEnabled(false);
                return false;
            }
        });

        main_cityt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                main_zipz.setEnabled(false);
                return false;
            }
        });
        main_zipz.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                main_states.setEnabled(false);
                main_cityt.setEnabled(false);
                return false;
            }
        });


        btn=(Button)findViewById(R.id.test);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maincity=main_cityt.getText().toString();
                mainstate=main_states.getText().toString();
                mainzip=main_zipz.getText().toString();
                if(mainzip.equals("")){
                    if(mainstate.equals("")||maincity.equals("")){
                        Toast.makeText(getApplicationContext(),"Please enter city or va",Toast.LENGTH_LONG).show();
                    }else {
                        StringBuilder a=new StringBuilder();
                        useurl= a.append(URL).append(mainstate+"/"+maincity+".json").toString();
                        connect();
                    }

                }else {
                    StringBuilder a=new StringBuilder();
                    useurl= a.append(URL).append(mainzip + ".json").toString();
                    connect();
                }
            }
        });





    }

    public void connect(){

        PageTask a=new PageTask(this);
        //a.execute("http://api.wunderground.com/api/5b907dd061c30668/forecast/q/VA/Vienna.json");
        a.execute(useurl);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.action_settings:
                Intent intent=new Intent(MainActivity.this,SettingActivity.class);
                startActivityForResult(intent,0);
            break;
            case R.id.action_land:
                if(getRequestedOrientation()== ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }else if (getRequestedOrientation()==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                }
        }


        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }



     //get sharedpreferece  value
    public void getpreference(){
        sharedPreferences=getSharedPreferences(filename,0);

        checkgps=sharedPreferences.getBoolean("gps",false);
        Log.e("can","can not ");
        temstyle=sharedPreferences.getBoolean("tem",true);
        Log.e("can","can not ");
        daysstyle=sharedPreferences.getString("days","1");
        Log.e("can","can not ");
        zipcode=sharedPreferences.getString("zip","22180");
        city=sharedPreferences.getString("city","Vienna");
        state=sharedPreferences.getString("state","VA");
        Log.e("can","can not ");


    }

     // check the state of the gps setting
    private void openGPSSettings() {
        LocationManager alm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Get Location", Toast.LENGTH_SHORT)
                    .show();
            Log.i(TAG,"the data is: ");
            return;

        }

        Toast.makeText(this, "please open the GPS", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
        startActivityForResult(intent,0);

    }
    // get the location use location manager here!!!!!!
    private void getLocation()
    {
        LocationManager locationManager;
        String serviceName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) this.getSystemService(serviceName);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        String provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(provider);
        updateToNewLocation(location);
        locationManager.requestLocationUpdates(provider, 1 * 1000, 500,new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        });
    }
     //update the location get the location address
    private void updateToNewLocation(Location location) {

        showgps=(TextView)findViewById(R.id.currentgps);
        if (location != null) {
            latitude = location.getLatitude();
            longitude= location.getLongitude();
            showgps.setText(latitude + "\t" + longitude);
        } else {
            Toast.makeText(getApplicationContext(),"Can not get the location",Toast.LENGTH_LONG).show();
        }

    }






     //send the http request and get the return using asynctask here!!!!
    class PageTask extends AsyncTask<String, Integer, String> {
        ProgressDialog pdialog;
        public PageTask(Context context){
            pdialog = new ProgressDialog(context, 0);
            //?????? why the fuction deleted???
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
                   Toast.makeText(getApplicationContext(),"Make sure the enter is correct",Toast.LENGTH_LONG).show();
                }
                Log.d("Wei zhang test URL",s);
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
            String s=parseJsonMulti(result,daysstyle,temstyle);
            showresultv.setText(s);

            pdialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            // start show
            message.setText(R.string.downloading);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // update and ??? how to make the process look in %
            message.setText(""+values[0]+"%");
            pdialog.setProgress(values[0]);
        }

    }

    // prase the data from website
    private String parseJsonMulti(String strResult,String a,Boolean b) {
        Log.e("HHHHhhhhhlkadsjfl;akjl;dkfj;jkkl","Hrere hehhhhdshhfdshhdfshdsaf");
        String f = "";
        if(a.equals("1")){
            if(b){
                try {

                    JSONArray jsonObjs = new JSONObject(strResult).getJSONObject("forecast").getJSONObject("simpleforecast").getJSONArray("forecastday");
                    Log.e("aaaaaaaaaaaaa",f);
                    for(int i = 0; i < jsonObjs.length()-2 ; i++){
                        JSONObject jsonObj = ((JSONObject)jsonObjs.opt(i));
                        JSONObject jsonObjecthigh= ((JSONObject)jsonObjs.opt(i)).getJSONObject("high");
                        JSONObject jsonObjectlow = ((JSONObject)jsonObjs.opt(i)).getJSONObject("low");
                        String condition = jsonObj.getString("conditions");
                        String temperaturehs=jsonObjecthigh.getString("celsius");

                        String temperaturels=jsonObjectlow.getString("celsius");

                        f +=   "conddtion: "+condition+"High Temperature "+temperaturehs+"Low Temperature "+temperaturels+"\n" ;
                        Log.e("aaaaaaaaaaaaa",f);

                    }


                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"invaild enter",Toast.LENGTH_LONG).show();
                    Log.e("aaaaaaaaaaaaa",e.getMessage());

                    e.printStackTrace();
                }



            }else {
                try {

                    JSONArray jsonObjs = new JSONObject(strResult).getJSONObject("forecast").getJSONObject("simpleforecast").getJSONArray("forecastday");
                    Log.e("aaaaaaaaaaaaa",f);
                    for(int i = 0; i < jsonObjs.length()-2 ; i++){
                        JSONObject jsonObj = ((JSONObject)jsonObjs.opt(i));
                        JSONObject jsonObjecthigh= ((JSONObject)jsonObjs.opt(i)).getJSONObject("high");
                        JSONObject jsonObjectlow = ((JSONObject)jsonObjs.opt(i)).getJSONObject("low");
                        String condition = jsonObj.getString("conditions");
                        String temperaturehh=jsonObjecthigh.getString("fahrenheit");

                        String temperaturelh=jsonObjectlow.getString("fahrenheit");


                        f +=   "conddtion: "+condition+"High T "+temperaturehh+"Low T "+temperaturelh+"\n" ;
                        Log.e("aaaaaaaaaaaaa",f);

                    }


                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"invaild enter",Toast.LENGTH_LONG).show();
                    Log.e("aaaaaaaaaaaaa",e.getMessage());


                    e.printStackTrace();
                }

            }

        }else if(a.equals("2")){
            if(b){
                try {

                    JSONArray jsonObjs = new JSONObject(strResult).getJSONObject("forecast").getJSONObject("simpleforecast").getJSONArray("forecastday");
                    Log.e("aaaaaaaaaaaaa",f);
                    for(int i = 0; i < jsonObjs.length()-1 ; i++){
                        JSONObject jsonObj = ((JSONObject)jsonObjs.opt(i));
                        JSONObject jsonObjecthigh= ((JSONObject)jsonObjs.opt(i)).getJSONObject("high");
                        JSONObject jsonObjectlow = ((JSONObject)jsonObjs.opt(i)).getJSONObject("low");
                        String condition = jsonObj.getString("conditions");
                        String temperaturehs=jsonObjecthigh.getString("celsius");

                        String temperaturels=jsonObjectlow.getString("celsius");

                        f +=   "conddtion: "+condition+"High Temperature "+temperaturehs+"Low Temperature "+temperaturels+"\n" ;
                        Log.e("aaaaaaaaaaaaa",f);

                    }


                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"invaild enter",Toast.LENGTH_LONG).show();
                    Log.e("aaaaaaaaaaaaa",e.getMessage());

                    e.printStackTrace();
                }

            }else {
                try {

                    JSONArray jsonObjs = new JSONObject(strResult).getJSONObject("forecast").getJSONObject("simpleforecast").getJSONArray("forecastday");
                    Log.e("aaaaaaaaaaaaa",f);
                    for(int i = 0; i < jsonObjs.length()-1 ; i++){
                        JSONObject jsonObj = ((JSONObject)jsonObjs.opt(i));
                        JSONObject jsonObjecthigh= ((JSONObject)jsonObjs.opt(i)).getJSONObject("high");
                        JSONObject jsonObjectlow = ((JSONObject)jsonObjs.opt(i)).getJSONObject("low");
                        String condition = jsonObj.getString("conditions");
                        String temperaturehh=jsonObjecthigh.getString("fahrenheit");

                        String temperaturelh=jsonObjectlow.getString("fahrenheit");


                        f +=   "conddtion: "+condition+"High T "+temperaturehh+"Low T "+temperaturelh+"\n" ;
                        Log.e("aaaaaaaaaaaaa",f);

                    }


                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"invaild enter",Toast.LENGTH_LONG).show();
                    Log.e("aaaaaaaaaaaaa",e.getMessage());

                    e.printStackTrace();
                }

            }

        }else if(a.equals("3")){
            if(b){
                try {

                    JSONArray jsonObjs = new JSONObject(strResult).getJSONObject("forecast").getJSONObject("simpleforecast").getJSONArray("forecastday");
                    Log.e("aaaaaaaaaaaaa",f);
                    for(int i = 0; i < jsonObjs.length() ; i++){
                        JSONObject jsonObj = ((JSONObject)jsonObjs.opt(i));
                        JSONObject jsonObjecthigh= ((JSONObject)jsonObjs.opt(i)).getJSONObject("high");
                        JSONObject jsonObjectlow = ((JSONObject)jsonObjs.opt(i)).getJSONObject("low");
                        String condition = jsonObj.getString("conditions");
                        String temperaturehs=jsonObjecthigh.getString("celsius");

                        String temperaturels=jsonObjectlow.getString("celsius");

                        f +=   "conddtion: "+condition+"High Temperature "+temperaturehs+"Low Temperature "+temperaturels+"\n" ;
                        Log.e("aaaaaaaaaaaaa",f);

                    }


                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"invaild enter",Toast.LENGTH_LONG).show();
                    Log.e("aaaaaaaaaaaaa",e.getMessage());

                    e.printStackTrace();
                }

            }else {try {

                JSONArray jsonObjs = new JSONObject(strResult).getJSONObject("forecast").getJSONObject("simpleforecast").getJSONArray("forecastday");
                Log.e("aaaaaaaaaaaaa",f);
                for(int i = 0; i < jsonObjs.length() ; i++){
                    JSONObject jsonObj = ((JSONObject)jsonObjs.opt(i));
                    JSONObject jsonObjecthigh= ((JSONObject)jsonObjs.opt(i)).getJSONObject("high");
                    JSONObject jsonObjectlow = ((JSONObject)jsonObjs.opt(i)).getJSONObject("low");
                    String condition = jsonObj.getString("conditions");
                    String temperaturehh=jsonObjecthigh.getString("fahrenheit");

                    String temperaturelh=jsonObjectlow.getString("fahrenheit");


                    f +=   "conddtion: "+condition+"High T "+temperaturehh+"Low T "+temperaturelh+"\n" ;
                    Log.e("aaaaaaaaaaaaa",f);

                }


            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(),"invaild enter",Toast.LENGTH_LONG).show();
                Log.e("aaaaaaaaaaaaa",e.getMessage());

                e.printStackTrace();
            }


            }

        }else {
            try {

                JSONArray jsonObjs = new JSONObject(strResult).getJSONObject("forecast").getJSONObject("simpleforecast").getJSONArray("forecastday");
                Log.e("bbbbbbbb",f);
                for(int i = 0; i < jsonObjs.length() ; i++){
                    JSONObject jsonObj = ((JSONObject)jsonObjs.opt(i));
                    JSONObject jsonObjecthigh= ((JSONObject)jsonObjs.opt(i)).getJSONObject("high");
                    JSONObject jsonObjectlow = ((JSONObject)jsonObjs.opt(i)).getJSONObject("low");
                    String condition = jsonObj.getString("conditions");
                    String temperaturehs=jsonObjecthigh.getString("celsius");

                    String temperaturels=jsonObjectlow.getString("celsius");

                    f +=   "conddtion: "+condition+"High Temperature "+temperaturehs+"Low Temperature "+temperaturels+"\n" ;
                    Log.e("bbbbb",f);

                }


            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(),"invaild enter",Toast.LENGTH_LONG).show();
                Log.e("aaaaaaaaaaaaa",e.getMessage());

                e.printStackTrace();
            }


        }


     /*
        try {

            JSONArray jsonObjs = new JSONObject(strResult).getJSONObject("forecast").getJSONObject("simpleforecast").getJSONArray("forecastday");

            for(int i = 0; i < jsonObjs.length() ; i++){
                JSONObject jsonObj = ((JSONObject)jsonObjs.opt(i));
                JSONObject jsonObjecthigh= ((JSONObject)jsonObjs.opt(i)).getJSONObject("high");
                JSONObject jsonObjectlow = ((JSONObject)jsonObjs.opt(i)).getJSONObject("low");
                String condition = jsonObj.getString("conditions");
                String temperaturehh=jsonObjecthigh.getString("fahrenheit");
                String temperaturehs=jsonObjecthigh.getString("celsius");
                String temperaturelh=jsonObjectlow.getString("fahrenheit");
                String temperaturels=jsonObjectlow.getString("celsius");
                Log.d("aaaaaaaaaaaaa",f);

                f +=   "conddtion: "+condition+" "+temperaturehh+" "+temperaturehs+" "+temperaturelh+" "+temperaturels ;
            }
        } catch (JSONException e) {
        Log.d("aaaaaaaaaaaaa",f);
            e.printStackTrace();
        }*/
       Log.e("aaaaaaaaaaaaa",f);
        return f;
    }








}
