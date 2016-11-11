package com.sacc.servlet;

import com.googlecode.objectify.ObjectifyService;
import com.sacc.entity.User;
import com.sacc.entity.Video;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class DashboardServlet extends HttpServlet {
    @Override
    public void init() {
        ObjectifyService.register(Video.class);
        ObjectifyService.register(User.class);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        User user = ObjectifyService.ofy()
            .load()
            .type(User.class)
            .id(email)
            .now();

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.append("<!DOCTYPE html>");
        out.append("<html lang=\"en\">");
        out.append("<head lang=\"fr\">");
        out.append("<title>Dashboard</title>");
        out.append("<body>");
        out.append("<form method='post'>");
        out.append("<input type='email' name='email' />");
        out.append("<button>Send</button>");
        out.append("</form>");

        if (null != user) {
            List<Video> videos = ObjectifyService.ofy()
                .load()
                .type(Video.class)
                .ancestor(user)
                .list();
            out.append(String.valueOf(videos.size())).append(" videos for ").append(user.getId());
            out.append("<table>");
            out.append("<thead>");
            out.append("<tr><th>Name</th><th>Duration</th><th>Format</th><th>Status</th><th>Link</th></tr>");
            out.append("</thead>");
            out.append("<tbody>");
            for (Video v : videos) {
                String link = "<a href='https://storage.googleapis.com/sacclaude.appspot.com/"+ v.getBlobName() +"."+ v.getFormat() +"'>"+ v.getBlobName() +"</a>";
                out.append("<tr>");
                out.append("<td>").append(v.getName()).append("</td>");
                out.append("<td>").append(String.valueOf(v.getDuration())).append("</td>");
                out.append("<td>").append(v.getFormat().name()).append("</td>");
                out.append("<td>").append(v.getStatus().name()).append("</td>");
                out.append("<td>").append(link).append("</td>");
                out.append("</tr>");
            }
            out.append("<tbody>");
            out.append("</table>");
        } else {
            out.append("User : ").append(email).append(" not found.");
        }

        out.append("</body>");
        out.append("</head>");
        out.append("</html>");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.append("<!DOCTYPE html>");
        out.append("<html lang=\"en\">");
        out.append("<head lang=\"fr\">");
        out.append("<title>Dashboard</title>");
        out.append("<body>");
        out.append("<form method='post'>");
        out.append("<input type='email' name='email' />");
        out.append("<button>Send</button>");
        out.append("</form>");
        out.append("</body>");
        out.append("</head>");
        out.append("</html>");
    }
}
