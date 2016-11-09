package com.sacc;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.gson.Gson;
import com.googlecode.objectify.ObjectifyService;
import com.sacc.entity.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by djoé on 08/11/2016.
 */
public class VideoPullingServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(Worker.class.getName());
    private static Storage storage = null;
    private static Datastore datastore = null;

    @Override
    public void init() {
        storage = StorageOptions.defaultInstance().service();
        datastore = DatastoreOptions.defaultInstance().service();
        ObjectifyService.register(Video.class);
        ObjectifyService.register(User.class);
    }


    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        BufferedReader reader = request.getReader();
        String requestVideo = reader.readLine();

        Gson json = new Gson();

        ConversionRequest cr = json.fromJson(requestVideo, ConversionRequest.class);
        List<User> users = ObjectifyService.ofy()
                .load()
                .type(User.class)
                .filter("userId", cr.getMailAddress())
                .list();

        User user = null;
        if (users.size() == 1)
            user = users.get(0);
        else
            user = new User();

        Queue queue = null;

        if (user.getSla() == SLA.BRONZE)
            queue = QueueFactory.getQueue("bronze-queue");
        else
            queue = QueueFactory.getQueue("pending-queue");

        for (FORMAT format : cr.getConvertTypes()) {
            Video video = new Video();
            video.setName(cr.getName());
            video.setUserId(cr.getMailAddress());
            video.setDuration(cr.getDuration());
            video.setSla(user.getSla());

            if (video.getSla() == SLA.BRONZE)
                queue.add(TaskOptions.Builder.withUrl("/worker").param("video", json.toJson(video, Video.class)));
            else
                queue.add(TaskOptions.Builder.withUrl("/ArgentGoldServlet").param("video", json.toJson(video, Video.class)));

        }

    }

}
