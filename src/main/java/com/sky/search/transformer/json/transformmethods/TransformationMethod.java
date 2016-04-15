package com.sky.search.transformer.json.transformmethods;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sky.search.transformer.json.TransformationContext;
import com.sky.search.transformer.json.TransformationFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Created by pair on 07/04/2016.
 */
public interface TransformationMethod {
    Map<String,BiFunction<JsonObject,TransformationContext,JsonElement>> registeredMethods = new HashMap<>();
    default void registerMethod(){
        registeredMethods.entrySet().
                forEach(entry -> TransformationFactory.registerMethod(entry.getKey(),entry.getValue()));
    }
}
