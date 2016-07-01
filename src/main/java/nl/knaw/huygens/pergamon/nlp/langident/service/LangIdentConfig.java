package nl.knaw.huygens.pergamon.nlp.langident.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

public class LangIdentConfig extends Configuration {
  @NotEmpty
  private String defaultModel = "cavnartrenkle";

  @JsonProperty
  public String getDefaultModel() {
    return defaultModel;
  }

  @JsonProperty
  public void setDefaultModel(String defaultModel) {
    this.defaultModel = defaultModel;
  }
}
