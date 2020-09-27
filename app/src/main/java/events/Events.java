package events;

import utils.jsonparsers.Witobj;

public class Events {


    public static class PartialResults {
        private final String message;

        public PartialResults(String message) {
            this.message = message;
        }

        public String getPartialResults() {
            return message;
        }


    }
    public static class Results {
        private String message;

        public Results(String message) {
            this.message = message;
        }
        public String getResults() {
            return message;
        }


    }

    public static class ActivatedRecognition {

        private final boolean activated;

        public ActivatedRecognition(boolean activated) {
            this.activated = activated;
        }


        public boolean isActivated() {
            return activated;
        }
    }

    public static class ComputingRecognition {

        private final boolean isComputing;

        public ComputingRecognition(boolean isComputing) {
            this.isComputing = isComputing;
        }

        public boolean isComputing() {
            return isComputing;
        }
    }

    public static class SpeechMessage {
        private final String message;


        public SpeechMessage(String message) {
            this.message = message;

        }

        public String getSpeechMessage() {
            return message;
        }

    }
    public static class SpeechMessageShow {
        private final String message;


        public SpeechMessageShow(String message) {
            this.message = message;

        }

        public String getSpeechMessage() {
            return message;
        }

    }
    public static class WitREsp {

        private final Witobj witResponse;
        private final String sender;

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