package uk.co.pgsoftware.utils.json.transformer.transformmethods;

import com.google.gson.*;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import uk.co.pgsoftware.utils.json.transformer.TransformationContext;
import uk.co.pgsoftware.utils.json.transformer.TransformationSpec;
import uk.co.pgsoftware.utils.json.transformer.Transformer;

import java.io.FileReader;

public class ArrayTransformationMethodTest {
    private static final String TEST_FILES_DIR="src/test/resources/transformationMethodsTestFiles/arrayTransformation";
    private static final String INPUT_JSON=TEST_FILES_DIR+"/nestedArray02.json";
    private static final String SINGLE_LEVEL_TRANSFORMATION_JSON=TEST_FILES_DIR+"/singleLevelArrayTransformation.json";
    private static final String SINGLE_LEVEL_TRANSFORMATION_RESULT_JSON=TEST_FILES_DIR+"/singleLevelArrayTransformationResult.json";
    private static final String MULTI_LEVEL_TRANSFORMATION_JSON=TEST_FILES_DIR+"/multiLevelArrayTransformation.json";
    private static final String MULTI_LEVEL_TRANSFORMATION_RESULT_JSON=TEST_FILES_DIR+"/multiLevelArrayTransformationResult.json";

    private JsonParser jsonParser = new JsonParser();
    private Gson gson = new GsonBuilder().create();

    @Before
    public void setUp(){
    }
    @Test
    public void testArrayTransformationWithSingleLevelLoop() throws Exception {

        testTransformation("series", INPUT_JSON, SINGLE_LEVEL_TRANSFORMATION_JSON, SINGLE_LEVEL_TRANSFORMATION_RESULT_JSON);
    }

    private void testTransformation(String inputJsonRef, String inputJson, String transformationJson, String transformationResultJson) throws Exception {
        TransformationSpec transformationSpec = new Transformer().initializeTransformationSpec(transformationJson);

        TransformationContext transformationContext = transformationSpec.getTransformationContext();
        transformationContext.loadJsonFile(inputJsonRef, inputJson);

        JsonElement result = transformationSpec.transform();

        System.out.println(gson.toJson(result));
        JsonElement expectedJson = createJsonObjectFromFile(transformationResultJson);

        JSONAssert.assertEquals(gson.toJson(expectedJson),gson.toJson(result),false);
    }

    @Test
    public void testArrayTransformationWithMultiLevelLoop() throws Exception{
        testTransformation("series",INPUT_JSON,MULTI_LEVEL_TRANSFORMATION_JSON,MULTI_LEVEL_TRANSFORMATION_RESULT_JSON);

    }

    private JsonElement createJsonObjectFromFile(String inputFile) throws Exception {
        FileReader fileReader = new FileReader(inputFile);
        return jsonParser.parse(fileReader);
    }
}
