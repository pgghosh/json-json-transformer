package com.sky.search.transformer.json.transformmethods;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.sky.search.transformer.json.TransformationContext;
import com.sky.search.transformer.json.TransformationException;
import com.sky.search.transformer.json.TransformationFactory;

import java.util.Map;
import java.util.function.BiFunction;

import static com.sky.search.transformer.json.transformmethods.TransformationUtils.resolveJsonPath;

/**
 * Created by partha ghosh on 07/04/2016.
 */
public class GenericTransformationMethods implements TransformationMethod{

    {
        registeredMethods.put("default",this::simpleTransformation);
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
                BiFunction<JsonObject, TransformationContext, JsonElement> transformationMethod = TransformationFactory.getTranformationMethod(methodDirective);
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

//    public JsonElement specialKeyTransformation(JsonObject jsonFragment,TransformationContext transformationContext){
//        return jsonFragment.getAsJsonPrimitive("value");
//    }
}
