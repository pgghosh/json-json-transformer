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
public class MultiLevelArrayTransformationMethod implements TransformationMethod {

    {
        registeredMethods.put("multiarray",this::arrayTransformation);
    }

    private static final String ARRAY_ITERATOR ="iterateOver";
    private static final String ARRAY_SOURCE_REF ="sourcePath";
    private static final String ARRAY_ITERATOR_ELEMENT="elementRef";
    private static final String ARRAY_DOCUMENT_KEY="document";

    public JsonElement arrayTransformation(JsonObject jsonFragment, TransformationContext transformationContext){

        JsonArray targetJsonArray = new JsonArray();
        buildArray(jsonFragment,transformationContext).forEach(targetJsonArray::add);
        return targetJsonArray;

    }

    private List<JsonElement> buildArray(JsonObject jsonFragment, TransformationContext transformationContext){

        JsonObject iteratorRef = jsonFragment.getAsJsonObject(ARRAY_ITERATOR);
        JsonObject document = jsonFragment.getAsJsonObject(ARRAY_DOCUMENT_KEY);

        return buildArrayDocument(document,transformationContext,iteratorRef);
    }

    private List<JsonElement> buildArrayDocument(JsonObject document,TransformationContext transformationContext,JsonObject iteratorRef){
        String sourcePathRef = iteratorRef.getAsJsonPrimitive(ARRAY_SOURCE_REF).getAsString();
        String iteratorElementRef = iteratorRef.getAsJsonPrimitive(ARRAY_ITERATOR_ELEMENT).getAsString();
        JsonArray sourceArray = resolveJsonPath(sourcePathRef,transformationContext).getAsJsonArray();

        List<JsonElement> jsonElements = new ArrayList<>();
        for(JsonElement source : sourceArray){
            transformationContext.setJsonRef(iteratorElementRef,source.getAsJsonObject());
            JsonObject subIteratorRef = iteratorRef.getAsJsonObject(ARRAY_ITERATOR);
            if(subIteratorRef == null){
                jsonElements.add(defaultTransformationMethod.apply(document,transformationContext));
            }else {
                jsonElements.addAll(buildArrayDocument(document,transformationContext,subIteratorRef));
            }
            transformationContext.removeJsonRef(iteratorElementRef);
        }

        return jsonElements;

    }

}
