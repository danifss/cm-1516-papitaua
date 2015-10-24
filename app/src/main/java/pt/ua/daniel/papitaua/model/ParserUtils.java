package pt.ua.daniel.papitaua.model;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ico on 2015-10-03.
 */
public class ParserUtils {

    /**
     * calls the remote web service and returns the JSON results as a string
     *
     * @param baseURL   the remote resource
     * @return  results enconded in JSON
     * @throws MalformedURLException
     * @throws IOException
     */
    static public String callEmentasWS(String baseURL) throws MalformedURLException, IOException {
        URL url;
        StringBuilder builder = new StringBuilder();

        url = new URL(baseURL);
        // this code works with API 23
        // for older API, the Apache HttpConnection may be used instead
        // e.g.: http://www.vogella.com/tutorials/AndroidJSON/article.html#androidjson_read
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } finally {
            urlConnection.disconnect();
        }
        return builder.toString();
    }
}
