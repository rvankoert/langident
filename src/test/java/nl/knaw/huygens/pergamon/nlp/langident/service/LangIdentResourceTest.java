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

import com.google.common.collect.ImmutableMap;
import nl.knaw.huygens.pergamon.nlp.langident.CavnarTrenkle;
import nl.knaw.huygens.pergamon.nlp.langident.TrainingSet;
import org.junit.Test;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LangIdentResourceTest {
  @Test(expected = WebApplicationException.class)
  public void invalidModelName() {
    LangIdentResource resource = new LangIdentResource("foo", new HashMap<>(), "foo");
    resource.classify("some input text", Optional.of("bar"));
  }

  @Test
  public void listLanguages() throws IOException {
    LangIdentResource resource = new LangIdentResource("ct",
      ImmutableMap.of("ct", new CavnarTrenkle().train(TrainingSet.getBuiltin())),
      "bla");
    Map<String, Object> languages = resource.listLanguages(Optional.empty());
    assertEquals("ct", languages.get("model"));
    assertEquals("bla", languages.get("version"));
    assertTrue(((Collection<?>) languages.get("languages")).contains("it"));
  }
}
