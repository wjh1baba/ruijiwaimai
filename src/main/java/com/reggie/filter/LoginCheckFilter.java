package com.reggie.filter;


import com.alibaba.fastjson.JSON;
import com.reggie.common.BaseContext;
import com.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.WebFault;
import java.io.IOException;

@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器,支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1.获取本次请求的URI url是uri的一种
        String requestURI = request.getRequestURI();

        //定义不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg", //移动端发送短信
                "/user/login"    //移动登录
        };

        //2.进行路径匹配,判断请求是否需要处理  下面写好了方法
        boolean check = check(urls, requestURI);

        //3.如果不需要处理，直接放行
        if (check){
            log.info("本次请求不需要处理"+requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        //4.判断是否登录状态,如果是登录状态，则直接放行
        if(request.getSession().getAttribute("employee")!= null){
            log.info("用户已登录,用户id为{}",request.getSession().getAttribute("employee"));

            Long id = (Long) request.getSession().getAttribute("employee");
            BaseContext.setThreadLocal(id);

            filterChain.doFilter(request,response);
            return;
        }

        //4.2   移动端 判断是否登录状态,如果是登录状态，则直接放行
        if(request.getSession().getAttribute("user")!= null){
            log.info("用户已登录,用户id为{}",request.getSession().getAttribute("user"));

            Long Userid = (Long) request.getSession().getAttribute("user");
            BaseContext.setThreadLocal(Userid);

            filterChain.doFilter(request,response);
            return;
        }

        log.info("用户未登录");
        //5.如果未登录，则返回未登录的结果给前端,前端进行拦截
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }


    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }
}
