package nl.knaw.huygens.pergamon.nlp.langident.service;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import nl.knaw.huygens.pergamon.nlp.langident.LanguageGuesser;

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
  private final Map<String, LanguageGuesser> models;

  public LangIdentResource(String defaultModel, Map<String, LanguageGuesser> models) {
    this.defaultModel = defaultModel;
    this.models = models;
  }

  @POST
  @Timed
  public List<LanguageGuesser.Prediction> classify(@FormParam("text") String text, @FormParam("model") Optional<String> modelName) {
    LanguageGuesser model = models.get(modelName.or(defaultModel));
    return model.predictScores(text);
  }

  @GET
  @Timed
  @Path("/languages")
  public Set<String> listLanguages(@QueryParam("model") Optional<String> modelName) {
    LanguageGuesser model = models.get(modelName.or(defaultModel));
    return model.languages();
  }

  @GET
  @Path("/list")
  @Timed
  public Set<String> listModels() {
    return models.keySet();
  }
}
