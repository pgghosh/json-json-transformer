package uk.co.pgsoftware.utils.json.transformer.transformmethods;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import uk.co.pgsoftware.utils.json.transformer.TransformationContext;
import uk.co.pgsoftware.utils.json.transformer.TransformationException;
import uk.co.pgsoftware.utils.json.transformer.transformmethods.ConditionalOperations.OPERATOR;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by partha ghosh on 21/04/2016.
 */
public class ConditionalTransformationMethods implements TransformationMethod{
    {
        registeredMethods.put("cond",this::compare);
    }

    private static final String JSON_PATH_PATTERN_STRING="[A-z0-9\\.\\-\\_]+";
    private static final Pattern CONDITIONAL_EXPRESSION_PATTERN = Pattern.compile("^("+JSON_PATH_PATTERN_STRING+")([=><!]{1,2})("+JSON_PATH_PATTERN_STRING+")$");
    public JsonElement compare(JsonObject jsonFragment, TransformationContext transformationContext){

        String conditionalExpression = jsonFragment.getAsJsonPrimitive("expression").getAsString();

        Matcher matcher = CONDITIONAL_EXPRESSION_PATTERN.matcher(conditionalExpression.replaceAll("\\s+",""));
        if(!matcher.matches()){
            throw new TransformationException("Bad Conditional Expression specified : "+conditionalExpression);
        }

        if(matcher.find()){
            String lhs = matcher.group(1);
            String operatorSymbol = matcher.group(2);
            String rhs = matcher.group(3);

            JsonElement lhsJson = TransformationUtils.resolveJsonPath(lhs,transformationContext);
            JsonElement rhsJson = TransformationUtils.resolveJsonPath(rhs,transformationContext);

            OPERATOR operator = OPERATOR.getOperator(operatorSymbol);
            if(operator == null){
                throw new TransformationException("Invalid operatopr : "+operatorSymbol);
            }

            JsonPrimitive result = operator.getMethod().apply(lhsJson.getAsJsonPrimitive(), rhsJson.getAsJsonPrimitive());
            if(result == null || !result.isBoolean()){
                throw new TransformationException("Result of operation expression : "+conditionalExpression+" should be boolean.");
            }
            return result;
        }

        return new JsonPrimitive(false);
    }

}
