package uk.co.pgsoftware.utils.json.transformer.transformmethods;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import uk.co.pgsoftware.utils.json.transformer.TransformationContext;
import uk.co.pgsoftware.utils.json.transformer.TransformationException;

import java.util.ArrayList;
import java.util.List;

import static uk.co.pgsoftware.utils.json.transformer.TransformationFactory.defaultTransformationMethod;
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
    private static final String ARRAY_FILTER_KEY="filter";


    public JsonElement arrayTransformation(JsonObject jsonFragment, TransformationContext transformationContext){

        JsonArray targetJsonArray = new JsonArray();
        evaluateArray(jsonFragment,transformationContext).forEach(targetJsonArray::add);
        return targetJsonArray;

    }

    private List<JsonElement> evaluateArray(JsonObject jsonFragment, TransformationContext transformationContext){
        String sourcePath = jsonFragment.get(ARRAY_SOURCE_PATH).getAsString();
        String sourceRef = jsonFragment.get(ARRAY_SOURCE_REF).getAsString();
        JsonElement filter = jsonFragment.get(ARRAY_FILTER_KEY);

        JsonArray sourceArray = resolveJsonPath(sourcePath,transformationContext).getAsJsonArray();

        JsonElement targetDoc = jsonFragment.get(ARRAY_DOCUMENT_KEY);

        List<JsonElement> evaluatedElements = new ArrayList<>(sourceArray.size());
        boolean processArray=true;
        for(int i=0; i < sourceArray.size(); i++){
            if(filter !=null && !filter.isJsonNull()){
                processArray = evaluateFilter(filter,transformationContext);
            }
            if(processArray){
                evaluatedElements.add(processAndCreateArrayElement(transformationContext, sourceRef, sourceArray.get(i), targetDoc));
            }
        }
        return evaluatedElements;
    }

    private JsonElement processAndCreateArrayElement(TransformationContext transformationContext, String sourceRef, JsonElement sourceElement, JsonElement targetDoc) {
        JsonElement jel;
        if(targetDoc.isJsonPrimitive()){
            jel = resolveJsonPath(targetDoc.getAsString(),transformationContext);
        }else if(targetDoc.isJsonNull() || targetDoc.isJsonArray()) {
            throw new TransformationException("Invalid target Document ref provided.");
        }
        else{
            transformationContext.setJsonRef(sourceRef,sourceElement.getAsJsonObject());
            jel = defaultTransformationMethod.apply(targetDoc.getAsJsonObject(),transformationContext);
            transformationContext.removeJsonRef(sourceRef);
        }
        return jel;
    }

    private boolean evaluateFilter(JsonElement filterArgs, TransformationContext context){

        JsonPrimitive filterResult;
        if(filterArgs.isJsonNull() || filterArgs.isJsonArray()){
            throw new TransformationException("Filter should either be a json primitive or jsonObject which has a directive, that returns a boolean value, as the only element");
        }
        if(filterArgs.isJsonPrimitive()){
            filterResult = resolveJsonPath(filterArgs.getAsString(),context).getAsJsonPrimitive();
        }else{
            filterResult = defaultTransformationMethod.apply(filterArgs.getAsJsonObject(),context).getAsJsonPrimitive();
        }
        if(filterResult==null || !filterResult.isBoolean()){
            throw new TransformationException("Filter Result should be boolean.");
        }

        return filterResult.getAsBoolean();
    }

}
