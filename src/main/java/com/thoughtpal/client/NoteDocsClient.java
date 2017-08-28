package com.thoughtpal.client;

import com.thoughtpal.model.notedoc.NoteDocumentText;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * Created by robertwood on 1/28/17.
 */
public class NoteDocsClient
{
    // TODO: DI
    private final HttpClient    client = HttpClients.createDefault();

    public NoteDocumentText getNoteDocument(String directory, String fileName) throws Exception
    {
        // curl http://localhost:8080/AppDev/AppDevAPI.JavaServlet.nodoc
        //String uriPath = "http://localhost:5011/" + workspace + "/" + noteDocName;
        String test = "A test & and another";
        String check = URLEncoder.encode(test, "UTF-8");
        //fileName = "AppDevFW%2EAWS%2Enodoc";
        //String uriPath = "http://localhost:5011/?dir=" + URLEncoder.encode(directory, "UTF-8") + "&file=" + URLEncoder.encode(fileName, "UTF-8");
        String uriPath = "http://localhost:5011/?dir=" + directory + "&file=" + fileName;
        HttpGet httpGet = new HttpGet(uriPath);
        try {
            HttpResponse response = client.execute(httpGet);

            HttpEntity entity = response.getEntity();
            String rawText = EntityUtils.toString(entity, "UTF-8");
            return new NoteDocumentText("001", "001", rawText);
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }

    }
}
