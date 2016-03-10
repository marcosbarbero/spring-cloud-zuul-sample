package demo.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UrlPathHelper;

import java.security.Principal;

/**
 * @author marcos.barbero
 */
@Component
public class AuthFilter extends ZuulFilter {

    private static Logger LOGGER = LoggerFactory.getLogger(AuthFilter.class);

    private RouteLocator routeLocator;

    private static final UrlPathHelper URL_PATH_HELPER = new UrlPathHelper();

    @Autowired
    public AuthFilter(RouteLocator routeLocator) {
        this.routeLocator = routeLocator;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return getAuthenticatedUser() != null;
    }

    @Override
    public Object run() {
        LOGGER.info("client: {}, accessing service: {}", getAuthenticatedUser().getName(), requestedContext());
        return null;
    }

    private Principal getAuthenticatedUser() {
        return RequestContext.getCurrentContext().getRequest().getUserPrincipal();
    }

    private String requestedContext() {
        final Route route = this.routeLocator.getMatchingRoute(this.requestURI());
        return (route != null) ? route.getId().toLowerCase() : null;
    }

    private String requestURI() {
        return URL_PATH_HELPER.getPathWithinApplication(RequestContext.getCurrentContext().getRequest());
    }
}
