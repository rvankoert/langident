package nl.knaw.huygens.pergamon.nlp.langident.service;

/*
 * #%L
 * langident
 * %%
 * Copyright (C) 2016 Huygens ING (KNAW)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import nl.knaw.huygens.pergamon.nlp.langident.CavnarTrenkle;
import nl.knaw.huygens.pergamon.nlp.langident.CumulativeFrequency;
import nl.knaw.huygens.pergamon.nlp.langident.LanguageGuesser;
import nl.knaw.huygens.pergamon.nlp.langident.NaiveBayes;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LangIdentApp extends Application<LangIdentConfig> {
  private final Map<String, LanguageGuesser> models = new HashMap<>();

  private LangIdentApp() throws ClassNotFoundException, IOException {
    // Load training data from our packaged JSON file. The format is [[label, doc], ...].
    InputStream trainingData = this.getClass().getResourceAsStream("/training-data.json");
    ArrayNode allData = (ArrayNode) new ObjectMapper().readTree(trainingData);

    List<CharSequence> docs = new ArrayList<>();
    List<String> labels = new ArrayList<>();
    allData.forEach(sample -> {
      labels.add(sample.get(0).textValue());
      docs.add(sample.get(1).textValue());
    });

    // We train our models here because training them is much cheaper than serializing and deserializing them
    // (Naive Bayes models tend to get big because there's no feature selection).
    models.put("cavnartrenkle", new CavnarTrenkle().train(docs, labels));
    models.put("cumfreq", new CumulativeFrequency().train(docs, labels));
    models.put("naivebayes", new NaiveBayes().train(docs, labels));
  }

  public static void main(String[] args) throws Exception {
    new LangIdentApp().run(args);
  }

  @Override
  public String getName() {
    return "langident";
  }

  @Override
  public void initialize(Bootstrap<LangIdentConfig> bootstrap) {
  }

  @Override
  public void run(LangIdentConfig config, Environment env) {
    LangIdentResource resource = new LangIdentResource(config.getDefaultModel(), models);
    env.jersey().register(resource);
  }
}
