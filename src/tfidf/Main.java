package tfidf;

import tfidf.indexer.DirectIndexer;
import tfidf.indexer.InverseIndexer;
import tfidf.indexer.StatsCalculator;
import tfidf.labeling.Labeler;
import tfidf.mongo.MongoConnector;
import tfidf.search.Search;
import tfidf.utils.Configs;
import tfidf.utils.DirectoryParser;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws URISyntaxException, IOException {
        Configs.loadProperties("/home/vlad/ws/Disertatie/config.properties");

        cleanDatabase();

        List<String> filePaths = new ArrayList<>();
        DirectoryParser directoryParser = new DirectoryParser();
        DirectIndexer directIndexer = new DirectIndexer();
        InverseIndexer inverseIndexer = new InverseIndexer();
        StatsCalculator statsCalculator = new StatsCalculator();
        Labeler labeler = new Labeler();
        directoryParser.parseDirectory(Paths.get(Configs.WORKDIR_PATH), filePaths);

        filePaths.forEach(file -> {
            try {
                labeler.writeLabel(file);
                directIndexer.reduceFile(directIndexer.mapFile(Paths.get(file)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        List<String> collections = MongoConnector.getCollections("DirectIndex");
        for (String collection : collections) {
            inverseIndexer.reduce(inverseIndexer.sort(collection));
        }

        statsCalculator.calculateNorms(filePaths);
    }

    private static void cleanDatabase() {
        MongoConnector.dropDatabase("RIW");
        MongoConnector.dropDatabase("DirectIndex");
        MongoConnector.dropDatabase("InvertedIndex");
    }
}
