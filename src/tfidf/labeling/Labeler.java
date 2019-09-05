package tfidf.labeling;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.Mongo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import tfidf.mongo.MongoConnector;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.mongodb.client.model.Filters.eq;

public class Labeler {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public void writeLabel(String filePath) throws JsonProcessingException {
        Label label = computeLabel(Paths.get(filePath));
        MongoConnector.writeToCollection(label, "RIW", "labels");
    }

    public Label getLabel(String fileName) throws IOException {
        MongoCollection<Document> labelCollection = MongoConnector.getCollection("RIW", "labels");
        Document labelDocument = labelCollection.find(eq("id", fileName)).iterator().next();
        return objectMapper.readValue(labelDocument.toJson(), Label.class);
    }

    private Label computeLabel(Path filePath) {
        String fileName = filePath.getFileName().toString();
        String[] tokens = fileName.split("-");
        String label = tokens[0];

        return Label.builder()
                .id(fileName)
                .label(label)
                .build();
    }
}
