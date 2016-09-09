package nl.knaw.huygens.pergamon.nlp.langident.service.health;

/*-
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

import com.codahale.metrics.health.HealthCheck;
import nl.knaw.huygens.pergamon.nlp.langident.service.LangIdentResource;

import java.util.Optional;
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
