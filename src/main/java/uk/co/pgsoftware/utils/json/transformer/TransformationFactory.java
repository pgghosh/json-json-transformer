package uk.co.pgsoftware.utils.json.transformer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import uk.co.pgsoftware.utils.json.transformer.inputproviders.TransformationInputProvider;
import uk.co.pgsoftware.utils.json.transformer.transformmethods.TransformationMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static uk.co.pgsoftware.utils.json.transformer.transformmethods.TransformationMethod.DEFAULT_TRANSFORMATION_METHOD;

/**
 * Created by Partha Ghosh on 07/04/2016.
 */
public class TransformationFactory {
    private static final Map<String,BiFunction<JsonObject,TransformationContext,JsonElement>> REGISTRY = new HashMap<>();
    private static final Map<String,TransformationInputProvider> TRANSFORMATION_INPUT_PROVIDER_MAP = new HashMap<>();

    public static BiFunction<JsonObject,TransformationContext,JsonElement> defaultTransformationMethod;

    private static boolean init=false;

    static void init(){
        new FastClasspathScanner("uk.co.pgsoftware.utils.json.transformer")
                .matchClassesImplementing(TransformationMethod.class, c -> registerTransformationMethods(c))
                .scan();
        new FastClasspathScanner("uk.co.pgsoftware.utils.json.transformer")
                .matchClassesImplementing(TransformationInputProvider.class,c -> registerInputProvider(c))
                .scan();
        defaultTransformationMethod = getTransformationMethod(DEFAULT_TRANSFORMATION_METHOD);
        init = true;
    }

    private static void registerTransformationMethods(Class<? extends TransformationMethod> transMethodClass){
        try {
            transMethodClass.newInstance().registerMethod();
        } catch (Exception e) {
            throw new TransformationException("Error during registration of transformation methods.",e);
        }
    }

    private static void registerInputProvider(Class<? extends TransformationInputProvider> inputProviderClass){
        try {
            TransformationInputProvider inputProvider = inputProviderClass.newInstance();
            String providerKey = inputProvider.PROVIDER_KEY[0];
            if(providerKey != null && !providerKey.equals("")){
                registerTransformationInputProvider(providerKey,inputProvider);
            }
        } catch (Exception e) {
            throw new TransformationException("Error during registration of transformation methods.",e);
        }
    }
    static boolean isInit(){return init;}

    public static boolean registerMethod(String directiveName,BiFunction<JsonObject,TransformationContext,JsonElement> transformationMethod){
        return registerMethod(directiveName,transformationMethod,false);
    }

    public static boolean registerMethod(String directiveName,BiFunction<JsonObject,TransformationContext,JsonElement> transformationMethod,boolean overwrite){
        if(!overwrite && REGISTRY.containsKey(directiveName))
            return false;
        REGISTRY.put(directiveName,transformationMethod);
        return true;
    }

    public static BiFunction<JsonObject,TransformationContext,JsonElement> getTransformationMethod(String directiveName){
        return REGISTRY.get(directiveName);
    }

    public static void registerTransformationInputProvider(String providerName,TransformationInputProvider provider){
        TRANSFORMATION_INPUT_PROVIDER_MAP.put(providerName,provider);
    }

    public static TransformationInputProvider getTransformationInputProvider(String providerName){
        return TRANSFORMATION_INPUT_PROVIDER_MAP.get(providerName);

    }
}
