package com.sacc;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.gson.Gson;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by djoé on 28/10/2016.
 */
public class BronzeWorker extends HttpServlet {
    private static final Logger log = Logger.getLogger(BronzeWorker.class.getName());
    private static final String BUCKET_NAME = "sacclaude.appspot.com";
    private static Storage storage = null;

    @Override
    public void init() {
        storage = StorageOptions.defaultInstance().service();
    }


    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String videoStr = request.getParameter("video");

        Gson json = new Gson();

        log.info("Video descriptor : " + videoStr);
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
        }

        long t1 = System.currentTimeMillis();

        try {
            Thread.sleep(msPerSecondVideo * video.getDuration());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long t2 = System.currentTimeMillis();
        log.info("Sleep time : " + (t2 - t1) + "ms");

        video.setCompressed(true);


        List<Acl> acls = new ArrayList<>();
        acls.add(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));

        Blob blob =
                storage.create(
                        BlobInfo.builder(BUCKET_NAME, video.getName()).acl(acls).build(),
                        json.toJson(video, Video.class).getBytes());

        // return the public download link
        response.getWriter().print(blob.mediaLink());

    }
}