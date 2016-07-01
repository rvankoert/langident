package nl.knaw.huygens.pergamon.nlp.langident.service;

import com.fasterxml.jackson.annotation.JsonProperty;

class Result {
  private final String label;

  public Result(String label) {
    this.label = label;
  }

  @JsonProperty
  public String getLabel() {
    return label;
  }
}
