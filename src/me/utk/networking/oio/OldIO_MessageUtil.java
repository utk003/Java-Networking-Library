package me.utk.networking.oio;

enum OldIO_MessageUtil {
    CONNECTION_CONFIRMATION, SERVER_CLOSED, CONNECTION_CLOSED, END_MESSAGE, NONE;

    @Override
    public String toString() {
        switch (this) {
            case CONNECTION_CONFIRMATION:
                return "CONNECTION CONFIRMED";

            case CONNECTION_CLOSED:
                return "CONNECTION CLOSED";
            case SERVER_CLOSED:
                return "SERVER CLOSED";

            case END_MESSAGE:
                return "MESSAGE COMPLETE";

            case NONE:
            default:
                return "";
        }
    }

    public static OldIO_MessageUtil fromString(String s) {
        switch (s) {
            case "CONNECTION CONFIRMED":
                return CONNECTION_CONFIRMATION;

            case "CONNECTION CLOSED":
                return CONNECTION_CLOSED;
            case "SERVER CLOSED":
                return SERVER_CLOSED;

            case "MESSAGE COMPLETE":
                return END_MESSAGE;

            default:
                return NONE;
        }
    }

}
