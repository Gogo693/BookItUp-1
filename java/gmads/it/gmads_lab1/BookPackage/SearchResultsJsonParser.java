package gmads.it.gmads_lab1.BookPackage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmads.it.gmads_lab1.BookPackage.BookJsonParser;
import gmads.it.gmads_lab1.BookPackage.Book;

public class SearchResultsJsonParser
{
    private BookJsonParser movieParser = new BookJsonParser();
    public List<Book> parseResults(JSONObject jsonObject)
    {
        if (jsonObject == null)
            return null;
        List<Book> results = new ArrayList<>();
        JSONArray hits = jsonObject.optJSONArray("hits");
        if (hits == null)
            return null;
        for (int i = 0; i < hits.length(); ++i) {
            JSONObject hit = hits.optJSONObject(i);
            if (hit == null)
                continue;
            Book movie = movieParser.parse(hit);
            if (movie == null)
                continue;
            results.add(movie);
        }
        return results;
    }
}
