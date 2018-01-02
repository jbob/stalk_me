package ch.markusko.stalkme;

/**
 * Created by markus on 12/30/17.
 */

public class CreateSessionResult {

    private final String shareUrl;
    private final String cookie;
    private final String id;

    public CreateSessionResult(String shareUrl, String cookie, String id) {
        this.shareUrl = shareUrl;
        this.cookie = cookie;
        this.id = id;
    }


    public String getShareUrl() {
        return shareUrl;
    }

    public String getCookie() {
        return cookie;
    }

    public String getId() {
        return id;
    }
}
