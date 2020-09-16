
package utils.jsonparsers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Entities implements Serializable {

    @SerializedName("datetime")
    @Expose
    private List<Datetime> datetime = null;
    @SerializedName("duration")
    @Expose
    private List<Duration> duration = null;
    @SerializedName("app_data")
    @Expose
    private List<AppDatum> appData = null;

    @SerializedName("phone_number")
    @Expose
    private List<PhoneNumber> phoneNumber= null;

    @SerializedName("intent")
    @Expose
    private List<Intent> intent = null;
    @SerializedName("number")
    @Expose
    private List<Number> number = null;



    public List<Duration> getDuration() {
        return duration;
    }

    public void setDuration(List<Duration> duration) {
        this.duration = duration;
    }

    public List<PhoneNumber> getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(List<PhoneNumber> phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Number> getNumber() {
        return number;
    }

    public void setNumber(List<Number> number) {
        this.number = number;
    }

    public List<Datetime> getDatetime() {
        return datetime;
    }

    public void setDatetime(List<Datetime> datetime) {
        this.datetime = datetime;
    }

    public List<AppDatum> getAppData() {
        return appData;
    }

    public void setAppData(List<AppDatum> appData) {
        this.appData = appData;
    }

    public List<Intent> getIntent() {
        return intent;
    }

    public void setIntent(List<Intent> intent) {
        this.intent = intent;
    }

}
