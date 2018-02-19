package utils.jsonparsers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by bill on 2/10/18.
 */

public class Number implements Serializable {

    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("confidence")
    @Expose
    private Double confidence;
    @SerializedName("type")
    @Expose
    private String type;

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
