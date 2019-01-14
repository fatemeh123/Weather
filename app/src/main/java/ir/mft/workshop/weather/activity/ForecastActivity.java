package ir.mft.workshop.weather.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import ir.aid.library.Frameworks.helper.ConnectionHelper;
import ir.aid.library.Frameworks.helper.ToolbarHelper;
import ir.aid.library.Frameworks.utils.FrameworkException;
import ir.aid.library.Interfaces.OnGetResponse;
import ir.mft.workshop.weather.R;
import ir.mft.workshop.weather.adapter.WeatherAdapter;
import ir.mft.workshop.weather.api.WeatherAPI;

public class ForecastActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        recyclerView = findViewById(R.id.recycler);

        Bundle bundle = getIntent().getExtras();    // vase gereftane esme city

        String city = bundle.getString("city");

        try {    //alamate back tooye safhe
            new ToolbarHelper(this).Title(city).ButtonHome(true).HomeIcon(R.drawable.icon_arrow_back);
        } catch (FrameworkException e) {
            e.printStackTrace();
        }


        connection(city);
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
                                initWeatherData(result);
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

        WeatherAPI api = gson.fromJson(result , WeatherAPI.class);

        WeatherAdapter weatherAdapter=new WeatherAdapter(this , api);
        recyclerView.setAdapter(weatherAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(
                ForecastActivity.this,LinearLayout.VERTICAL,false));


    }

    private int getIcon(String sky){

        switch (sky){

            case "Cloudy": return R.drawable.icon_cloud;

            default: return 0;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                finish();
                break;
        }


        return super.onOptionsItemSelected(item);
    }
}
