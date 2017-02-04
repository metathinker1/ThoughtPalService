package com.thoughtpal.client;

import com.thoughtpal.model.note.NoteDocument;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by robertwood on 1/28/17.
 */
public class NoteDocsClient
{
    // TODO: DI
    private final HttpClient    client = HttpClients.createDefault();

    public String getNoteDocAsString(String noteDocName, String workspace)
    {
        // curl http://localhost:8080/AppDev/AppDevAPI.JavaServlet.nodoc
        String uriPath = "http://localhost:5011/" + workspace + "/" + noteDocName;
        HttpGet httpGet = new HttpGet(uriPath);
        try {
            HttpResponse response = client.execute(httpGet);

            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }

    }
}
