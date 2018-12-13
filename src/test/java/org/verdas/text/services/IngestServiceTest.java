package org.verdas.text.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.vedas.storage.model.Transcription;
import org.vedas.text.service.IngestService;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class IngestServiceTest {

    @Spy
    private IngestService ingestService;

    @Test
    public void testCorpusExtraction() throws IOException {
        List<Transcription> corpus = ingestService.extractCorpusFromDir("src/test/resources/corpus/");
        assertEquals(2, corpus.size());
    }

}
