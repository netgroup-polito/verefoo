package it.polito.verefoo.rest.spring;

import java.util.ArrayList;
import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.RequestMethod;

public class ResourceWrapperWithLinks<T> {

    List<String> linkStrings = new ArrayList<>();
    List<String> relationships = new ArrayList<>();
    List<String> methods = new ArrayList<>();

    public ResourceWrapperWithLinks<T> addLink(String linkString, String relationship, RequestMethod method) {
        linkStrings.add(linkString);
        relationships.add(relationship);
        methods.add(method.toString());
        return this;
    }
    
    public Resources<T> wrap(T resource) {
        List<T> resources = new ArrayList<>();
        // resource should be null if nothing, besides the links, has to be returned
        if (resource != null) {
            resources.add(resource);
        }
        
        Resources<T> result = new Resources<T>(resources);
        for (int i = 0; i < linkStrings.size(); i++) {
            Link link = new Link(linkStrings.get(i)).withRel(relationships.get(i)).withType(methods.get(i).toString());
            result.add(link);
        }
        return result;
    }
}
