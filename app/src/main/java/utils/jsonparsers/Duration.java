package utils.jsonparsers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Duration implements Serializable {

    @SerializedName("minute")
    @Expose
    private Integer minute;
    @SerializedName("unit")
    @Expose
    private String unit;
    @SerializedName("normalized")
    @Expose
    private Normalized normalized;
    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("confidence")
    @Expose
    private Double confidence;
    @SerializedName("type")
    @Expose
    private String type;

    public Integer getMinute ()
    {
        return minute;
    }

    public void setMinute (Integer minute)
    {
        this.minute = minute;
    }

    public String getUnit ()
    {
        return unit;
    }

    public void setUnit (String unit)
    {
        this.unit = unit;
    }

    public Normalized getNormalized ()
    {
        return normalized;
    }

    public void setNormalized (Normalized normalized)
    {
        this.normalized = normalized;
    }

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

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }


}
