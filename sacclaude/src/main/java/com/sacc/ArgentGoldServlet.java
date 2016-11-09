package com.sacc;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by djoé on 28/10/2016.
 */
public class ArgentGoldServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(Worker.class.getName());

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        BufferedReader reader = request.getReader();
        String video = reader.readLine();

        log.info( "Video put in queue");
        // Add the task to the default queue.
        Queue queue = QueueFactory.getQueue("ar-gold-queue");
        queue.add(TaskOptions.Builder.withUrl("/worker").param("video", video));

        response.sendRedirect("/");

    }
}