package gmads.it.gmads_lab1.RequestPackage;

import org.json.JSONObject;

public class RequestJsonParser
{
    public Request parse(JSONObject jsonObject)
    {
        if (jsonObject == null)
            return null;

        String rId = jsonObject.optString("rId");
        int reviewStatusOwner = jsonObject.optInt("reviewStatusOwner");
        int reviewStatusRenter = jsonObject.optInt("reviewStatusRenter");
        int requestStatus = jsonObject.optInt("requestStatus");
        String ownerId = jsonObject.optString("ownerId");
        String bId = jsonObject.optString("bId");
        String bName = jsonObject.optString("bName");
        String renterId = jsonObject.optString("renterId");
        String ownerName = jsonObject.optString("ownerName");
        String renterName = jsonObject.optString("renterName");
        String urlBookImage = jsonObject.optString("urlBookImage");
        Long objectID = jsonObject.optLong("objectID");

            if (rId != null ){
                return new Request(
                    rId,
                    reviewStatusOwner,
                    reviewStatusRenter,
                    requestStatus,
                    ownerId,
                    bId,
                    bName,
                    renterId,
                    ownerName,
                    renterName,
                    urlBookImage,
                    objectID);
        }
        return null;
    }
}
