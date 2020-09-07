package controller.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class ApiSunat {

    private String URLAPI = "https://api.sunat.cloud/ruc/";
    
    private String URLSUNATAPI = "https://dniruc.apisperu.com/api/v1/dni/48909348?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImFsZXhhbmRlcl9keF8xMEBob3RtYWlsLmNvbSJ9.6TLycBwcRyW1d-f_hhCoWK1yOWG_HJvXo8b-EoS5MhE";
    
    private String URLRENIECAPI = "https://dniruc.apisperu.com/api/v1/ruc/10764233889?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImFsZXhhbmRlcl9keF8xMEBob3RtYWlsLmNvbSJ9.6TLycBwcRyW1d-f_hhCoWK1yOWG_HJvXo8b-EoS5MhE";
    
    private String jsonURL;

    public ApiSunat(String numdocument) {
        URLAPI = "https://api.sunat.cloud/ruc/" + numdocument;
    }

    public String GetRequest() {
        String result = "";
        if (isValid(URLAPI)) {
            BufferedReader reader = null;
            try {
                URL obj = new URL(URLAPI);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");
                int responseCode = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuilder buffer = new StringBuilder();
                    int read;
                    char[] chars = new char[1024];
                    while ((read = reader.read(chars)) != -1) {
                        buffer.append(chars, 0, read);
                    }
                    jsonURL = buffer.toString();
                    result = "200";
                } else {
                    result = "GET Response Code:" + responseCode;
                }
                reader.close();
            } catch (IOException ex) {
                result = "Error:" + ex.getLocalizedMessage();
            }
        } else {
            result = "Error en el formato de la URL";
        }
        return result;
    }

    private boolean isValid(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }

    public String getJsonURL() {
        return jsonURL;
    }

}
