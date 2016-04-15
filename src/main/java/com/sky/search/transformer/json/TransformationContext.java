package com.sky.search.transformer.json;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pair on 07/04/2016.
 */
public class TransformationContext {

    Map<String,JsonObject> inputJsons = new HashMap<>();

    public void loadJsonFile(String jsonRef,String fileName) throws IOException {

        FileReader fileReader = new FileReader(fileName);

        loadJson(jsonRef,fileReader);

    }
    public void loadJson(String jsonRef,Reader json){

        JsonParser parser = new JsonParser();
        setJsonRef(jsonRef,parser.parse(json).getAsJsonObject());
    }

    public void setJsonRef(String jsonRef,JsonObject jsonObj){
        inputJsons.put(jsonRef,jsonObj);
    }

    public void removeJsonRef(String jsonRef){
        inputJsons.remove(jsonRef);
    }
    public JsonObject getJson(String jsonRef){
        return inputJsons.get(jsonRef);
    }

}
