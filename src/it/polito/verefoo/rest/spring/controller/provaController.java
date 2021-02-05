package it.polito.verefoo.rest.spring.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import com.fasterxml.jackson.module.jsonSchema.factories.VisitorContext;
import com.fasterxml.jackson.module.jsonSchema.types.ArraySchema;
import com.fasterxml.jackson.module.jsonSchema.types.ObjectSchema;
import com.github.victools.jsonschema.generator.Option;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
public class provaController {

    @Autowired
    DEBUG_Repository debug_Repository;

    Logger loggerInfo = LogManager.getLogger(provaController.class);

    @RequestMapping(value = "/convertPojoToJsonSchemas", method = RequestMethod.POST)
    public void converter() throws IOException, ClassNotFoundException {

        // Incidentally, the false parameter to the SubTypesScanner constructor is
        // essential
        // to include the Object class and therefore correctly performing the
        // getSubTypesOf method
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

    /**
     * Recursive function to set additionalProperties = false for every object
     * inside the json schema
     * 
     * @param jsonSchema
     */
    public static void rejectAdditionalProperties(JsonSchema jsonSchema) {
        if (jsonSchema.isObjectSchema()) {
            ObjectSchema objectSchema = jsonSchema.asObjectSchema();
            ObjectSchema.AdditionalProperties additionalProperties = objectSchema.getAdditionalProperties();
            if (additionalProperties instanceof ObjectSchema.SchemaAdditionalProperties) {
                rejectAdditionalProperties(
                        ((ObjectSchema.SchemaAdditionalProperties) additionalProperties).getJsonSchema());
            } else {
                for (JsonSchema property : objectSchema.getProperties().values()) {
                    rejectAdditionalProperties(property);
                }
                objectSchema.rejectAdditionalProperties();
            }
        } else if (jsonSchema.isArraySchema()) {
            ArraySchema.Items items = jsonSchema.asArraySchema().getItems();
            if (items.isSingleItems()) {
                rejectAdditionalProperties(items.asSingleItems().getSchema());
            } else if (items.isArrayItems()) {
                for (JsonSchema schema : items.asArrayItems().getJsonSchemas()) {
                    rejectAdditionalProperties(schema);
                }
            }
        }
    }

    /*
     * this method overrides the default behaviour (superclass VisitorContext) of
     * the schema generator: The only difference with the default behaviour is that
     * the seenSchemas list is not updated as soon as a JavaType is encountered, so
     * that all references ($ref) to schemas are resolved in place: no pointers are
     * generated, but all sub-schemas are interpolated
     */
    private static JsonSchemaGenerator buildSchemaGenerator(ObjectMapper objectMapper) {
        final SchemaFactoryWrapper schemaFactoryWrapper = new SchemaFactoryWrapper();
        schemaFactoryWrapper.setVisitorContext(new VisitorContext() {

            private final HashSet<JavaType> seenSchemas = new HashSet<JavaType>();

            @Override
            public String addSeenSchemaUri(JavaType aSeenSchema) {
                if (aSeenSchema != null && !aSeenSchema.isPrimitive()) {
                    // seenSchemas.add(aSeenSchema);
                    return javaTypeToUrn(aSeenSchema);
                }
                return null;
            }

            @Override
            public String getSeenSchemaUri(JavaType aSeenSchema) {
                return (seenSchemas.contains(aSeenSchema)) ? javaTypeToUrn(aSeenSchema) : null;
            }

            @Override
            public String javaTypeToUrn(JavaType jt) {
                return "urn:jsonschema:" + jt.toCanonical().replace('.', ':').replace('$', ':');
            }
        });
        return new JsonSchemaGenerator(objectMapper, schemaFactoryWrapper);
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