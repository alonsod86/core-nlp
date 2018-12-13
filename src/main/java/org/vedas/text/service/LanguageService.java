package org.vedas.text.service;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.vedas.storage.model.NamedEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LanguageService {

//    @Autowired @Qualifier("ner")
//    private Optional<StanfordCoreNLP> ner;

    @Autowired
    private ApplicationContext ctx;

    public List<CoreSentence> extractSentences(CoreDocument corpus) {
        ctx.getBean(StanfordCoreNLP.class).annotate(corpus);
        return corpus.sentences();
    }

    public List<NamedEntity> extractNamedEntities(Annotation corpus) {
        ctx.getBean(StanfordCoreNLPClient.class).annotate(corpus);
        List<Pair<String, List<CoreMap>>> collect = corpus.get(CoreAnnotations.SentencesAnnotation.class).stream()
            .map(sentence -> Pair.of(sentence.get(CoreAnnotations.TextAnnotation.class),
                sentence.get(CoreAnnotations.MentionsAnnotation.class)))
            .collect(Collectors.toList());

        List<NamedEntity> entities = new ArrayList<>();
        collect.forEach(sentence -> sentence.getRight()
            .forEach(entity -> entities.add(toNamedEntity(entity, sentence.getLeft()))));

        return entities;
    }

    public List<CoreEntityMention> extractNamedEntities(CoreDocument corpus) {
        ctx.getBean(StanfordCoreNLP.class).annotate(corpus);
        return corpus.entityMentions();
    }

    public List<Tree> extractDependencies(Annotation corpus) {
        ctx.getBean(StanfordCoreNLPClient.class).annotate(corpus);
        List<Tree> collect = corpus.get(CoreAnnotations.SentencesAnnotation.class).stream()
            .map(sentence -> sentence.get(TreeCoreAnnotations.TreeAnnotation.class))
            .collect(Collectors.toList());
        return collect;
    }

    public List<SemanticGraph> extractDependencies(CoreDocument corpus) {
        ctx.getBean(StanfordCoreNLP.class).annotate(corpus);
        List<SemanticGraph> collect = corpus.sentences().stream()
            .map(sentence -> sentence.dependencyParse())
            .collect(Collectors.toList());
        return collect;
    }

    public static NamedEntity toNamedEntity(CoreMap mention, String sentence) {
        return new NamedEntity(
            mention.get(CoreAnnotations.TextAnnotation.class),
            mention.get(CoreAnnotations.EntityTypeAnnotation.class),
            sentence
        );
    }
}
