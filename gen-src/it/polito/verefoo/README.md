## gen-src.it.polito.verefoo package

The gen-src.it.polito.verefoo package contains wrapper classes for the JAXB classes, that are present in the gen-src.it.polito.verefoo.jaxb package.
In case the XML Schemas included in the xsd folder are modified and new JAXB classes are automatically generated, the following steps must be followed by the developers:
* copy each class from gen-src.it.polito.verefoo.jaxb to gen-src.it.polito.verefoo, adding the "Db" prefix to the name;
* add the following piece of code in the class definition, for each class:
<pre><code>
@Id
@GeneratedValue
Long id;
</code></pre>

This classes are needed for the interaction with Neo4j.