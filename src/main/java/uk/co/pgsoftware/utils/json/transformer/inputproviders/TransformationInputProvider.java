package uk.co.pgsoftware.utils.json.transformer.inputproviders;

import com.google.gson.JsonObject;

/**
 * All provider implementations are available
 * Created by Partha Ghosh on 16/04/2016.
 */
public interface TransformationInputProvider {
    String[] PROVIDER_KEY=new String[1];

    JsonObject loadInputDocument(JsonObject inputArgs);
}
