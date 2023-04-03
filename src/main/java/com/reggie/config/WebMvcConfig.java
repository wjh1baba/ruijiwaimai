package com.reggie.config;

import com.reggie.common.JacksonObjectMapper;
import com.reggie.controller.interceptor.ProjectInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.List;


@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    /**
     * 设置静态资源映射
     * @param registry
     */

    @Autowired
    private ProjectInterceptor projectInterceptor;


    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry){
        log.info("开始进行静态资源映射");
        //前面是前端路径，后面是映射位置
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }


//本来想把过滤器改成拦截器，遇到了问题  ，暂时作废
//    @Override
//    protected void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(projectInterceptor)
//                .addPathPatterns("/books")
//                .excludePathPatterns(
//                        "/employee/login",
//                        "/employee/logout",
//                        "/backend/**",
//                        "/front/**"
//                );
//    }


    //扩展mvc框架的消息转换器
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象消息转换器，底层使用Jackson将Java对象转化成json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //把设置的消息转换器对象追加到mvc框架的转换容器集合中
        converters.add(0,messageConverter);
    }
}
