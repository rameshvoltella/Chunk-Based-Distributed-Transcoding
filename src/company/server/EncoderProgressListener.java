package company.server;

public interface EncoderProgressListener {
    void onProgressUpdate(String clientID, double progress);
}
