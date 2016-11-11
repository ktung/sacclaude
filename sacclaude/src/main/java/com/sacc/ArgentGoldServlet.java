package com.sacc;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.gson.Gson;
import com.googlecode.objectify.ObjectifyService;
import com.sacc.entity.SLA;
import com.sacc.entity.STATUS;
import com.sacc.entity.User;
import com.sacc.entity.Video;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by djoé on 28/10/2016.
 */
public class ArgentGoldServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(Worker.class.getName());

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String videoStr = request.getParameter("video");
        String userStr = request.getParameter("user");

        Gson json = new Gson();


        Video video = json.fromJson(videoStr, Video.class);
        User user = json.fromJson(userStr, User.class);

        // liste de video en traitement pour user
        List<Video> inQueueVideos = ObjectifyService.ofy()
                .load()
                .type(Video.class)
                .ancestor(video.getUser())
                .filter("status", STATUS.CONVERTING).list();

        video.setDate(new Date());
        // mise en pending
        if(user.getSla() == SLA.ARGENT && inQueueVideos.size() == 3 ||
                inQueueVideos.size() == 5)
        {
            video.setStatus(STATUS.PENDING);
            ObjectifyService.ofy().save().entity(video).now();
        }
        else // triater directement
        {
            video.setStatus(STATUS.CONVERTING);
            ObjectifyService.ofy().save().entity(video).now();
            // Add the task to the default queue.
            Queue queue = QueueFactory.getQueue("ar-gold-queue");
            queue.add(TaskOptions.Builder.withUrl("/worker").param("video", json.toJson(video, Video.class)));
        }

        response.sendRedirect("/");

    }
}
