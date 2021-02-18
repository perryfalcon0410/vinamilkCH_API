package vn.viettel.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PreFilter extends ZuulFilter {

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
        return true;
    }

    @Override
    public Object run() {
        //        RequestContext context = RequestContext.getCurrentContext();
        //        HttpServletRequest request = context.getRequest();
        //        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        //        context.addZuulRequestHeader(HttpHeaders.AUTHORIZATION, authorization);
        return null;
    }
}
