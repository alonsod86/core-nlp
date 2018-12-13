package org.vedas.text.integration;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.vedas.storage.model.Transcription;
import org.vedas.text.service.IngestService;
import org.vedas.text.service.LanguageService;

import java.io.IOException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"no-dep"})
public class LanguageServiceTestIT {

    @Autowired
    private LanguageService languageService;

    @Autowired
    private IngestService ingestService;

    @Test
    public void testNerExtraction() throws IOException {
        List<Transcription> corpus = ingestService.extractCorpusFromDir("src/test/resources/corpus/");

        corpus.forEach(item -> {
            List<CoreEntityMention> mentions = languageService.extractNamedEntities(new CoreDocument(item.getCorpus()));
            mentions.forEach(mention -> {
                System.out.println(mention.entityType() + ": " + mention.text()
                + ": " + mention.sentence().text());
            });
        });
    }
}
