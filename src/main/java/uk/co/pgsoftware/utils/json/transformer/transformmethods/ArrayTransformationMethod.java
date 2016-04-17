package uk.co.pgsoftware.utils.json.transformer.transformmethods;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import uk.co.pgsoftware.utils.json.transformer.TransformationContext;
import uk.co.pgsoftware.utils.json.transformer.TransformationException;
import uk.co.pgsoftware.utils.json.transformer.TransformationFactory;

import static uk.co.pgsoftware.utils.json.transformer.transformmethods.TransformationUtils.resolveJsonPath;

/**
 * Created by Partha on 09/04/2016.
 */
public class ArrayTransformationMethod implements TransformationMethod {

    {
        registeredMethods.put("array",this::arrayTransformation);
    }

    private static final String ARRAY_SOURCE_PATH ="sourcePath";
    private static final String ARRAY_SOURCE_REF ="elementRef";
    private static final String ARRAY_DOCUMENT_KEY="document";

    public JsonElement arrayTransformation(JsonObject jsonFragment, TransformationContext transformationContext){
        String sourcePath = jsonFragment.get(ARRAY_SOURCE_PATH).getAsString();
        String sourceRef = jsonFragment.get(ARRAY_SOURCE_REF).getAsString();

        JsonArray sourceArray = resolveJsonPath(sourcePath,transformationContext).getAsJsonArray();

        JsonElement targetDoc = jsonFragment.get(ARRAY_DOCUMENT_KEY);

        JsonArray targetJsonArray = new JsonArray();
        for(int i=0; i < sourceArray.size(); i++){
            JsonElement jel;
            if(targetDoc.isJsonPrimitive()){
                jel = resolveJsonPath(targetDoc.getAsString(),transformationContext);
            }else if(targetDoc.isJsonNull() || targetDoc.isJsonArray())
                throw new TransformationException("Invalid target Document ref provided.");
            else{
                transformationContext.setJsonRef(sourceRef,sourceArray.get(i).getAsJsonObject());
                jel = TransformationFactory.getTranformationMethod(DEFAULT_TRANSFORMATION_METHOD)
                        .apply(targetDoc.getAsJsonObject(),transformationContext);
                transformationContext.removeJsonRef(sourceRef);
            }
            targetJsonArray.add(jel);
        }

        return targetJsonArray;

    }
}
