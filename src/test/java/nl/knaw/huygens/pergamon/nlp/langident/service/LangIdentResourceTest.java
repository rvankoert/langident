package nl.knaw.huygens.pergamon.nlp.langident.service;

import com.google.common.base.Optional;
import org.junit.Test;

import javax.ws.rs.WebApplicationException;
import java.util.HashMap;

public class LangIdentResourceTest {
  @Test(expected = WebApplicationException.class)
  public void invalidModelName() {
    LangIdentResource resource = new LangIdentResource("foo", new HashMap<>());
    resource.classify("some input text", Optional.of("bar"));
  }
}
