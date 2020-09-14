
package utils.jsonparsers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Intent implements Serializable {

    @SerializedName("confidence")
    @Expose
    private Double confidence;
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    public String getName() {
        return name;
    }

    public Double getConfidence() {
        return confidence;
    }


    public String getValue() {
        return id;
    }



}
