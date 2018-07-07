package gmads.it.gmads_lab1.Map.common.model;


import com.google.gson.annotations.SerializedName;

public class Polyline {

    @SerializedName("points")
    String points;

    public String getPoints() {
        return points;
    }
}