package ir.mft.workshop.weather.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import ir.aid.library.Frameworks.helper.ConnectionHelper;
import ir.aid.library.Frameworks.utils.SharedPreferenceUtils;
import ir.aid.library.Interfaces.OnGetResponse;
import ir.mft.workshop.weather.R;
import ir.mft.workshop.weather.api.WeatherAPI;
import ir.mft.workshop.weather.tools.Converter;
import ir.mft.workshop.weather.tools.WeatherPhoto;

public class MainActivity extends AppCompatActivity {


    private MaterialDialog.Builder mBuilder;
    private MaterialDialog mDialog;

    private Button btnFind , btnForecast;
    private TextView txtCityName , txtTemp , txtSky;
    private ImageView img;
    private String city;
    private SharedPreferenceUtils preference;
    private static final String CITY_KET = "city";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preference = new SharedPreferenceUtils(this);

        initViews();

        if (preference.readString(CITY_KET , "").equals("")){
            connection("Tehran");  // اگه خالی بود دیفالت تهران رو بگیره
        }
        else {

            city = preference.readString(CITY_KET,"");
            connection(city); // دفعه بعدی باید لندن رو بگیره

        }

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDialog.show();

            }
        });

        initDialog();

    }

    private void initDialog(){

        Button btndismiss = (Button) mDialog.findViewById(R.id.btn_dismiss);
        Button btnSave= (Button) mDialog.findViewById(R.id.btn_save);
        final EditText cityEdt = (EditText) mDialog.findViewById(R.id.edt_city) ;
        btndismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });



        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cityEdt.getText().toString().equals("")) {
                    cityEdt.setError("is Empty...");
                } else {

                    final String city1= cityEdt.getText().toString();
                    preference.writeString(CITY_KET , city1);


                    Toast.makeText(MainActivity.this,"Successfuly saved!",Toast.LENGTH_LONG).show();

                    mDialog.dismiss();
                    recreate();
                }

            }

    });
    }

    private void initViews(){


        btnFind=findViewById(R.id.btnFind);
        txtCityName=findViewById(R.id.txtCityName);
        txtTemp=findViewById(R.id.txtTemp);
        img=findViewById(R.id.img_weather);
        txtSky=findViewById(R.id.txt_sky_is);
        btnForecast=findViewById(R.id.btnForcast);

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //too preference ham bayad zakhire konamesh
            }
        });

        final String location = preference.readString(CITY_KET , "");


        btnForecast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,ForecastActivity.class);
                intent.putExtra("city" , location);
                startActivity(intent);
            }
        });

        mBuilder= new MaterialDialog.Builder(this);
        mBuilder.customView(R.layout.find_city_dialog_layout,false);
        mDialog=mBuilder.build();


    }

    private void connection(String city){

        String url = "http://phoenix-iran.ir/Files_php_App/WeatherApi/newApiWeather.php";

        new ConnectionHelper(url , 5000)
                .addStringRequest("city" , city)
                .getResponse(new OnGetResponse() {
                    @Override
                    public void notConnection(String result) {
                        // اگر اتصال به اینترنت هم وصل نباشه این متد فراخانی میشه
                    }

                    @Override
                    public void success(final String result) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (result.equals("null")){
                                    Toast.makeText(MainActivity.this, "not Found", Toast.LENGTH_SHORT).show();
                                }
                                else if (!result.equals("null")){
                                    initWeatherData(result);

                                }
                                //Log.i("central_core" , result);
                            }
                        });

                        // هر اطلاعاتی که از سمت سرور وارد برنامه میشه داخل این result دخیره شده
                    }

                    @Override
                    public void nullable(String result) {
                        // اینجا رو یادم رفته پاک کنم در اصل عملی انجام نمیده, از قبل مونده که کال بک یجور دیگه بود
                    }
                });
    }

    private void initWeatherData(String result){

        Gson gson = new Gson();

        try{
            WeatherAPI api = gson.fromJson(result , WeatherAPI.class);

            int conditionTemp = api.getResult().getCondition().getTemperature();
            int temp = Converter.convertToC(conditionTemp);

            String city = api.getResult().getLocation().getCity();
            String skyText = api.getResult().getCondition().getText();

            txtCityName.setText(city);
            txtTemp.setText(String.valueOf(temp) + " C");
            txtSky.setText(skyText);

            img.setImageDrawable(getResources().getDrawable(WeatherPhoto.getPhotoWeather(skyText)));
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.d("central_core", e.toString());
            Toast.makeText(this, "check other city", Toast.LENGTH_LONG).show();
        }

    }

}
