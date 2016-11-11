package com.sacc.servlet;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.googlecode.objectify.ObjectifyService;
import com.sacc.entity.User;
import com.sacc.entity.Video;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RemoveVideosServlet extends HttpServlet {
    private Storage storage;

    @Override
    public void init() {
        storage = StorageOptions.defaultInstance().service();
        ObjectifyService.register(Video.class);
        ObjectifyService.register(User.class);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Video> videos = ObjectifyService.ofy()
            .load()
            .type(Video.class)
            .order("-date")
            .list();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -5);
        Date limitDate = cal.getTime();

        for (Video v : videos) {
            if (v.getDate().compareTo(limitDate) <= 0) {
                storage.delete(v.getBucketName(),v.getBlobName());
                ObjectifyService.ofy().delete().entity(v).now();
            }
        }
    }
}
