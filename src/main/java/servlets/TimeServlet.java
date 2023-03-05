package servlets;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;
import utils.TimeUtils;

@WebServlet(value = "/time")
public class TimeServlet extends HttpServlet {

  final private String DEFAULT_TIME_ZONE = "UTC";
  final private String LAST_TIME_ZONE_COOKIES_NAME = "lastTimezone";
  final private String TIMEZONE_PARAMETR_NAME = "timezone";

  private TemplateEngine engine;

  @Override
  public void init(ServletConfig config) throws ServletException {
    engine = new TemplateEngine();

    JakartaServletWebApplication app = JakartaServletWebApplication.buildApplication(
        config.getServletContext());

    WebApplicationTemplateResolver resolver =
        new WebApplicationTemplateResolver(app);
    resolver.setPrefix("/WEB-INF/templates/");
    resolver.setSuffix(".html");
    resolver.setTemplateMode("HTML5");
    resolver.setCacheable(false);
    engine.addTemplateResolver(resolver);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    ZoneId zoneId = null;
    if (req.getParameterMap().containsKey(TIMEZONE_PARAMETR_NAME)) {
      zoneId = getZoneIdFromUserQuery(req.getParameter(TIMEZONE_PARAMETR_NAME));
      resp.addCookie(new Cookie(LAST_TIME_ZONE_COOKIES_NAME, zoneId.toString()));
    } else {
      zoneId = getZoneIdFromCookies(req);
    }

    String timezone = getDateTimeByZoneId(zoneId);
    Map<String, Object> params = new HashMap<>();
    params.put(TIMEZONE_PARAMETR_NAME, timezone);
    Context simpleContext = new Context(
        req.getLocale(),
        Map.of("params", params)
    );

    resp.setContentType("text/html");
    engine.process(TIMEZONE_PARAMETR_NAME, simpleContext, resp.getWriter());
    resp.getWriter().close();
  }

  private ZoneId getZoneIdFromUserQuery(String userTimeZone) {
    ZoneId zoneId = TimeUtils.getTimeZone(userTimeZone);
    if (zoneId == null) {
      zoneId = ZoneId.of(DEFAULT_TIME_ZONE);
    }
    return zoneId;
  }

  private ZoneId getZoneIdFromCookies(HttpServletRequest req) {
    ZoneId zoneId = ZoneId.of(DEFAULT_TIME_ZONE);
    Map<String, String> cookies = getCookies(req);
    if (cookies.containsKey(LAST_TIME_ZONE_COOKIES_NAME)) {
      zoneId = getZoneIdFromUserQuery(cookies.get(LAST_TIME_ZONE_COOKIES_NAME));
    }
    return zoneId;
  }

  private String getDateTimeByZoneId(ZoneId zoneId) {
    Clock clock = Clock.system(zoneId);
    return LocalDateTime.now(clock).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
        " " + zoneId;
  }

  private Map<String, String> getCookies(HttpServletRequest req) {
    String cookies = req.getHeader("Cookie");

    if (cookies == null) {
      return Collections.emptyMap();
    }

    Map<String, String> result = new HashMap<>();
    String[] separateCookies = cookies.split(";");
    for (String pair : separateCookies) {
      String[] keyValue = pair.split("=");
      result.put(keyValue[0], keyValue[1]);
    }
    return result;
  }
}
