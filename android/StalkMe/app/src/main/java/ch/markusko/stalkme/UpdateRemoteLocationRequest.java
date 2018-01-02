package ch.markusko.stalkme;

/**
 * Created by markus on 12/30/17.
 */

public class UpdateRemoteLocationRequest {

    private final String cookie;
    private final double lng;
    private final double lat;

    public UpdateRemoteLocationRequest(String cookie, double lng, double lat) {
        this.cookie = cookie;
        this.lng = lng;
        this.lat = lat;
    }

    public String getCookie() {
        return cookie;
    }

    public double getLng() {
        return lng;
    }

    public double getLat() {
        return lat;
    }
}
