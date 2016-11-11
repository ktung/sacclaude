package com.sacc;


import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.gson.Gson;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.sacc.entity.SLA;
import com.sacc.entity.STATUS;
import com.sacc.entity.User;
import com.sacc.entity.Video;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
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
        /*ObjectifyService.register(Video.class);
        ObjectifyService.register(User.class);*/
    }


    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String videoStr = request.getParameter("video");
        String userId = request.getParameter("user");

        Key<User> keyUser = Key.create(User.class, userId);
        Video video = ObjectifyService.ofy().cache(false).load().key(Key.create(keyUser, Video.class, videoStr)).now();

        if(video == null)
            throw new IOException(videoStr);

        // récupère les data de la vidéo (on s'en sert pas, mais c'est pour la logique)
        byte[] videoData =storage.get(video.getBucketName(), video.getBlobName()).content();

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

        // sauvegarde de la vidéo convertie
        Blob blob =
                storage.create(
                        BlobInfo.builder(BUCKET_NAME, video.getName()).acl(acls).build(),
                        new ByteArrayInputStream(videoData));

        video.setStatus(STATUS.DONE);

        // update du statut de la vidéo
        ObjectifyService.ofy().save().entity(video).now();

        // vois si il y a des vidéo en pending
        if(video.getSla() != SLA.BRONZE)
            unpendingVideo(video);

    }

    private boolean unpendingVideo(Video video) {

        // list des pidéo en pending pour l'utilisateur
        List<Video> result = ObjectifyService.ofy()
                .load()
                .type(Video.class)
                .ancestor(ObjectifyService.ofy().load().key(Key.create(User.class, video.getUserId())).now())
                .filter("status", STATUS.PENDING)
                .order("-date")
                .limit(1).list();

        if(result.size() == 0) {
            return false;
        }
        Video v = result.get(0);

        v.setStatus(STATUS.CONVERTING);

        ObjectifyService.ofy().save().entity(v).now();
        Gson json = new Gson();

        // mise en queue pour traitement
        Queue queue = QueueFactory.getQueue("ar-gold-queue");
        queue.add(TaskOptions.Builder.withUrl("/worker").param("video", json.toJson(v, Video.class)));

        return true;

    }
}