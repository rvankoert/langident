package nl.knaw.huygens.nlp.langident;

import java.util.*;

/**
 * Naive Bayes classifier for language identification.
 * <p>
 * This is a multinomial Naive Bayes classifier using n-gram features, for 2 <=
 * n <= 7, and a uniform prior over languages.
 */
public class NaiveBayes extends NGramFeaturesClassifier {
    private static final long serialVersionUID = 915582140959386023L;

    // Laplace/Lidstone smoothing parameter, currently fixed.
    private double pseudocount = 1.;

    private Set<String> labels;

    // Feature log-probabilities per label.
    // Maps label -> ngram -> log(P(ngram)).
    private Map<String, Map<CharSequence, Double>> featureProb;

    /**
     * Construct untrained classifier with default settings.
     */
    public NaiveBayes() {
    }

    public NaiveBayes(double pseudocount, int minN, int maxN) {
        this.pseudocount = pseudocount;
        this.minN = minN;
        this.maxN = maxN;
    }

    /**
     * Train Naive Bayes model on labeled documents.
     * <p>
     * Each i'th element of docs is expected have the i'th label in labels. If
     * the length of these two arrays doesn't match, an exception is thrown.
     * <p>
     * Calling train a second time retrains the model.
     *
     * @param docs
     * @param labels
     */
    @Override
    public Classifier train(List<CharSequence> docs, List<String> labels) {
        featureProb = new HashMap<String, Map<CharSequence, Double>>();
        this.labels = new HashSet<String>();

        if (docs.size() != labels.size()) {
            throw new IllegalArgumentException(String.format("%d samples != %d labels", docs.size(), labels.size()));
        }

        for (String label : labels) {
            featureProb.put(label, new HashMap<CharSequence, Double>());
            this.labels.add(label);
        }

        for (int i = 0; i < docs.size(); i++) {
            Map<CharSequence, Double> fp = featureProb.get(labels.get(i));
            features(docs.get(i))
                    .forEach(ngram -> fp.compute(ngram, (ng, oldCount) -> (oldCount == null ? 0 : oldCount) + 1));
        }

        Map<CharSequence, Double> totalCounts = new HashMap<CharSequence, Double>();
        featureProb.forEach((label, counts) -> {
            counts.forEach((ngram, count) -> totalCounts.put(ngram, totalCounts.getOrDefault(ngram, 0.) + count));
        });

        // Normalize counts to probabilities; also Lidstone smoothing.
        totalCounts.forEach((ngram, totalCount) -> {
            featureProb.forEach((label, prob) -> {
                double count = prob.getOrDefault(ngram, 0.);
                prob.put(ngram,
                        Math.log(count + pseudocount) - Math.log(totalCount + pseudocount * totalCounts.size()));
            });
        });

        return this;
    }

    /**
     * Predict label for doc.
     *
     * @param doc
     * @return
     */
    @Override
    public String predict(CharSequence doc) {
        Map<String, Double> prob = decisionFunction(doc);
        return prob.entrySet().stream()
                .max((candidate1, candidate2) -> Double.compare(candidate1.getValue(), candidate2.getValue())) //
                .get().getKey();
    }

    /**
     * Decision function: returns an unnormalized (log-)probability distribution
     * over the labels for doc.
     *
     * @param doc
     * @return
     */
    private Map<String, Double> decisionFunction(CharSequence doc) {
        Map<String, Double> prob = new HashMap<String, Double>();

        final double prior = -Math.log(labels.size());    // Uniform prior.
        for (String label : labels) {
            prob.put(label, prior);
        }

        features(doc).forEach(ngram -> {
            labels.forEach(label -> {
                prob.put(label, prob.get(label) + featureProb.get(label).getOrDefault(ngram, 0.));
            });
        });

        return prob;
    }
}