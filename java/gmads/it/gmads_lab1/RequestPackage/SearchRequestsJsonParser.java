package gmads.it.gmads_lab1.RequestPackage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmads.it.gmads_lab1.RequestPackage.RequestJsonParser;
import gmads.it.gmads_lab1.RequestPackage.Request;

public class SearchRequestsJsonParser
{
    private RequestJsonParser movieParser = new RequestJsonParser();
    public List<Request> parseResults(JSONObject jsonObject)
    {
        if (jsonObject == null)
            return null;
        List<Request> results = new ArrayList<>();
        JSONArray hits = jsonObject.optJSONArray("hits");
        if (hits == null)
            return null;
        for (int i = 0; i < hits.length(); ++i) {
            JSONObject hit = hits.optJSONObject(i);
            if (hit == null)
                continue;
            Request movie = movieParser.parse(hit);
            if (movie == null)
                continue;
            results.add(movie);
        }
        return results;
    }
}
