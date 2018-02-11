
package utils.jsonparsers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Intent {

    @SerializedName("confidence")
    @Expose
    private Double confidence;
    @SerializedName("value")
    @Expose
    private String value;

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

}
