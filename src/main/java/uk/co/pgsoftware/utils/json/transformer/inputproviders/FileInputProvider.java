package uk.co.pgsoftware.utils.json.transformer.inputproviders;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import uk.co.pgsoftware.utils.json.transformer.TransformationException;

import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Created by Partha on 17/04/2016.
 */
public class FileInputProvider implements TransformationInputProvider {

    static {PROVIDER_KEY[0] = "FILE";}

    private static final String FILE_ARG = "file";

    @Override
    public JsonObject loadInputDocument(JsonObject inputArgs) {
            String fileName = inputArgs.get(FILE_ARG).getAsString();
            try {
                FileReader json = new FileReader(fileName);
                return new JsonParser().parse(json).getAsJsonObject();
            } catch (FileNotFoundException e) {
                throw new TransformationException(e);
            }
    }
}
