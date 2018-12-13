package org.vedas.text.integration;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.vedas.storage.model.NamedEntity;
import org.vedas.storage.model.Transcription;
import org.vedas.storage.repo.TranscriptionsRepository;
import org.vedas.text.service.IngestService;
import org.vedas.text.service.LanguageService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.neo4j.driver.v1.Values.parameters;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"no-ner"}) //no-dep
public class StorageServiceTestIT {

    @Autowired
    private TranscriptionsRepository graph;

    @Autowired
    private LanguageService languageService;

    @Autowired
    private IngestService ingestService;

    @Before
    public void init() {
        graph.wipeOut();
    }

    @Test
    public void testNamedEntitiesGraph() throws IOException {
        List<Transcription> corpus = ingestService.extractCorpusFromDir("src/test/resources/corpus/");
        corpus.forEach(item -> {
//            List<CoreEntityMention> mentions = languageService.extractNamedEntities(new CoreDocument(item.getCorpus()));
//            item.setEntities(mentions.stream()
//                .map(mention -> new NamedEntity(mention.text(), mention.entityType(), mention.sentence().text()))
//                .collect(Collectors.toList()));
            List<NamedEntity> mentions = languageService.extractNamedEntities(new Annotation(item.getCorpus()));
            item.setEntities(mentions);
            graph.save(item);
        });
    }

    @Test
    public void testCombineEntitiesAndDependencies() throws IOException {
        List<Transcription> corpus = ingestService.extractCorpusFromDir("src/test/resources/corpus/corpus_es_4.txt");
        corpus.stream().limit(1).forEach(transcription -> {
            languageService.extractSentences(new CoreDocument(transcription.getCorpus()))
                .forEach(sentence -> {
                    CoreDocument coreSentence = new CoreDocument(sentence.text());
                    Map<String, String> mentions = new HashMap<>();
                    try {
                        mentions = languageService.extractNamedEntities(coreSentence)
                            .stream().distinct().collect(Collectors.toMap(
                                CoreEntityMention::text, CoreEntityMention::entityType, (e1, e2) -> e1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    List<SemanticGraph> dependencies = languageService.extractDependencies(coreSentence);
                    if (!mentions.isEmpty()) {
                        System.out.println(sentence.text());
                        Iterable<SemanticGraphEdge> semanticGraphEdges = dependencies.get(0).edgeIterable();
                        Map<String, String> finalMentions = mentions;
                        semanticGraphEdges.forEach(edge -> {
                            IndexedWord source = edge.getSource();
                            IndexedWord target = edge.getTarget();
                            if (finalMentions.containsKey(source.word())) {
                                System.out.println("    " + source.word() + "("+ finalMentions.get(source.word())+"):" + source.tag()
                                    + " -- " + edge.getRelation().getShortName() + " -- " + target.word() + ":" + target.tag());
                            }
                            if (finalMentions.containsKey(target.word())) {
                                System.out.println("    " + source.word() + ":" + source.tag()
                                    + " -- " + edge.getRelation().getShortName() + " -- " + target.word() + "("+ finalMentions.get(target.word())+"):" + target.tag());
                            }
                        });
                    }
                });
        });
    }

    @Test
    public void testDependenciesGraph() throws IOException {
        Driver driver = GraphDatabase.driver(
            "bolt://localhost:7687", AuthTokens.basic("neo4j", "AngelFire"));
        Session session = driver.session();
        List<Transcription> corpus = ingestService.extractCorpusFromDir("src/test/resources/corpus/");
        corpus.forEach(item -> {
            item.setEntities(new ArrayList<>());
//            List<Tree> trees = languageService.extractDependencies(new Annotation(item.getCorpus()));
            List<SemanticGraph> semanticGraphs = languageService.extractDependencies(new CoreDocument(item.getCorpus()));

            Iterable<SemanticGraphEdge> semanticGraphEdges = semanticGraphs.get(10).edgeIterable();
            semanticGraphEdges.forEach(edge -> {
                IndexedWord source = edge.getSource();
                IndexedWord target = edge.getTarget();
//                    if ("NOUN".equals(source.tag()) || "NOUN".equals(target.tag())) {
                System.out.println(source.word() + ":" + source.tag()
                    + " -[" + edge.getRelation().getShortName() + "]-" + target.word() + ":" + target.tag());

                session.run("CREATE (a:Word {word: $worda, type: $typea})-[r:" + edge.getRelation().getShortName().toUpperCase() + "]->" +
                        "(b:Word {word: $wordb, type: $typeb})",
                    parameters("worda", source.word(), "typea", source.tag(),
                        "wordb", target.word(), "typeb", target.tag()));
//                    }

            });
//            }
            System.out.println(item);
        });
    }
}