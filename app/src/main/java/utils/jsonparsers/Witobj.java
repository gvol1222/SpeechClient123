
package utils.jsonparsers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Witobj implements Serializable {

    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("entities")
    @Expose

    private Entities entities;
    @SerializedName("msg_id")
    @Expose
    private String msgId;

    public List<Intent> getIntents() {
        return intents;
    }

    @SerializedName("intents")
    @Expose
    private List<Intent> intents = null;

    @SerializedName("traits")
    @Expose
    private Traits traits;

    public Traits getTraits() {
        return traits;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Entities getEntities() {
        return entities;
    }

    public void setEntities(Entities entities) {
        this.entities = entities;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

}
