package it.polito.verefoo.rest.spring;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer {
  
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        final Jaxb2RootElementHttpMessageConverter  conv = new Jaxb2RootElementHttpMessageConverter ();
        converters.add(conv);
        converters.add(new MappingJackson2HttpMessageConverter());
    }
      
   
}
