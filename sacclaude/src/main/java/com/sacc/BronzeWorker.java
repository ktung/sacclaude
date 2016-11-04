package com.sacc;


import com.google.appengine.repackaged.com.google.gson.Gson;
import com.sacc.entity.FORMAT;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Time;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.sacc.entity.FORMAT.*;


/**
 * Created by djoé on 28/10/2016.
 */
public class BronzeWorker extends HttpServlet {
    private static final Logger log = Logger.getLogger(BronzeWorker.class.getName());

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String videoStr = request.getParameter("video");

        Gson json = new Gson();

        log.log(Level.INFO, "Video descriptor : " + videoStr);
        Video video = json.fromJson(videoStr, Video.class);

        long msPerSecondVideo = 1000;

        switch (video.getFormat())
        {
            case MKV:
                msPerSecondVideo = 2000;
                break;
            case MPEG4:
                msPerSecondVideo = 2500;
                break;
            case AVI:
                msPerSecondVideo = 1000;
                break;
            case OGG:
                msPerSecondVideo = 200;
                break;
            case FLV:
                msPerSecondVideo = 100;
                break;
        }

        long t1 = System.currentTimeMillis();

        try {
            Thread.sleep(msPerSecondVideo * video.getDuration());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long t2 = System.currentTimeMillis();
        log.log(Level.INFO, "Sleep time : " + (t2 - t1) + "ms");
    }
}