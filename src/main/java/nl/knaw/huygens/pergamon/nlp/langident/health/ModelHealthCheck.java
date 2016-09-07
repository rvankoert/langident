package nl.knaw.huygens.pergamon.nlp.langident.health;

import com.codahale.metrics.health.HealthCheck;
import com.google.common.base.Optional;
import nl.knaw.huygens.pergamon.nlp.langident.service.LangIdentResource;

import java.util.Set;

public class ModelHealthCheck extends HealthCheck {
  private final LangIdentResource resource;

  public ModelHealthCheck(LangIdentResource resource) {
    this.resource = resource;
  }

  @Override
  protected Result check() throws Exception {
    @SuppressWarnings("unchecked") final Set<String> models = (Set<String>) resource.listModels().get("models");

    if (models.isEmpty()) {
      return Result.unhealthy("No models to work with");
    }

    for (String name : models) {
      if (resource.listLanguages(Optional.of(name)).isEmpty()) {
        return Result.unhealthy(String.format("model %s has no languages", name));
      }
    }

    return Result.healthy(String.format("%d models in use: %s", models.size(), models));
  }
}
