package com.sacc;

import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.sacc.entity.User;
import com.sacc.entity.Video;

import javax.annotation.Nonnull;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by djoé on 11/11/2016.
 */
public class Initializer implements ServletContextListener {

    @Override
    public void contextInitialized(@Nonnull final ServletContextEvent SCE) {

        ObjectifyService.register(Video.class);
        ObjectifyService.register(User.class);
        ObjectifyService.begin();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
