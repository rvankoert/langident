package nl.knaw.huygens.pergamon.nlp.langident;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableList;

/**
 * A training set is a list of documents, paired with an equally long list of (string) labels.
 */
public class TrainingSet {
  private final List<CharSequence> docs;
  private final List<String> labels;

  public TrainingSet(List<CharSequence> docs, List<String> labels) {
    if (docs.size() != labels.size()) {
      throw new IllegalArgumentException(
        format("%d (docs) != %d (labels)", docs.size(), labels.size()));
    }
    this.docs = unmodifiableList(docs);
    this.labels = unmodifiableList(labels);
  }

  /**
   * Gets the built-in training set.
   */
  static public TrainingSet getBuiltin() throws IOException {
    // Load training data from our packaged JSON file. The format is [[label, doc], ...].
    InputStream trainingData = TrainingSet.class.getResourceAsStream("/training-data.json");
    ArrayNode allData = (ArrayNode) new ObjectMapper().readTree(trainingData);

    List<CharSequence> docs = new ArrayList<>();
    List<String> labels = new ArrayList<>();

    allData.forEach(sample -> {
      labels.add(sample.get(0).textValue());
      docs.add(sample.get(1).textValue());
    });

    return new TrainingSet(docs, labels);
  }

  public List<CharSequence> getDocs() {
    return docs;
  }

  public List<String> getLabels() {
    return labels;
  }
}
