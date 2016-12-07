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

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import nl.knaw.huygens.pergamon.nlp.langident.CavnarTrenkle;
import nl.knaw.huygens.pergamon.nlp.langident.CumulativeFrequency;
import nl.knaw.huygens.pergamon.nlp.langident.Model;
import nl.knaw.huygens.pergamon.nlp.langident.NaiveBayes;
import nl.knaw.huygens.pergamon.nlp.langident.TrainingSet;
import nl.knaw.huygens.pergamon.nlp.langident.service.health.ModelHealthCheck;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class LangIdentApp extends Application<LangIdentConfig> {
  private final Map<String, Model> models = new HashMap<>();
  static final String version; // Git commit SHA-1 of langident, for identification purposes.

  static {
    try (InputStream versionProp = LangIdentApp.class.getResourceAsStream("/version.properties")) {
      Properties prop = new Properties();
      prop.load(versionProp);
      version = prop.getProperty("version");
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private LangIdentApp() throws IOException {
    TrainingSet set = TrainingSet.getBuiltin();

    // We train our models here because training them is much cheaper than serializing and deserializing them
    // (Naive Bayes models tend to get big because there's no feature selection).
    models.put("cavnartrenkle", new CavnarTrenkle().train(set));
    models.put("cumfreq", new CumulativeFrequency().train(set));
    models.put("naivebayes", new NaiveBayes().train(set));
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
    LangIdentResource resource = new LangIdentResource(config.getDefaultModel(), models, version);
    env.jersey().register(resource);
    env.healthChecks().register("models", new ModelHealthCheck(resource));
  }
}
