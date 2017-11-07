package Recognize;

import android.speech.SpeechRecognizer;

/**
 * Created by bill on 11/2/17.
 */

public class Constants {

    //Errors speeech regognition
    public static final int ErrorAudio = SpeechRecognizer.ERROR_AUDIO;
    public static final int ErrorNoMatch = SpeechRecognizer.ERROR_NO_MATCH;
    public static final int ErrorSpeechTimeOut = SpeechRecognizer.ERROR_SPEECH_TIMEOUT;
    public static final int ErrorNetworkTimeout = SpeechRecognizer.ERROR_NETWORK_TIMEOUT;
    public static final int ErrorRecognizerBusy = SpeechRecognizer.ERROR_RECOGNIZER_BUSY;
    public static final int DelayRestartTime = 500;
}
