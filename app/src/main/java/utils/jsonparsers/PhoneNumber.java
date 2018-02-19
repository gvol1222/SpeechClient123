package utils.jsonparsers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by bill on 2/11/18.
 */

public class PhoneNumber implements Serializable {


    @SerializedName("value")
    @Expose
    private String value;

    @SerializedName("confidence")
    @Expose
    private Double confidence;

    public String getValue ()
    {
        return value;
    }

    public void setValue (String value)
    {
        this.value = value;
    }

    public Double getConfidence ()
    {
        return confidence;
    }

    public void setConfidence (Double confidence)
    {
        this.confidence = confidence;
    }
    @Override
    public String toString()
    {
        return "ClassPojo [value = "+value+", confidence = "+confidence.toString()+"]";
    }
}
