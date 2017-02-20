package uk.co.pgsoftware.utils.json.transformer.transformmethods;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import uk.co.pgsoftware.utils.json.transformer.TransformationContext;
import uk.co.pgsoftware.utils.json.transformer.TransformationFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Created by pair on 07/04/2016.
 */
public interface TransformationMethod {

    String DEFAULT_TRANSFORMATION_METHOD="default";
    Map<String,BiFunction<JsonObject,TransformationContext,JsonElement>> registeredMethods = new HashMap<>();
    default void registerMethod(){
        registeredMethods.entrySet().
                forEach(entry -> TransformationFactory.registerMethod(entry.getKey(),entry.getValue()));
    }

}
