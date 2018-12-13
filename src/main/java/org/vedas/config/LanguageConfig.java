package org.vedas.config;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.StanfordCoreNLPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

@Configuration
public class LanguageConfig {

    @Autowired
    private Environment env;

//    @Bean(name = "ner")
    public StanfordCoreNLP ner() throws IOException {
        if (!Arrays.asList(env.getActiveProfiles()).contains("no-ner")
            && !Arrays.asList(env.getActiveProfiles()).contains("no-nlp")) {
            // set up pipeline properties
            Properties props = new Properties();
            props.load(getClass().getClassLoader().getResourceAsStream("nlp-spanish.properties"));
            props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
            // pos model for PCFG/SR parser
            props.setProperty("pos.model", "edu/stanford/nlp/models/pos-tagger/spanish/spanish-distsim.tagger");
            // set up pipeline
            StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
            return pipeline;
        } else {
            return null;
        }
    }

    @Bean(name = "dep")
    public StanfordCoreNLP dep() throws IOException {
        // set up pipeline properties
        if (!Arrays.asList(env.getActiveProfiles()).contains("no-dep")
            && !Arrays.asList(env.getActiveProfiles()).contains("no-nlp")) {
            Properties props = new Properties();
            props.load(getClass().getClassLoader().getResourceAsStream("nlp-spanish.properties"));
            props.setProperty("annotators", "tokenize,ssplit,pos,depparse,lemma,ner");
            // pos model for dependency parser
            props.setProperty("pos.model", "edu/stanford/nlp/models/pos-tagger/spanish/spanish-ud.tagger");
            props.setProperty("depparse.model", "edu/stanford/nlp/models/parser/nndep/UD_Spanish.gz");
            // set up pipeline
            StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
            return pipeline;
        } else {
            return null;
        }
    }

    @Bean
    public StanfordCoreNLPClient rest() throws IOException {
        // set up pipeline properties
        if (!Arrays.asList(env.getActiveProfiles()).contains("no-rest")
            && !Arrays.asList(env.getActiveProfiles()).contains("no-nlp")) {

            // set up pipeline properties
            Properties props = new Properties();
            // props.load(getClass().getClassLoader().getResourceAsStream("nlp-spanish.properties"));
            props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");

            StanfordCoreNLPClient pipeline = new StanfordCoreNLPClient(props,
                "http://localhost", 9000, 2);
            return pipeline;
        } else {
            return null;
        }
    }
}
