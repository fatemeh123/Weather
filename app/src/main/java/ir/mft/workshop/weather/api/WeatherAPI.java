package ir.mft.workshop.weather.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import ir.mft.workshop.weather.api.models.Result;

public class WeatherAPI {

    @SerializedName("result")
    @Expose
    private Result result;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

}
