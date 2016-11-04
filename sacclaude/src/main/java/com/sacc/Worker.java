package com.sacc;


import com.google.cloud.datastore.*;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.gson.Gson;
import com.sacc.entity.Video;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


/**
 * Created by djoé on 28/10/2016.
 */
public class Worker extends HttpServlet {
    private static final Logger log = Logger.getLogger(Worker.class.getName());
    private static final String BUCKET_NAME = "sacclaude.appspot.com";
    private static Storage storage = null;
    private static Datastore datastore = null;

    @Override
    public void init() {
        storage = StorageOptions.defaultInstance().service();
        datastore = DatastoreOptions.defaultInstance().service();
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
        log.info("Sleep time : " + (t2 - t1) + "ms");

        video.setConverted(true);


        List<Acl> acls = new ArrayList<>();
        acls.add(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));

        Blob blob =
                storage.create(
                        BlobInfo.builder(BUCKET_NAME, video.getName()).acl(acls).build(),
                        json.toJson(video, Video.class).getBytes());

        if(havePendingVideo(video.getUserId()))

        // return the public download link
        response.getWriter().print(blob.mediaLink());

    }

    private boolean havePendingVideo(String userId) {

        // Retrieve the last 10 visits from the datastore, ordered by timestamp.
        Query<Entity> query = Query.entityQueryBuilder().kind("video")
                .filter(StructuredQuery.PropertyFilter.eq("userId", userId))
                .orderBy(StructuredQuery.OrderBy.asc("date")).build();
        QueryResults<Entity> results = datastore.run(query);

        if(results.hasNext()) {
            Entity.
            results.next().key();
        }
    }
}