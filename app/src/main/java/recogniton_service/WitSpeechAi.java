package recogniton_service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.example.bill.Activities.R;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

import applications.Constatns;
import events.Events;
import recognize.SpeechRegognition;
import tts.SpeecHelper;
import tts.TtsProgressListener;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import utils.jsonparsers.Witobj;

public class WitSpeechAi  extends Service implements TtsProgressListener {

    private final String TAG = this.getClass().getSimpleName();
    protected SpeechRegognition recognition;
    private Handler startHandler;
    private Handler closeHandler;
    private boolean isFirst;
    private SpeecHelper talkengine;

    private OkHttpClient httpClient;
    private HttpUrl.Builder httpBuilder;
    private Request.Builder httpRequestBuilder;

    private boolean isFinishedTts;
    private Handler mHandler;
    private AudioRecord recorder;
    private static final int SAMPLE_RATE = 8000;
    private static final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL, AUDIO_FORMAT) * 10;
    private static final AtomicBoolean recordingInProgress = new AtomicBoolean(false);
    private Thread recordingThread;

    /* Go to your Wit.ai app Management > Settings and obtain the Client Access Token */
    private static final String CLIENT_ACCESS_TOKEN = "6QDRV7CUMG5GKV3FUMUVS4NLADANOMVN";
    //broadcast for actions on clicking notification
    private final BroadcastReceiver NotAction = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.i(TAG, "activated is broadcast " + recognition.isActivated());
          //  if (recognition.isActivated()) {
                //StopSrecognition();
              //  recognition.setActivated(false);
           // } else {
              //  speak(getResources().getString(R.string.StartMessage),true);

          //  }
        }
    };
    @Subscribe
    public void OnSpeechMessage(Events.SpeechMessage event){
        boolean reocgnizeAfter = event.getRecognige_after();
        String message = event.getSpeechMessage();
        speak(message,reocgnizeAfter);
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
        InitHandler();
        Init();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onDestroy() {
        super.onDestroy();
        free();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onStartTalk() {
        //on start talking assistant close recognition and enable beep


    }


    @Override
    public void onEndTalk() {
        //on end talking assistant start recognition
        isFinishedTts =true;
        //recognition.setActivated(true);
        Log.i(TAG,"End tts talking");

        runStartSpeech();
    }

    @Override
    public void setIsTalking(boolean isTalking) {

    }

    @Override
    public boolean getIsTalking() {
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void Init() {
        Log.i(TAG, "Initialization object and messages");
        isFinishedTts =true;
        talkengine = new SpeecHelper(getApplicationContext(), this);
        registerReceiver(NotAction, new IntentFilter(Constatns.NOT_ACTION));


    }
    //close and destroy speech recognition
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void free() {
        Log.i(TAG, "Free resources");
        if (recorder == null) return;
        recordingInProgress.set(false);
        recorder.stop();
        recorder.release();
        recorder = null;
        recordingThread = null;

        unregisterReceiver(NotAction);

    }

    public void StartMessage(final String msg) {


        ToasMessage(msg);
        talkengine.speak(msg);



    }
    public void ToasMessage(final String msg){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(WitSpeechAi.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //initiate handler for running on main thread
    private void InitHandler() {
        Log.i(TAG, "Initialization of handlers");
        startHandler = new Handler(Looper.getMainLooper());
        closeHandler = new Handler(Looper.getMainLooper());
    }

    public void setRecognition() {
        Log.i(TAG, "Recognition created");
        recognition = new SpeechRegognition(getApplicationContext());


    }



    public void StopSrecognition() {
        if (recorder == null) return;
        recordingInProgress.set(false);
        recorder.stop();
        recorder.release();
        recorder = null;
        recordingThread = null;
    }

    public void StartRecognition() {

            startService(new Intent(this, Maestro.class));
        httpClient = new OkHttpClient();
        httpBuilder = HttpUrl.parse("https://api.wit.ai/speech").newBuilder();
        httpBuilder.addQueryParameter("v", "20200916?q=");
        httpRequestBuilder = new Request.Builder()
                .url(httpBuilder.build())
                .header("Authorization", "Bearer 6QDRV7CUMG5GKV3FUMUVS4NLADANOMVN")
                .header("Content-Type", "audio/raw")
                .header("Transfer-Encoding", "chunked");

        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL, AUDIO_FORMAT, BUFFER_SIZE);
            recorder.startRecording();
            recordingInProgress.set(true);
            recordingThread = new Thread(new StreamRecordingRunnable(), "Stream Recording Thread");
            recordingThread.start();

    }

    // Define a Runnable to stream the recording data to the Speech API
    // https://wit.ai/docs/http#post__speech_link
    private class StreamRecordingRunnable implements Runnable {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void run() {
            final ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
            RequestBody requestBody = new RequestBody() {
                @Override
                public MediaType contentType() {
                    return MediaType.parse("audio/raw;encoding=signed-integer;bits=16;rate=8000;endian=little");
                }

                @Override
                public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {
                    while (recordingInProgress.get()) {
                        int result = recorder.read(buffer, BUFFER_SIZE);
                        if (result < 0) {
                            throw new RuntimeException("Reading of audio buffer failed: " +
                                    getBufferReadFailureReason(result));
                        }
                        bufferedSink.write(buffer);
                        buffer.clear();
                    }
                }
            };

            // Start streaming audio to Wit.ai Speech AP
            Request request = httpRequestBuilder.post(requestBody).build();

            try (Response response = httpClient.newCall(request).execute()) {

                if (response.isSuccessful()) {

                    String responseData = response.body().string();
                    Log.e("Your Array Response", response.toString());

                    Log.v("Streaming Response", responseData);
                }
            } catch (IOException e) {
                Log.e("Streaming Response", e.getMessage());
            }
            finally {
                Log.i(" abc","audio ini");
            }

        }

        private String getBufferReadFailureReason(int errorCode) {
            switch (errorCode) {
                case AudioRecord.ERROR_INVALID_OPERATION:
                    return "ERROR_INVALID_OPERATION";
                case AudioRecord.ERROR_BAD_VALUE:
                    return "ERROR_BAD_VALUE";
                case AudioRecord.ERROR_DEAD_OBJECT:
                    return "ERROR_DEAD_OBJECT";
                case AudioRecord.ERROR:
                    return "ERROR";
                default:
                    return "Unknown (" + errorCode + ")";
            }
        }
    }


    public void runStartSpeech() {

        startHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "recognition started first: " + isFirst);
                StartRecognition();

            }
        });
    }





    public boolean isFinishedTts() {
        return isFinishedTts;
    }



    public void speak (String message,boolean recognize_after){

        //
        //
        // StartRecognition();
        /*if (recognize_after) {
          StartRecognition();
        }
        else
            StopSrecognition();*/

        StartMessage(message);
    }


}