package gmads.it.gmads_lab1.BookPackage;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BookJsonParser
{
    public Book parse(JSONObject jsonObject)
    {
        List<String> lc=new ArrayList<>();
        List<String> la=new ArrayList<>();

        if (jsonObject == null)
            return null;
        String bId=jsonObject.optString("bId");
        String isbn=jsonObject.optString("isbn");
        String title = jsonObject.optString("title");
        String description = jsonObject.optString("description");
        String urlimage= jsonObject.optString("urlimage");
        String author= jsonObject.optString("author");
        String publishdate=jsonObject.optString("publishDate");
        String categories= jsonObject.optString("categories");

        Long distance = jsonObject.optJSONObject("_rankingInfo").optLong("geoDistance");

        Double finderlat=jsonObject.optJSONObject("_rankingInfo").optJSONObject("matchedGeoLocation").optDouble("lat");
        Double finderlng=jsonObject.optJSONObject("_rankingInfo").optJSONObject("matchedGeoLocation").optDouble("lng");
        Long id=jsonObject.optLong("objectID");
        lc.add(categories);
        la.add(author);
        String owner= jsonObject.optString("owner");
        String publisher= jsonObject.optString("publisher");
        String name=jsonObject.optString("nomeproprietario");
        Double lat= jsonObject.optJSONObject("_geoloc").optDouble("lat",0.0);
        Double lng= jsonObject.optJSONObject("_geoloc").optDouble("lng",0.0);
        int stato = jsonObject.optInt("stato");

        if (title != null ){
            Book b= new Book(
                    bId,
                    isbn,
                    title,
                    description,
                    urlimage,
                    publishdate,
                    la,
                    lc,
                    publisher,
                    owner,
                    lat,
                    lng,
                    stato);
            b.setBId(bId);
            b.setObjectID(id);
            b.setNomeproprietario(name);
            b.setDistance(distance);
            b.setfinder(finderlat,finderlng);
            return b;
        }
        return null;
    }
}
