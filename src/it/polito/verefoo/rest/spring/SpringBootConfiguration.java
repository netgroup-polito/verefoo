package it.polito.verefoo.rest.spring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.SessionFactory;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.data.neo4j.conversion.MetaDataDrivenConversionService;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;

// TODO #jalol separate config from rest api
@SpringBootApplication
// @EnableSwagger2
// A client implementation should activate this annotation and use the injected bean RestTemplate, which works as a rest client along with hyperlinks
// @EnableHypermediaSupport(type = HypermediaType.HAL)
public class SpringBootConfiguration {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootConfiguration.class, args);
    }

    public static final String URL = System.getenv("NEO4J_URL") != null ? System.getenv("NEO4J_URL")
            : "http://localhost:7474";

    @Bean
    public Configuration configuration() {
        return new Configuration.Builder().uri(URL).credentials("neo4j", "costLess").verifyConnection(true).build();
    }

    @Bean
    public SessionFactory sessionFactory() {
        return new SessionFactory(configuration(), "it.polito.verefoo.jaxb", "it.polito.verefoo");
    }

    @Bean
    public Neo4jTransactionManager transactionManager() {
        return new Neo4jTransactionManager(sessionFactory());
    }

    @Bean
    public ConversionService conversionService() {
        ConversionService conversionService = new MetaDataDrivenConversionService(sessionFactory().metaData());
        DefaultConversionService.addDefaultConverters((GenericConversionService) conversionService);
        return conversionService;
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            /*
             * System.out.println("Let's inspect the beans provided by Spring Boot:");
             * 
             * String[] beanNames = ctx.getBeanDefinitionNames(); Arrays.sort(beanNames);
             * for (String beanName : beanNames) { System.out.println(beanName); }
             */

        };
    }

    @Bean
    public HttpMessageConverters converters() {
        // This snippet globally instructs Jackson2 to not write null fields in the
        // response, which is the default behaviour
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = mappingJackson2HttpMessageConverter.getObjectMapper();
        // doesn't write null-valued properties in the response
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        // equivalent of setting additionalProperties = false in json schemas; in other words
        // reject every unknown property
        objectMapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);

        return new HttpMessageConverters(false,
                Arrays.asList(mappingJackson2HttpMessageConverter, new Jaxb2RootElementHttpMessageConverter()));
    }

    /*
     * This bean customizes the creation of the openapi UI in Swagger version 3
     */
    @Bean
    public OpenAPI customOpenAPI() {

        List<Tag> tags = new ArrayList<>();
        Tag tag = new Tag();
        tag.setName("version 2");
        tags.add(tag);
        tag = new Tag();
        tag.setName("version 1 - graphs");
        tags.add(tag);
        tag = new Tag();
        tag.setName("version 1 - requirements");
        tags.add(tag);
        tag = new Tag();
        tag.setName("version 1 - simulations");
        tags.add(tag);
        tag = new Tag();
        tag.setName("version 1 - substrates");
        tags.add(tag);

        List<Server> servers = new ArrayList<>();
        Server server = new Server();
        server.setDescription("ADP module server");
        server.setUrl("http://localhost:8085/verefoo/adp");
        servers.add(server);

        return new OpenAPI().components(new Components()).servers(servers)
                .info(new Info().title("Verefoo API documentation")
                        .description("This is the automatically-generated documentation of Verefoo's REST APIs."))
                .tags(tags);
    }

    /*
     * This bean further customizes the creation of the openapi UI in Swagger
     * version 3
     */
    @Bean
    public OpenApiCustomiser sortSchemasAlphabetically() {
        return openApi -> {
            Map<String, Schema> schemas = openApi.getComponents().getSchemas();
            schemas = schemas.entrySet().stream().sorted((entry1, entry2) -> entry1.getKey().compareTo(entry2.getKey()))
                    .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
            openApi.getComponents().setSchemas(new TreeMap<>(schemas));
        };
    }

    /*
     * This bean works for Swagger 2, which has been disabled in favour of Swagger 3
     *
     * @Bean public Docket apiDocket() { return new
     * Docket(DocumentationType.SWAGGER_2) .select()
     * .apis(RequestHandlerSelectors.basePackage("it.polito.verefoo.rest.spring"))
     * .paths(PathSelectors.any()) .build(); }
     */
}