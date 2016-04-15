package com.sky.search.transformer.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Created by Partha Ghosh on 07/04/2016.
 */
public class TransformationFactory {
    private static final Map<String,BiFunction<JsonObject,TransformationContext,JsonElement>> REGISTRY = new HashMap<>();

    public static boolean registerMethod(String directiveName,BiFunction<JsonObject,TransformationContext,JsonElement> transformationMethod){
        return registerMethod(directiveName,transformationMethod,false);
    }

    public static boolean registerMethod(String directiveName,BiFunction<JsonObject,TransformationContext,JsonElement> transformationMethod,boolean overwrite){
        if(!overwrite && REGISTRY.containsKey(directiveName))
            return false;
        REGISTRY.put(directiveName,transformationMethod);
        return true;
    }

    public static BiFunction<JsonObject,TransformationContext,JsonElement> getTranformationMethod(String directiveName){
        return REGISTRY.get(directiveName);
    }
}
