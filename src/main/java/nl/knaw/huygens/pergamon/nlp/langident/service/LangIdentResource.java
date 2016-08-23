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
import nl.knaw.huygens.pergamon.nlp.langident.Model;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Path("/ident")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.APPLICATION_JSON)
public class LangIdentResource {
  private final String defaultModel;
  private final Map<String, Model> models;

  public LangIdentResource(String defaultModel, Map<String, Model> models) {
    this.defaultModel = defaultModel;
    this.models = models;
  }

  @POST
  @Timed
  public List<Model.Prediction> classify(@FormParam("text") String text,
                                         @FormParam("model") Optional<String> modelName) {
    Model model = models.get(modelName.or(defaultModel));
    if (model == null) {
      throw new WebApplicationException(String.format("unknown model '%s'", modelName), 404);
    }
    return model.predictScores(text);
  }

  @GET
  @Timed
  @Path("/languages")
  public Set<String> listLanguages(@QueryParam("model") Optional<String> modelName) {
    Model model = models.get(modelName.or(defaultModel));
    return model.languages();
  }

  @GET
  @Path("/list")
  @Timed
  public Set<String> listModels() {
    return models.keySet();
  }
}
