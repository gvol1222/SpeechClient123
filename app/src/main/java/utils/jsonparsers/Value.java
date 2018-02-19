
package utils.jsonparsers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Value implements Serializable {

    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("grain")
    @Expose
    private String grain;
    @SerializedName("type")
    @Expose
    private String type;

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
