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

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.ImmutableMap;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import nl.knaw.huygens.pergamon.nlp.langident.Model;

import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;

@Path("/ident")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.APPLICATION_JSON)
public class LangIdentResource {
  private final String defaultModel;
  private final Map<String, Model> models;
  private final String version;

  public LangIdentResource(String defaultModel, Map<String, Model> models, String version) {
    this.defaultModel = defaultModel;
    this.models = models;
    this.version = version;
  }

  @POST
  @Timed
  public Map<String, Object> classify(@FormParam("text") String text,
                                      @QueryParam("model") Optional<String> modelName) {
    String name = modelName.orElse(defaultModel);
    Model model = modelByName(name);
    return ImmutableMap.of(
      "model", name,
      "prediction", model.predictScores(text),
      "version", version
    );
  }

  @GET
  @Timed
  @Path("/languages")
  public Map<String, Object> listLanguages(@QueryParam("model") Optional<String> modelName) {
    String name = modelName.orElse(defaultModel);
    Model model = modelByName(name);
    return ImmutableMap.of(
      "languages", model.languages(),
      "model", name,
      "version", version
    );
  }

  private Model modelByName(String name) {
    Model model = models.get(name);
    if (model == null) {
      throw new WebApplicationException(format("unknown model '%s'", name), 404);
    }
    return model;
  }

  @GET
  @Path("/models")
  @Timed
  public Map<String, Object> listModels() {
    return ImmutableMap.of(
      "models", models.keySet(),
      "version", version
    );
  }
}
