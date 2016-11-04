package com.sacc;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.repackaged.com.google.gson.Gson;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by djoé on 28/10/2016.
 */
public class BronzeServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        BufferedReader reader = request.getReader();
        String video = reader.readLine();

        // Add the task to the default queue.
        Queue queue = QueueFactory.getQueue("bronze-queue");
        queue.add(TaskOptions.Builder.withUrl("/bronzeworker").param("video", video));

        response.sendRedirect("/");

    }
}
