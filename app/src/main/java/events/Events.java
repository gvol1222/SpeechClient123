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

    public static class ComputingRecognition {

        private boolean isComputing;

        public ComputingRecognition(boolean isComputing) {
            this.isComputing = isComputing;
        }

        public boolean isComputing() {
            return isComputing;
        }
    }

    public static class SpeechMessage {
        private String message;
        private boolean Recognize_after;

        public SpeechMessage(String message, boolean recognize_after) {
            this.message = message;
            Recognize_after = recognize_after;
        }


        public String getSpeechMessage() {
            return message;
        }

        public boolean getRecognize_after() {
            return Recognize_after;
        }
    }

    public static class WitREsp {

        private Witobj witResponse;
        private String sender;

        public WitREsp(Witobj witResponse, String sender) {
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