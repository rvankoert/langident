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
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import nl.knaw.huygens.pergamon.nlp.langident.Model;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import java.util.Map;

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
    String name = modelName.or(defaultModel);
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
    String name = modelName.or(defaultModel);
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
