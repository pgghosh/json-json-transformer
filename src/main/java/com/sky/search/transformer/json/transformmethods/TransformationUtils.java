package com.sky.search.transformer.json.transformmethods;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sky.search.transformer.json.TransformationContext;
import com.sky.search.transformer.json.TransformationException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Partha on 09/04/2016.
 */
public class TransformationUtils {

    private static final String ARRAY_REGEX="^(\\w+)(?:\\[\\d+\\])+$";
    private static final String ARRAY_INDICES_REGEX="\\[(\\d+)\\]";

    // inputJsonPath spec => <inputJsonRef>:<jsonPathFromRoot>
    public static JsonElement resolveJsonPath(String inputJsonPath, TransformationContext context){

        Pattern pattern = Pattern.compile("^([^:\\s]*):([^:\\s]*)$");
        Matcher matcher = pattern.matcher(inputJsonPath);
        if(!matcher.find()){
            // throw exception
        }
        String jsonRef = matcher.group(1);
        String jsonPath = matcher.group(2);

        JsonObject json = context.getJson(jsonRef);
        String[] subPaths = jsonPath.split("\\.");
        int counter=0;
        JsonElement result=null;
        for(String subPath : subPaths){
            counter++;
            Integer arrayIndex=null;

            if(subPath.matches(ARRAY_REGEX))
                result = resolveArrayPathElement(subPath,json);
            else{
                result = json.get(subPath);
            }
            if(result == null || result.isJsonNull() || result.isJsonPrimitive() || result.isJsonArray())
                break;
            json = (JsonObject)result;
        }

        if(subPaths.length > counter){
            // Do we throw exception or just log a warning, as it could be that the path is non-existent in the source.
            throw new TransformationException("Path resolution stopped at subPath : "+subPaths[counter-1]);
        }
        return result;
    }

    private static JsonElement resolveArrayPathElement(String subPath,JsonObject parent){

        JsonElement result = null;
        Matcher matcher = Pattern.compile(ARRAY_REGEX).matcher(subPath);
        if(matcher.find()){
            String arrayKey = matcher.group(1);
            JsonArray json = parent.get(arrayKey).getAsJsonArray();
            matcher = Pattern.compile(ARRAY_INDICES_REGEX).matcher(subPath);
            while (matcher.find()){
                int index = Integer.parseInt(matcher.group(1));
                result = json.get(index);
                if(result == null || result.isJsonNull() || result.isJsonPrimitive() || result.isJsonObject())
                    break;
                json = result.getAsJsonArray();
            }
        }

        return result;
    }

}
