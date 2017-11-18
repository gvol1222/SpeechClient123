package WitConnection;

/**
 * Created by bill on 11/16/17.
 */

public interface WitResponseMessage {

    void ErrorOnCommand(String msg);

    void ErrorCommand(String msg);

    void Message(String search, String application, String conf);
}
