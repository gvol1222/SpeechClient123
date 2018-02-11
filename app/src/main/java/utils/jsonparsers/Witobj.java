
package utils.jsonparsers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Witobj implements Serializable {

    @SerializedName("_text")
    @Expose
    private String text;
    @SerializedName("entities")
    @Expose
    private Entities entities;
    @SerializedName("msg_id")
    @Expose
    private String msgId;

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
