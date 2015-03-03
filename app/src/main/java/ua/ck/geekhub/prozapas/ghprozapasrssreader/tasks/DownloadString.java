package ua.ck.geekhub.prozapas.ghprozapasrssreader.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by Allteran on 16.11.2014.
 */
public class DownloadString {
    public String mDownloadedString;

    public String getDownloadedString() {
        return mDownloadedString;
    }

    public DownloadString(String urlAdress) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            URL url = new URL(urlAdress);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            String buffString;
            while ((buffString = bufferedReader.readLine()) != null)
                stringBuilder.append(buffString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mDownloadedString = stringBuilder.toString();
    }
}

