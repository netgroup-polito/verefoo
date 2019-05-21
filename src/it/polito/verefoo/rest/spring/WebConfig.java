package it.polito.verefoo.rest.spring;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


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
@Configuration
@EnableSwagger2
 class SwaggerConfig {                                    
    @Bean
    public Docket api() { 
        return new Docket(DocumentationType.SWAGGER_2)  
          .select()                                  
          .apis(RequestHandlerSelectors.any())              
          .paths(PathSelectors.any())                          
          .build();                                           
    }
}
