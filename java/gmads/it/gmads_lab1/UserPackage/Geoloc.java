package gmads.it.gmads_lab1.UserPackage;

/**
 * Created by giorgiocrepaldi on 04/05/18.
 */

public class Geoloc {

    private Double lat;
    private Double lng;

    public Geoloc(){

    }

    public Geoloc(Double lat, Double lng){
        this.lat = lat;
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

}
