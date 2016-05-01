package uk.co.pgsoftware.utils.json.transformer.transformmethods;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import uk.co.pgsoftware.utils.json.transformer.TransformationContext;
import uk.co.pgsoftware.utils.json.transformer.TransformationException;
import uk.co.pgsoftware.utils.json.transformer.transformmethods.ConditionalOperations.OPERATOR;

import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by partha ghosh on 21/04/2016.
 */
public class ConditionalTransformationMethods implements TransformationMethod{
    {
        registeredMethods.put("cond",this::compare);
        registeredMethods.put("in",this::findInArray);
    }

    // Error Messages
    private static final String OPERATORS= Stream.of(OPERATOR.values()).map(OPERATOR::getSymbol).collect(Collectors.joining(","));
    private static final String USAGE="Usage : <JsonPath><Operator><JsonPath>.\nWhere : \n\t\tJsonPath : dot separated path\n\t\tOperator : Any of "+OPERATORS;


    private static final String JSON_PATH_PATTERN_STRING="[A-z0-9\\.\\-\\_]+";
    private static final Pattern CONDITIONAL_EXPRESSION_PATTERN = Pattern.compile("^("+JSON_PATH_PATTERN_STRING+")([=><!]{1,2})("+JSON_PATH_PATTERN_STRING+")$");
    private static final Pattern CONDITIONAL_IN_EXPRESSION_PATTERN = Pattern.compile("^("+JSON_PATH_PATTERN_STRING+")\\s+[iI][nN]\\s+("+JSON_PATH_PATTERN_STRING+")$");

    public JsonElement compare(JsonObject jsonFragment, TransformationContext transformationContext){

        String conditionalExpression = jsonFragment.getAsJsonPrimitive("expression").getAsString();

        Matcher matcher = CONDITIONAL_EXPRESSION_PATTERN.matcher(conditionalExpression.replaceAll("\\s+",""));
        if(!matcher.matches()){
//            matcher = CONDITIONAL_IN_EXPRESSION_PATTERN.matcher(conditionalExpression);
//            if(matcher.matches()){
//                return evaluateInExpression(matcher,transformationContext);
//            }
            throw new TransformationException("Bad Conditional Expression specified : "+conditionalExpression+"\n"+USAGE);
        }

        if(matcher.find()){
            String lhs = matcher.group(1);
            String operatorSymbol = matcher.group(2);
            String rhs = matcher.group(3);

            JsonElement lhsJson = TransformationUtils.resolveJsonPath(lhs,transformationContext);
            JsonElement rhsJson = TransformationUtils.resolveJsonPath(rhs,transformationContext);

            OPERATOR operator = OPERATOR.getOperator(operatorSymbol);
            if(operator == null){
                throw new TransformationException("Invalid operatopr : "+operatorSymbol+". Should be either of "+OPERATORS);
            }

            JsonPrimitive result = operator.getMethod().apply(lhsJson.getAsJsonPrimitive(), rhsJson.getAsJsonPrimitive());
            if(result == null || !result.isBoolean()){
                throw new TransformationException("Result of operation expression : "+conditionalExpression+" should be boolean. Evaluated to "+result);
            }
            return result;
        }

        return new JsonPrimitive(false);
    }

    /**
     * The findInArray will have one argument of the form "<JsonPath>":"<JsonPath>"|JsonArrayObject.
     * Examples are :
     *       "ref1:blah.foo.bar" : "ref2:my.bar.blah"
     *       "ref1:blah.foo.bar" : ["blah","bar","foo"]
     */
    public JsonElement findInArray(JsonObject args, TransformationContext transformationContext) {
        Set<Entry<String, JsonElement>> members = args.entrySet();
        if(members.size() != 1){ // At this moment we only allow one find expression
            return new JsonPrimitive(false);
        }

        Entry<String, JsonElement> element = members.iterator().next();
        String lhs = element.getKey();
        JsonElement rhs = element.getValue();

        JsonElement lhsJson = TransformationUtils.resolveJsonPath(lhs,transformationContext);
        JsonArray rhsJson;
        if(rhs.isJsonNull() || rhs.isJsonObject()){
            throw new TransformationException("The source object to compare must be an array or a json path primitive that evaluates to an array.");
        }
        if(rhs.isJsonArray()){
            rhsJson = rhs.getAsJsonArray();
        }else{
            JsonElement el = TransformationUtils.resolveJsonPath(rhs.getAsString(), transformationContext);
            if(!el.isJsonArray()){
                throw new TransformationException("The source json path primitive should resolve to an array.");
            }
            rhsJson = el.getAsJsonArray();
        }

        return evaluateIn(lhsJson.getAsJsonPrimitive(),rhsJson);

    }

    private JsonPrimitive evaluateIn(JsonPrimitive lhs,JsonArray rhs){
        JsonPrimitive result = new JsonPrimitive(false);
        for(JsonElement jel:rhs){
            if(!jel.isJsonPrimitive()){
                continue;
            }
            result = ConditionalOperations.equal(lhs,jel.getAsJsonPrimitive());
            if(result.getAsBoolean()){
                break;
            }
        }
        return result;
    }

}
