package com.sky.search.transformer.json;

import com.google.gson.*;
import com.sky.search.transformer.json.transformmethods.ArrayTransformationMethod;
import com.sky.search.transformer.json.transformmethods.DatatypeTransformationMethods;
import com.sky.search.transformer.json.transformmethods.GenericTransformationMethods;
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

    @Before
    public void setUp(){
        new GenericTransformationMethods().registerMethod();
        new ArrayTransformationMethod().registerMethod();
        new DatatypeTransformationMethods().registerMethod();

        transformationContext = new TransformationContext();

        jsonParser = new JsonParser();
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Test
    public void simpleTransformationFromSingleInputSourceDoc() throws Exception {

        String transformationSpecFile = "src/test/resources/simpleTransformationTest/singleSourceTransform.json";
        JsonObject transformationSpec = jsonParser.parse(new FileReader(transformationSpecFile)).getAsJsonObject();

        JsonArray inputs = transformationSpec.get("inputs").getAsJsonArray();

        inputs.forEach(obj -> addToContext(obj.getAsJsonObject()));

        JsonObject output = transformationSpec.getAsJsonObject("output");

        JsonElement transformedObject = TransformationFactory.getTranformationMethod("default").apply(output,transformationContext);

        System.out.println("TransformedJSON : ");
        System.out.println(gson.toJson(transformedObject));

    }

    private void addToContext(JsonObject inputSpecJson){

        String contextName = inputSpecJson.get("ref").getAsString();
        String provider = inputSpecJson.get("provider").getAsString();
        JsonObject args = inputSpecJson.get("args").getAsJsonObject();

        if(provider.equals("FILE")){
                transformationContext.setJsonRef(contextName,getJsonObjectFromFile(args));
        }

    }

    private JsonObject getJsonObjectFromFile(JsonObject args){
        String fileName = args.get("file").getAsString();
        try {
            FileReader json = new FileReader(fileName);
            return jsonParser.parse(json).getAsJsonObject();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void simpleTransformationFromMultipleInputSourceDocuments(){

    }

    @Test
    public void simpleTransformationToTargetDocWithArrayValueFromInput() throws Exception{
        String transformationSpecFile = "src/test/resources/simpleTransformationTest/singleSourceTransformWithArrayDirective.json";
        JsonObject transformationSpec = jsonParser.parse(new FileReader(transformationSpecFile)).getAsJsonObject();

        JsonArray inputs = transformationSpec.get("inputs").getAsJsonArray();

        inputs.forEach(obj -> addToContext(obj.getAsJsonObject()));

        JsonObject output = transformationSpec.getAsJsonObject("output");

        JsonElement transformedObject = TransformationFactory.getTranformationMethod("default").apply(output,transformationContext);

        System.out.println("TransformedJSON : ");
        System.out.println(gson.toJson(transformedObject));

    }
    @Test
    public void simpleTransformationToTargetDocWithArrayValueFromInputWithConstantsInSpec() throws Exception{
        String transformationSpecFile = "src/test/resources/simpleTransformationTest/singleSourceTransformWithArrayDirectiveAndConstants.json";
        JsonObject transformationSpec = jsonParser.parse(new FileReader(transformationSpecFile)).getAsJsonObject();

        JsonArray inputs = transformationSpec.get("inputs").getAsJsonArray();

        inputs.forEach(obj -> addToContext(obj.getAsJsonObject()));

        JsonObject output = transformationSpec.getAsJsonObject("output");

        JsonElement transformedObject = TransformationFactory.getTranformationMethod("default").apply(output,transformationContext);

        System.out.println("TransformedJSON : ");
        System.out.println(gson.toJson(transformedObject));

    }

}
