package events;

import utils.jsonparsers.Witobj;

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
    public static class ActivatedRecognition {

        private boolean activated;

        public ActivatedRecognition(boolean activated) {
            this.activated = activated;
        }


        public boolean isActivated() {
            return activated;
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

    public static class WitREsp {

        private Witobj witResponse;
        private String sender;
        public WitREsp(Witobj witResponse,String sender) {

            this.witResponse = witResponse;
            this.sender = sender;
        }

        public Witobj getWitResponse() {
            return witResponse;
        }
        public String getSender() {
            return sender;
        }
    }
}