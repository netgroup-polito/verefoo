package it.polito.verefoo.rest.spring.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.github.victools.jsonschema.generator.Option;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;
import it.polito.verefoo.rest.spring.repository.DEBUG_Repository;

@Hidden
@RestController
@RequestMapping(value = "/adp")
public class DevelopmentController {

    @Autowired
    DEBUG_Repository debug_Repository;


    @RequestMapping(value = "/convertPojoToJsonSchemas", method = RequestMethod.POST)
    public void converter() throws IOException, ClassNotFoundException {

        // Incidentally, the false parameter to the SubTypesScanner constructor is essential
        // to include the Object class and therefore correctly performing the getSubTypesOf method
        Reflections reflections = new Reflections(
                new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage("it.polito.verefoo.jaxb"))
                        .setScanners(new SubTypesScanner(false), new TypeAnnotationsScanner()));
        /*
         * The classes in the package are scanned through the annotation XmlType; the
         * alternative approach would be to scan all sub-types of Object, but
         * unfortunately that method may throw StackOverflowException, probably due to
         * the considerable number of classes in the package;
         */
        Set<Class<? extends Object>> classes = reflections.getTypesAnnotatedWith(XmlType.class);

        // now convert classes into json Schemas
        SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_7,
                OptionPreset.PLAIN_JSON);
        // USE_ANNOTATIONS only considers Json mapping annotations, like @JsonProperty
        configBuilder.getObjectMapper().enable(MapperFeature.USE_ANNOTATIONS);
        configBuilder.getObjectMapper().enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
        configBuilder.getObjectMapper().setSerializationInclusion(Include.NON_EMPTY);
        configBuilder.with(Option.FORBIDDEN_ADDITIONAL_PROPERTIES_BY_DEFAULT);
        SchemaGeneratorConfig config = configBuilder.build();
        SchemaGenerator generator = new SchemaGenerator(config);

        classes.forEach(pojo -> {

            JsonNode jsonSchema = generator.generateSchema(pojo);

            FileWriter fileWriter;
            try {
                fileWriter = new FileWriter(
                        new File("./entities/version1/jsonSchemas/", pojo.getSimpleName() + ".json"));
                configBuilder.getObjectMapper().writerWithDefaultPrettyPrinter().writeValue(fileWriter, jsonSchema);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

    }

    @RequestMapping(value = "/DEBUG_getAllNodes", method = RequestMethod.GET)
    public List<Object> DEBUG_getAllNodes() {
        return debug_Repository.DEBUG_getAllNodes();
    }

    @RequestMapping(value = "/DEBUG_removeAllNodes", method = RequestMethod.DELETE)
    public ResponseEntity<Void> DEBUG_removeAllNodes() {
        debug_Repository.DEBUG_cleanDb();
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}