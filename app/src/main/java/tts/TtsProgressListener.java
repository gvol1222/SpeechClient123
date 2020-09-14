package tts;

/**
 * Created by bill on 11/26/17.
 */

public interface TtsProgressListener {

    void onEndTalk();
    void setIsTalking(boolean isTalking);
    boolean getIsTalking();
    void onStartTalk();
}
