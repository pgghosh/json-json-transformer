package uk.co.pgsoftware.utils.json.transformer.transformmethods;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import uk.co.pgsoftware.utils.json.transformer.TransformationContext;
import uk.co.pgsoftware.utils.json.transformer.TransformationException;
import uk.co.pgsoftware.utils.json.transformer.TransformationFactory;

import java.util.Map;
import java.util.function.BiFunction;

import static uk.co.pgsoftware.utils.json.transformer.TransformationFactory.getTransformationMethod;
import static uk.co.pgsoftware.utils.json.transformer.transformmethods.TransformationUtils.resolveJsonPath;

/**
 * Created by partha ghosh on 07/04/2016.
 */
public class GenericTransformationMethods implements TransformationMethod{

    {
        registeredMethods.put(DEFAULT_TRANSFORMATION_METHOD,this::simpleTransformation);
        registeredMethods.put("const",this::constantValueTransformation);
    }

    public JsonElement simpleTransformation(JsonObject jsonFragment, TransformationContext transformationContext){
        JsonObject root = new JsonObject();
        for(Map.Entry<String,JsonElement> member : jsonFragment.entrySet()){
            JsonElement el = member.getValue();
            String key = member.getKey();
            if(key.startsWith("$")){ // Check if the key is a directive e.g. $array, $num
                if(!el.isJsonObject()){
                    throw new TransformationException("Value for methodDirective : "+key+" should be a json Object.");
                }
                String methodDirective = key.substring(1);
                BiFunction<JsonObject, TransformationContext, JsonElement> transformationMethod = getTransformationMethod(methodDirective);
                if(transformationMethod == null){
                    throw new TransformationException("Transformation method for directive : "+methodDirective+" not registered or cannot be found.");
                }
                return transformationMethod.apply(el.getAsJsonObject(),transformationContext);
            }
            if(el.isJsonPrimitive()){
                root.add(key,resolveJsonPath(el.getAsString(),transformationContext));
            }else if(el.isJsonObject()){
                root.add(key,simpleTransformation(el.getAsJsonObject(),transformationContext));
            }
        }
        return root;
    }

    public JsonElement constantValueTransformation(JsonObject jsonFragment,TransformationContext transformationContext){
        return jsonFragment.getAsJsonPrimitive("value");
    }

}
