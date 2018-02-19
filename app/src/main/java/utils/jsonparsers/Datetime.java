
package utils.jsonparsers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Datetime  implements Serializable {

    @SerializedName("confidence")
    @Expose
    private Integer confidence;
    @SerializedName("values")
    @Expose
    private List<Value> values = null;
    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("grain")
    @Expose
    private String grain;
    @SerializedName("type")
    @Expose
    private String type;

    public Integer getConfidence() {
        return confidence;
    }

    public void setConfidence(Integer confidence) {
        this.confidence = confidence;
    }

    public List<Value> getValues() {
        return values;
    }

    public void setValues(List<Value> values) {
        this.values = values;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getGrain() {
        return grain;
    }

    public void setGrain(String grain) {
        this.grain = grain;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
