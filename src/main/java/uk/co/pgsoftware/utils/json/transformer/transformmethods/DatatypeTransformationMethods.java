package uk.co.pgsoftware.utils.json.transformer.transformmethods;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import uk.co.pgsoftware.utils.json.transformer.TransformationContext;
import uk.co.pgsoftware.utils.json.transformer.TransformationException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static uk.co.pgsoftware.utils.json.transformer.transformmethods.TransformationUtils.resolveJsonPath;

/**
 * Created by partha ghosh on 14/04/2016.
 */
public class DatatypeTransformationMethods implements TransformationMethod {
    {
        registeredMethods.put("num",this::convertToNumber);
        registeredMethods.put("bool",this::convertToBoolean);
        registeredMethods.put("date",this::convertDateValue);
    }

    public JsonElement convertToNumber(JsonObject jsonFragment, TransformationContext transformationContext){
        String valueToConvert = resolveValue(jsonFragment.getAsJsonPrimitive("value").getAsString(),transformationContext);
        try{
            long longValue = Long.parseLong(valueToConvert);
            return new JsonPrimitive(longValue);
        }catch (NumberFormatException nfe){
            throw new TransformationException("Exception raised when converting to number",nfe);
        }
    }

    public JsonElement convertToBoolean(JsonObject jsonFragment, TransformationContext transformationContext){
        String valueToConvert = resolveValue(jsonFragment.getAsJsonPrimitive("value").getAsString(),transformationContext);
        boolean booleanValue = Boolean.parseBoolean(valueToConvert);
        return new JsonPrimitive(booleanValue);
    }

    public JsonElement convertDateValue(JsonObject jsonFragment, TransformationContext transformationContext){
        String valueToConvert = resolveValue(jsonFragment.getAsJsonPrimitive("value").getAsString(),transformationContext);

        if(valueToConvert == null)
            return JsonNull.INSTANCE;
        String sourceFormat = jsonFragment.getAsJsonPrimitive("sourceFormat").getAsString();
        String targetFormat = jsonFragment.getAsJsonPrimitive("targetFormat").getAsString();

        Date convertedDate;
        if(sourceFormat.equals("EPOCH")){
            long dateInMillisecondsSinceEpoch = Long.parseLong(valueToConvert);
            convertedDate = new Date(dateInMillisecondsSinceEpoch);
        }else{
            try {
                convertedDate = new SimpleDateFormat(sourceFormat).parse(valueToConvert);
            } catch (ParseException pe) {
                throw new TransformationException("Exception raised when converting string value "+valueToConvert+" using "+sourceFormat+" to date.",pe);
            }
        }

        String convertedStringValue;
        if(targetFormat.equals("EPOCH")){
            convertedStringValue = convertedDate.getTime()+"";
        }else{
            convertedStringValue = new SimpleDateFormat(targetFormat).format(convertedDate);
        }
        return new JsonPrimitive(convertedStringValue);
    }

    private String resolveValue(String valueData,TransformationContext context){
        if(valueData.matches("^[^:]*:.*$")){
            JsonElement value = resolveJsonPath(valueData,context);
            if(value==null)
                return null;
            return value.getAsString();
        }else{
            return valueData;
        }
    }
}
