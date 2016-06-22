package nl.knaw.huygens.nlp.langident.service;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import nl.knaw.huygens.nlp.langident.Classifier;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Map;
import java.util.Set;

@Path("/ident")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.APPLICATION_JSON)
public class LangIdentResource {
    private final String defaultModel;
    private final Map<String, Classifier> models;

    public LangIdentResource(String defaultModel, Map<String, Classifier> models) {
        this.defaultModel = defaultModel;
        this.models = models;
    }

    @POST
    @Timed
    public Result classify(@FormParam("text") String text, @FormParam("model") Optional<String> modelName) {
        Classifier classifier = models.get(modelName.or(defaultModel));
        return new Result(classifier.predict(text));
    }

    @GET
    @Path("/list")
    @Timed
    public Set<String> listModels() {
        return models.keySet();
    }
}
