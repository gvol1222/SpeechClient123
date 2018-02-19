
package utils.jsonparsers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AppDatum  implements Serializable {

    @SerializedName("suggested")
    @Expose
    private Boolean suggested;
    @SerializedName("confidence")
    @Expose
    private Double confidence;
    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("type")
    @Expose
    private String type;

    public Boolean getSuggested() {
        return suggested;
    }

    public void setSuggested(Boolean suggested) {
        this.suggested = suggested;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
