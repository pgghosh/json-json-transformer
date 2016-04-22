package uk.co.pgsoftware.utils.json.transformer.transformmethods;

import com.google.gson.JsonPrimitive;
import uk.co.pgsoftware.utils.json.transformer.TransformationException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Stream;

/**
 * Created by partha ghosh on 21/04/2016.
 */
public class ConditionalOperations {
    enum OPERATOR{
        EQUAL("=",ConditionalOperations::equal);

        private String symbol;
        private BiFunction<JsonPrimitive,JsonPrimitive,JsonPrimitive> method;

        OPERATOR(String symbol,BiFunction<JsonPrimitive,JsonPrimitive,JsonPrimitive> method){
            this.symbol = symbol;
            this.method = method;
        }
        public static OPERATOR getOperator(final String symbol){
            Optional<OPERATOR> operator = Stream.of(OPERATOR.values()).filter(op -> op.symbol.equals(symbol)).findFirst();
            if(operator.isPresent())
                return operator.get();
            else
                return null;
        }
        public BiFunction<JsonPrimitive,JsonPrimitive,JsonPrimitive> getMethod(){
            return method;
        }
    }

    public static JsonPrimitive equal(JsonPrimitive lhs,JsonPrimitive rhs){
        boolean result=false;
        if(lhs.isString()){
            result = lhs.getAsString().equals(rhs.getAsString());
        }else if(lhs.isBoolean()){
            boolean lhsVal = lhs.getAsBoolean();
            boolean rhsVal = rhs.getAsBoolean();
            result = (lhsVal == rhsVal);
        }else if(lhs.isNumber()){
            result = lhs.getAsNumber().equals(rhs.getAsNumber());
        }
        return new JsonPrimitive(result);
    }

    public static JsonPrimitive greaterThan(JsonPrimitive lhs,JsonPrimitive rhs){
        boolean result=false;
        if(lhs.isString() || lhs.isBoolean()){
            throw new TransformationException("greater than operator is only available for numbers");
        }else if(lhs.isNumber()){
            BigDecimal lhsValue = lhs.getAsBigDecimal();
            BigDecimal rhsValue = rhs.getAsBigDecimal();
            result = lhsValue.compareTo(rhsValue)>0;
        }
        return new JsonPrimitive(result);
    }

    public static JsonPrimitive lessThan(JsonPrimitive lhs,JsonPrimitive rhs){
        boolean result=false;
        if(lhs.isString() || lhs.isBoolean()){
            throw new TransformationException("greater than operator is only available for numbers");
        }else if(lhs.isNumber()){
            BigDecimal lhsValue = lhs.getAsBigDecimal();
            BigDecimal rhsValue = rhs.getAsBigDecimal();
            result = lhsValue.compareTo(rhsValue)<0;
        }
        return new JsonPrimitive(result);
    }

}
