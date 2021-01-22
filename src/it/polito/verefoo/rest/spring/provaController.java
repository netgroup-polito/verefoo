package it.polito.verefoo.rest.spring;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.type.TypeModifier;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import com.fasterxml.jackson.module.jsonSchema.factories.VisitorContext;
import com.fasterxml.jackson.module.jsonSchema.types.ArraySchema;
import com.fasterxml.jackson.module.jsonSchema.types.ObjectSchema;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;
import it.polito.verefoo.rest.spring.repository.DEBUG_Repository;

import com.fasterxml.jackson.databind.jsontype.*;
import com.fasterxml.jackson.databind.cfg.*;
import com.fasterxml.jackson.databind.introspect.*;

@Hidden
@RestController
@RequestMapping(value = "/adp")
public class provaController {

    @Autowired
    DEBUG_Repository debug_Repository;

    Logger loggerInfo = LogManager.getLogger(provaController.class);

    @RequestMapping(value = "/convertPojoToJsonSchemas", method = RequestMethod.POST)
    public void converter() throws IOException, ClassNotFoundException {

        // Incidentally, the false parameter to the SubTypesScanner constructor is essential
        // to include the Object class and therefore correctly performing the getSubTypesOf method
        Reflections reflections = new Reflections(new ConfigurationBuilder()
            .setUrls(ClasspathHelper.forPackage("it.polito.verefoo.jaxb"))
            .setScanners(new SubTypesScanner(false), new TypeAnnotationsScanner()));
        
        /* The classes in the package are scanned through the annotation XmlType; the alternative approach
        * would be to scan all sub-types of Object, but unfortunately that method may throw StackOverflowException,
        * probably due to the considerable number of classes in the package;
        */
        Set<Class<? extends Object>> classes = reflections.getTypesAnnotatedWith(XmlType.class);

        /* List<Class> classes = new ArrayList<>();
        classes.add(ActionTypes.class);
        classes.add(AllocationConstraintType.class);
        classes.add(Antispam.class);
        classes.add(ApplicationError.class);
        classes.add(Cache.class);
        classes.add(Configuration.class);
        classes.add(Connection.class);
        classes.add(Connections.class);
        classes.add(Constraints.class);
        classes.add(Dpi.class);
        classes.add(DpiElements.class);
        classes.add(Elements.class);
        classes.add(Endhost.class);
        classes.add(Endpoint.class);
        classes.add(EType.class);
        classes.add(Fieldmodifier.class);
        classes.add(Firewall.class);
        classes.add(Forwarder.class);
        classes.add(FunctionalTypes.class);
        classes.add(Graph.class);
        classes.add(Graphs.class);
        classes.add(Host.class);
        classes.add(Hosts.class);
        classes.add(HTTPDefinition.class);
        classes.add(Hyperlinks.class);
        classes.add(L4ProtocolTypes.class);
        classes.add(LinkConstraints.class);
        classes.add(Loadbalancer.class);
        classes.add(Mailclient.class);
        classes.add(Mailserver.class);
        classes.add(Nat.class);
        classes.add(Neighbour.class);
        classes.add(NetworkForwardingPaths.class);
        classes.add(NFV.class);
        classes.add(Node.class);
        classes.add(NodeConstraints.class);
        classes.add(NodeRefType.class);
        classes.add(ObjectFactory.class);
        classes.add(Path.class);
        classes.add(Paths.class);
        classes.add(PName.class);
        classes.add(POP3Definition.class);
        classes.add(PriorityFirewall.class);
        classes.add(Property.class);
        classes.add(PropertyDefinition.class);
        classes.add(ProtocolTypes.class);
        classes.add(StatefulFirewall.class);
        classes.add(SupportedVNFType.class);
        classes.add(TrafficMonitor.class);
        classes.add(TypeOfHost.class);
        classes.add(Vpnaccess.class);
        classes.add(Vpnexit.class);
        classes.add(WafElements.class);
        classes.add(WebApplicationFirewall.class);
        classes.add(Webclient.class);
        classes.add(Webserver.class); */

        classes.forEach(pojo -> {
            ObjectMapper mapper = new ObjectMapper();

            // consider also annotations in POJOs generated by JAXB
            // AnnotationIntrospector annotationIntrospector = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
            // AnnotationIntrospector annotationIntrospector = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance()){
            //     @Override
            //     public TypeResolverBuilder<?> findTypeResolver(MapperConfig<?> config,
            //         AnnotatedMember ac, JavaType baseType)
            //     {
            //         if (ac.hasAnnotation(XmlAttribute.class) && ac.getAnnotation(XmlAttribute.class).required()) {
            //             return ;
            //         }
            //         return null;
            //     }
            // }
            // };
            // mapper.setAnnotationIntrospector(annotationIntrospector);
            mapper.enable(MapperFeature.USE_ANNOTATIONS);

            // doesn't write null properties at all
            mapper.setSerializationInclusion(Include.NON_EMPTY);
            // mapper.configure(MapperFeature.USE_ANNOTATIONS, true);
            
            JsonSchemaGenerator schemaGen = buildSchemaGenerator(mapper);
            JsonSchema schema = null;
            try {
                schema = schemaGen.generateSchema(pojo);
                rejectAdditionalProperties(schema);
            } catch (JsonMappingException e) {
                e.printStackTrace();
            }
            
            FileWriter fileWriter;
            try {
                fileWriter = new FileWriter(
                        new File("./entities/version1/jsonSchemas/", pojo.getSimpleName() + ".json"));
                mapper.writerWithDefaultPrettyPrinter().writeValue(fileWriter, schema);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }

    /**
     * Recursive function to set additionalProperties = false for every object
     * inside the json schema
     * @param jsonSchema
     */
    public static void rejectAdditionalProperties(JsonSchema jsonSchema) {
        if (jsonSchema.isObjectSchema()) {
          ObjectSchema objectSchema = jsonSchema.asObjectSchema();
          ObjectSchema.AdditionalProperties additionalProperties = objectSchema.getAdditionalProperties();
          if (additionalProperties instanceof ObjectSchema.SchemaAdditionalProperties) {
              rejectAdditionalProperties(((ObjectSchema.SchemaAdditionalProperties) additionalProperties).getJsonSchema());
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
    public void DEBUG_removeAllNodes() {
        debug_Repository.DEBUG_cleanDb();
    }
}