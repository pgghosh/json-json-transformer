package uk.co.pgsoftware.utils.json.transformer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import uk.co.pgsoftware.utils.json.transformer.transformmethods.TransformationMethod;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

/**
 * This class will hold the transformation specification for each transformation that needs to be done.
 * Transformation will be done by first loading a transformation spec file in an instance of this class.
 * This will hold the Transformation context and the output translation document.
 * Created by Partha Ghosh on 16/04/2016.
 */
public class TransformationSpec {

    private static final String TRANSLATION_DOCUMENT_KEY = "output";
    private static final String TRANSLATION_INPUTS_KEY = "inputs";

    private TransformationContext context;
    private JsonObject translationDocument;

    public TransformationSpec(String transformationFile) throws FileNotFoundException {
        this(new FileReader(transformationFile));
    }

    public TransformationSpec(Reader transformationSpecReader) {
        JsonObject spec = new JsonParser().parse(transformationSpecReader).getAsJsonObject();
        translationDocument = spec.getAsJsonObject(TRANSLATION_DOCUMENT_KEY);
        context = new TransformationContext();
        loadInputs(spec);

    }

    private void loadInputs(JsonObject spec) {
        JsonElement inputs = spec.get(TRANSLATION_INPUTS_KEY);
        if (inputs == null || inputs.isJsonNull()) {
            return;
        }
        if (inputs.isJsonArray()) {
            inputs.getAsJsonArray().forEach(input -> loadInput(input.getAsJsonObject()));
        } else if (inputs.isJsonObject()) {
            loadInput(inputs.getAsJsonObject());
        } else {
            throw new TransformationException("Inputs not specified correctly.");
        }
    }

    private static final String INPUT_REF = "ref";
    private static final String INPUT_PROVIDER = "provider";
    private static final String INPUT_ARGS = "args";

    private void loadInput(JsonObject input) {

        String inputReference = input.getAsJsonPrimitive(INPUT_REF).getAsString();
        String providerName = input.getAsJsonPrimitive(INPUT_PROVIDER).getAsString().toUpperCase();

        JsonObject providerArgs = input.getAsJsonObject(INPUT_ARGS);

        JsonObject inputJson = TransformationFactory.getTransformationInputProvider(providerName)
                .loadInputDocument(providerArgs);
        context.setJsonRef(inputReference, inputJson);

    }

    public TransformationContext getTransformationContext() {
        return context;
    }

    public JsonElement transform() {
        return TransformationFactory
                .getTransformationMethod(TransformationMethod.DEFAULT_TRANSFORMATION_METHOD)
                .apply(translationDocument, context);
    }
}