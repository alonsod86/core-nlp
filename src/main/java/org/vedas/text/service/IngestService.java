package org.vedas.text.service;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.vedas.storage.model.Transcription;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.UUID.*;

@Service
public class IngestService {

    public List<Transcription> extractCorpusFromDir(String baseDir) throws IOException {
        List<Transcription> corpus = new ArrayList<>();
        Path startPath = Paths.get(baseDir);
        Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                throws IOException {
            corpus.add(new Transcription(
                randomUUID().toString(),
                IOUtils.toString(file.toUri(), Charset.forName("UTF-8")),
                file.toString()));
                return FileVisitResult.CONTINUE;
            }
        });

        return corpus;
    }
}
