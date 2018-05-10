package events;

public class Events {


    public static class PartialResults {
        private String message;
        public PartialResults(String message) {
            this.message = message;
        }
        public String getPartialResults() {
            return message;
        }
    }

    public static class FinalResults {
        private String message;
        public FinalResults(String message) {
            this.message = message;
        }
        public String getFinalResults() {
            return message;
        }
    }
    public static class SpeechMessage {
        private String message;
        private boolean Recognige_after;

        public SpeechMessage(String message, boolean recognige_after) {
            this.message = message;
            Recognige_after = recognige_after;
        }


        public String getSpeechMessage() {
            return message;
        }

        public boolean getRecognige_after() {
            return Recognige_after;
        }
    }
    public static class SpeechError {

        private boolean error;

        public SpeechError(boolean error) {
            this.error = error;
        }

        public boolean isError() {
            return error;
        }
    }
}
