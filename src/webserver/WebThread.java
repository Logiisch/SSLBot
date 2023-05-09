package webserver;

import commands.cmdLink;
import spark.Request;
import util.SteamConnector;

import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.halt;

public class WebThread implements Runnable{
    private static final String BG_LINK = "https://cdn.akamai.steamstatic.com/steam/apps/326460/page_bg_generated_v6b.jpg";

    private final SteamOpenID openid = new SteamOpenID();

    private String getFullUrl(Request request, String path) {
        StringBuilder builder = new StringBuilder(request.host());
        builder.insert(0, "http://");
        builder.append(path);
        return builder.toString().replace(":443/","/");
    }

    @Override
    public void run() {
        get("/login",(request,response)-> {

            Map<String, String[]> args = request.queryMap().toMap();

            if (args.containsKey("loginid")) {
                request.session(true).attribute("loginid",args.get("loginid")[0]);
            }

            response.redirect(openid.login(getFullUrl(request, "/auth")));
            // We should never return here.
            // The OpenID login provider should take us somewhere else!
            halt(403, "Go Away!");
            return null;

        });
        get("/logout",(request,response)-> {

                request.session(true).removeAttribute("steamid");
                response.redirect(getFullUrl(request, "/"));
                return null;
        });
        get("/auth",(request,response)-> {
            String userID = openid.verify(request.url(), request.queryMap().toMap());
                String errorUrl = SteamOpenID.removePort(getFullUrl(request, "/error"));

                if (userID == null) {
                    response.redirect(errorUrl);
                }
                /*request.session(true).attribute("steamid", user);
                response.redirect(fullUrl);*/
            String loginid = request.session(true).attribute("loginid");
            request.session(true).removeAttribute(loginid);

            if(loginid != null) {
                String discordid = cmdLink.getUserID(loginid);
                if (discordid.length()>0) cmdLink.linkUser(userID,discordid);
            }

            String code = CodeHandler.addCode(userID);
            String link = SteamOpenID.removePort(getFullUrl(request,"/success?code="+code+"&userid="+userID));
            response.redirect(link);


                return null;

        });

        get("/success", (request, response) -> {
            Map<String,String[]> args = request.queryMap().toMap();
            if (!args.containsKey("code") || !args.containsKey("userid")) response.redirect(getFullUrl(request,"/error"));

            String code = args.get("code")[0];
            String userID = args.get("userid")[0];
            String username = SteamConnector.getName(userID);

            return "<body style=\"background-image: url('" + BG_LINK + "'); color:white\">" +
                    "<h1 style=\"text-align:center\">Welcome " + username + "!</h1>" +
                    "You should have received a Discord DM. Please check your inbox.<br>" +
                    "If not, please go back to Discord and use the command:" +
                    "<h2><b>/link " + code + "<b></h2>" +
                    "Don't share this code!<br>" +
                    "Not your account? <a href=\"/login\">Try again!</a>" +
                    "</body>";

        });

        get("/error", (request, response) -> "<body style=\"background-image: url('" + BG_LINK + "'); color:white\">" +
                "<p style=\"text-align:center\">" +
                "<h1 style=\"color:red\">Error</h1>" +
                "<h2>Couldn't link your account! Please <a href=\"/login\">try again!</a></h2>" +
                "</p>" +
                "</body>");
    }
}
