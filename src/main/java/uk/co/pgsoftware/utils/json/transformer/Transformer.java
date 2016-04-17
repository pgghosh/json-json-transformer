package uk.co.pgsoftware.utils.json.transformer;

import static uk.co.pgsoftware.utils.json.transformer.TransformationFactory.init;
import static uk.co.pgsoftware.utils.json.transformer.TransformationFactory.isInit;

/**
 * Created by partha ghosh on 07/04/2016.
 */
public class Transformer {

    public TransformationSpec initializeTransformationSpec(String transformationSpecFile){
        if(!isInit())
            init();
        try {
            return new TransformationSpec(transformationSpecFile);
        }catch (Exception e){
            throw new TransformationException(e);
        }
    }

}
