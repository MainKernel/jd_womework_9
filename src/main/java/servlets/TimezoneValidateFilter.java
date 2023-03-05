package servlets;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import utils.TimeUtils;

@WebFilter(value = "/time")
public class TimezoneValidateFilter extends HttpFilter {

  @Override
  protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws IOException, ServletException {
    if (!req.getParameterMap().containsKey("timezone")) {
      chain.doFilter(req, res);
      return;
    }

    if (TimeUtils.validateTimeZone(req.getParameter("timezone"))) {
      chain.doFilter(req, res);
    } else {
      res.setStatus(400);
      res.getWriter().write("Invalid timezone");
      res.getWriter().close();
    }
  }
}
