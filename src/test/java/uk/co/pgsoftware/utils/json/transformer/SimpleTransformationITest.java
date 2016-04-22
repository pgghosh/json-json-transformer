package uk.co.pgsoftware.utils.json.transformer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.Before;
import org.junit.Test;

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

        transformationSpecFile = "src/test/resources/simpleTransformationTest/singleSourceTransform.json";
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
