package com.sacc.servlet;

import com.google.appengine.repackaged.com.google.gson.Gson;
import com.sacc.entity.ConversionRequest;
import com.sacc.entity.FORMAT;
import com.sacc.entity.SLA;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Servlet handling all the the requests done to our service
 * Created by lpotages on 04/11/16.
 */
public class RequestConversionServlet extends HttpServlet
{
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response){
        ConversionRequest newRequest = new ConversionRequest();

        // First building the ConversionRequest
        String mailAddress = request.getParameter("mail");
        newRequest.setMailAddress(mailAddress);

        SLA sla = SLA.BRONZE;

        switch ( request.getParameter("sla")){
            case "bronze":
                sla = SLA.BRONZE;
                break;
            case "argent":
                sla = SLA.ARGENT;
                break;
            case "gold":
                sla = SLA.GOLD;
                break;
        }

        newRequest.setSla(sla);

        String name = request.getParameter("name");
        newRequest.setName(name);

        Integer duration = Integer.parseInt(request.getParameter("duration"));
        newRequest.setDuration(duration);

        if(("aviConvert").equals(request.getParameter("avi"))){
            newRequest.addConvertType(FORMAT.AVI);
        }
        if(("mpegConvert").equals(request.getParameter("mpeg"))){
            newRequest.addConvertType(FORMAT.MPEG4);
        }
        if(("mkvConvert").equals(request.getParameter("mkv"))){
            newRequest.addConvertType(FORMAT.MKV);
        }
        if(("oggConvert").equals(request.getParameter("ogg"))){
            newRequest.addConvertType(FORMAT.OGG);
        }
        if(("flvConvert").equals(request.getParameter("flv"))){
            newRequest.addConvertType(FORMAT.FLV);
        }


        // Generating the JSON Object for this request
        Gson gson = new Gson();
        String parsedRequest = gson.toJson(newRequest);


        // Forwarding the request to the right service
        try{
            URL url;

            switch (sla){
                case ARGENT:
                    url = new URL("http://sacclaude.appspot.com/argent");
                    break;
                case GOLD:
                    url = new URL("http://sacclaude.appspot.com/gold");
                    break;
                default:
                    url = new URL("http://sacclaude.appspot.com/bronze");
                    break;
            }

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(URLEncoder.encode(parsedRequest, "UTF-8"));
            writer.close();

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                request.setAttribute("error", "");
                StringBuffer res = new StringBuffer();
                String line;

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    res.append(line);
                }
                reader.close();
                request.setAttribute("response", response.toString());
            } else {
                request.setAttribute("error", conn.getResponseCode() + " " + conn.getResponseMessage());
            }
        } catch (Exception e){
            log(e.getMessage());
        }

    }
}
