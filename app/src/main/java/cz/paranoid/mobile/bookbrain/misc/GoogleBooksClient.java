package cz.paranoid.mobile.bookbrain.misc;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.paranoid.mobile.bookbrain.containers.GoogleBookItem;

/**
 * Google books API client
 */
public class GoogleBooksClient
{
    /** base URL for calls */
    private static final String API_BASE_URL = "https://www.googleapis.com/books/v1/";
    /** async client */
    private AsyncHttpClient client;

    /**
     * Default constructor
     */
    public GoogleBooksClient()
    {
        this.client = new AsyncHttpClient();
    }

    /**
     * Builds API call URL
     * @param relativeUrl       relative part of URL
     * @return                  built URL
     */
    private String getApiUrl(String relativeUrl)
    {
        return API_BASE_URL + relativeUrl;
    }

    /**
     * Retrieve books from API using supplied parameters
     * @param isbn          book ISBN
     * @param listener      listener to be invoked after search
     */
    public void getBooks(final String isbn, final GoogleBookListListener listener)
    {
        try
        {
            String url = getApiUrl("volumes?q=");
            // ask Google
            client.get(url + URLEncoder.encode(isbn, "utf-8"), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // initiate list
                    List<GoogleBookItem> resList = new ArrayList<GoogleBookItem>();
                    try {
                        // ensure response arrived
                        if (response != null)
                        {
                            // parse JSON response

                            String resTitle = null;
                            String resAuthors = "";
                            String foundISBN = null;
                            // go through all items
                            JSONArray arr = response.getJSONArray("items");
                            for (int i = 0; i < arr.length(); i++)
                            {
                                JSONObject bookRec = arr.getJSONObject(i);
                                JSONObject itemRec = bookRec.getJSONObject("volumeInfo");
                                resTitle = itemRec.getString("title");
                                JSONArray authRec = itemRec.getJSONArray("authors");
                                resAuthors = "";
                                for (int j = 0; j < authRec.length(); j++)
                                {
                                    if (resAuthors.length() != 0)
                                        resAuthors += ", ";
                                    resAuthors += authRec.getString(j);
                                }

                                foundISBN = null;
                                JSONArray idents = itemRec.getJSONArray("industryIdentifiers");
                                for (int j = 0; j < idents.length(); j++)
                                {
                                    JSONObject idrec = idents.getJSONObject(j);
                                    String idtype = idrec.getString("type");
                                    String val = idrec.getString("identifier");
                                    // accept only ISBN 10 and 13
                                    if (idtype.equals("ISBN_13") || (idtype.equals("ISBN_10") && foundISBN == null))
                                        foundISBN = val;
                                }

                                GoogleBookItem gbi = new GoogleBookItem();
                                gbi.name = resTitle;
                                gbi.authors = resAuthors;
                                gbi.isbn = foundISBN;
                                resList.add(gbi);
                                Log.d("BookBrain", "Added book "+gbi.name);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        //
                    }

                    // invoke listener
                    listener.bookListReceived(resList);
                }
            });
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
    }
}