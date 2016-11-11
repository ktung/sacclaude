package com.sacc;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.storage.*;
import com.google.gson.Gson;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.sacc.entity.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by djoé on 08/11/2016.
 */
public class VideoPullingServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(Worker.class.getName());
    private static final String BUCKET_NAME = "sacclaude.appspot.com";
    private static Storage storage = null;
    private static Datastore datastore = null;


    @Override
    public void init() {
        storage = StorageOptions.defaultInstance().service();
        datastore = DatastoreOptions.defaultInstance().service();
        /*ObjectifyService.register(Video.class);
        ObjectifyService.register(User.class);
        ObjectifyService.begin();*/
    }


    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        ConversionRequest cr = readRequest(request);

        List<Acl> acls = new ArrayList<>();
        acls.add(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));

        byte[] videoData = new byte[1024*cr.getDuration()];

        for(int i = 0 ; i < 1024*cr.getDuration() ; i++)
            videoData[i] = '0';

        //on store les data de la vidéo (pour pas les garder en queue inutilement)
        Blob blob = storage.create(
                        BlobInfo.builder(BUCKET_NAME, cr.getName()).acl(acls).build(),
                        new ByteArrayInputStream(videoData));

        /*
        // récupère l'utilisateur qui demande la conversion
        List<User> users = ObjectifyService.ofy()
                .load()
                .type(User.class)
                .filter("userId", cr.getMailAddress())
                .list();

        User user = null;
        if (users.size() == 1)
            user = users.get(0);
        else // si il n'existe pas on créé un utilisateur bronze lambda
            user = new User();*/

        User user = new User();
        user.setId(cr.getMailAddress());
        user.setSla(cr.getSla());
        ObjectifyService.ofy().save().entity(user).now();
        Queue queue = null;

        if (user.getSla() == SLA.BRONZE)
            queue = QueueFactory.getQueue("bronze-queue");
        else
            queue = QueueFactory.getQueue("ar-gold-queue");

        int i = 0;
        // découpe le requête (une pour chaque format demandé)
        for (FORMAT format : cr.getConvertTypes()) {
            Video video = new Video();
            video.setName(cr.getName()+"."+format.toString());
            video.setUserId(cr.getMailAddress());
            video.setDuration(cr.getDuration());
            video.setSla(user.getSla());
            video.setBlobName(blob.blobId().name());
            video.setBucketName(blob.blobId().bucket());
            video.setUser(Key.create(User.class, user.getId()));
            video.setFormat(format);
            video.setStatus(STATUS.PENDING);

            i++;
            if (video.getSla() == SLA.BRONZE) {
                video.setStatus(STATUS.CONVERTING);
                ObjectifyService.ofy().save().entity(video).now();
                queue.add(TaskOptions.Builder.withUrl("/worker").param("video", video.getName()).param("user", video.getUserId()));
            }
            else { // le worker ArgentGoldServlet est celui qui gère les SLA argent et or

                //ObjectifyService.ofy().save().entity(video).now();
                // liste de video en traitement pour user
                List<Video> inQueueVideos = ObjectifyService.ofy().cache(false)
                        .load()
                        .type(Video.class)
                        .ancestor(ObjectifyService.ofy().load().key(Key.create(User.class, video.getUserId())).now())
                        .filter("status", "CONVERTING").list();


                video.setDate(new Date());
                // mise en pending
                if(video.getSla() == SLA.ARGENT && inQueueVideos.size() >= 3 ||
                        inQueueVideos.size() >= 5)
                {
                    video.setStatus(STATUS.PENDING);
                    ObjectifyService.ofy().save().entity(video).now();
                }
                else // triater directement
                {
                    video.setStatus(STATUS.CONVERTING);
                    ObjectifyService.ofy().save().entity(video).now();
                    // Add the task to the default queue.
                    queue.add(TaskOptions.Builder.withUrl("/worker").param("video", video.getName()).param("user", video.getUserId()));
                }
            }

        }

    }

    private  ConversionRequest readRequest(HttpServletRequest request)
    {
        ConversionRequest newRequest = new ConversionRequest();

        // First building the ConversionRequest
        String mailAddress = request.getParameter("mail");
        newRequest.setMailAddress(mailAddress);

        SLA sla = SLA.BRONZE;

        switch ( request.getParameter("sla")){
            case "bronze":
                sla = SLA.BRONZE;
                break;
            case "argent":
                sla = SLA.ARGENT;
                break;
            case "gold":
                sla = SLA.GOLD;
                break;
        }

        newRequest.setSla(sla);

        String name = request.getParameter("name");
        newRequest.setName(name);

        Integer duration = Integer.parseInt(request.getParameter("duration"));
        newRequest.setDuration(duration);

        if(("aviConvert").equals(request.getParameter("avi"))){
            newRequest.addConvertType(FORMAT.AVI);
        }
        if(("mpegConvert").equals(request.getParameter("mpeg"))){
            newRequest.addConvertType(FORMAT.MPEG4);
        }
        if(("mkvConvert").equals(request.getParameter("mkv"))){
            newRequest.addConvertType(FORMAT.MKV);
        }
        if(("oggConvert").equals(request.getParameter("ogg"))){
            newRequest.addConvertType(FORMAT.OGG);
        }
        if(("flvConvert").equals(request.getParameter("flv"))){
            newRequest.addConvertType(FORMAT.FLV);
        }

        return newRequest;
    }

}
