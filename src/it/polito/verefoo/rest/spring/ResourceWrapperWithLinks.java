package it.polito.verefoo.rest.spring;

import java.util.ArrayList;
import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.RequestMethod;

public class ResourceWrapperWithLinks<T> {

    List<String> linkStrings = new ArrayList<>();
    List<String> relationships = new ArrayList<>();
    List<RequestMethod> methods = new ArrayList<>();

    public ResourceWrapperWithLinks<T> addLink(String linkString, String relationship, RequestMethod method) {
        linkStrings.add(linkString);
        relationships.add(relationship);
        methods.add(method);
        return this;
    }
    
    /**
     * @param resource must be null if the response body, besides the links, is empty
     * @return an array containing the passed resource; notice that the response body is always wrapped into
     * an array
     */
    public Resources<T> wrap(T resource) {
        List<T> resources = new ArrayList<>();
        // resource should be null if nothing, besides the links, has to be returned
        if (resource != null) {
            resources.add(resource);
        }
        
        Resources<T> result = new Resources<T>(resources);
        for (int i = 0; i < linkStrings.size(); i++) {
            Link link = new Link(linkStrings.get(i)).withRel(relationships.get(i)).withTitle(buildTitle(methods.get(i)));
            result.add(link);
        }
        return result;
    }

    private String buildTitle(RequestMethod method) {
        String action;
        switch (method) {
            case DELETE:
                action = "delete the";
                break;
            case GET:
                action = "get the";
                break;
            case HEAD:
                action = "get only the response header";
                break;
            case OPTIONS:
                action = "get only the options of the";
                break;
            case PATCH:
                action = "patch the";
                break;
            case POST:
                action = "create a new";
                break;
            case PUT:
                action = "modify the";
                break;
            case TRACE:
                action = "trace the";
                break;
            default:
                action = "custom action on the";
                break;
        }

        return action + " resource";
    }
}
