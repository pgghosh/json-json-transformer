package uk.co.pgsoftware.utils.json.transformer;

import com.google.gson.*;
import uk.co.pgsoftware.utils.json.transformer.transformmethods.ArrayTransformationMethod;
import uk.co.pgsoftware.utils.json.transformer.transformmethods.DatatypeTransformationMethods;
import uk.co.pgsoftware.utils.json.transformer.transformmethods.GenericTransformationMethods;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Created by partha on 11/04/2016.
 */
public class SimpleTransformationITest {

    private TransformationContext transformationContext;

    private JsonParser jsonParser;

    private Gson gson;
    private String transformationSpecFile;

    @Before
    public void setUp(){
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Test
    public void simpleTransformationFromSingleInputSourceDoc() throws Exception {

        transformationSpecFile = "src/test/resources/simpleTransformationTest/singleSourceTransformWithArrayDirective.json";
        JsonElement transformedObject = performTransformation();
        System.out.println("TransformedJSON : ");
        System.out.println(gson.toJson(transformedObject));

    }

    @Test
    public void simpleTransformationFromMultipleInputSourceDocuments(){

    }

    @Test
    public void simpleTransformationToTargetDocWithArrayValueFromInput(){

        transformationSpecFile = "src/test/resources/simpleTransformationTest/singleSourceTransformWithArrayDirective.json";
        JsonElement transformedObject = performTransformation();
        System.out.println("TransformedJSON : ");
        System.out.println(gson.toJson(transformedObject));

    }
    @Test
    public void simpleTransformationToTargetDocWithArrayValueFromInputWithConstantsInSpec(){

        transformationSpecFile = "src/test/resources/simpleTransformationTest/singleSourceTransformWithArrayDirectiveAndConstants.json";
        JsonElement transformedObject = performTransformation();
        System.out.println("TransformedJSON : ");
        System.out.println(gson.toJson(transformedObject));

    }

    private JsonElement performTransformation() {
        TransformationSpec transformationSpec = new Transformer().initializeTransformationSpec(transformationSpecFile);
        return transformationSpec.transform();
    }

}
