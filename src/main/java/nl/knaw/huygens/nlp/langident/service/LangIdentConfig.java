package nl.knaw.huygens.nlp.langident.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

class LangIdentConfig extends Configuration {
  @NotEmpty
  private String defaultModel;

  @JsonProperty
  public String getDefaultModel() {
    return defaultModel;
  }

  @JsonProperty
  public void setDefaultModel(String defaultModel) {
    this.defaultModel = defaultModel;
  }
}
