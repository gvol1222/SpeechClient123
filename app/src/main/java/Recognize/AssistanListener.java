package Recognize;

/**
 * Created by bill on 11/2/17.
 */

public interface AssistanListener {
    void OnSpeechLiveResult(String LiveResult);

    void OnSpeechResult(String Result);

    void OnSpeechError(int Error);


}
