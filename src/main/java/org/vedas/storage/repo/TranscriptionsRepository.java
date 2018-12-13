package org.vedas.storage.repo;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.vedas.storage.model.Transcription;

public interface TranscriptionsRepository extends Neo4jRepository<Transcription, String> {

    @Query("MATCH (n) OPTIONAL MATCH (n)-[r0]-() DELETE r0, n")
    void wipeOut();

//    @Query("MATCH (m:Movie)<-[r:ACTED_IN]-(a:Person) RETURN m,r,a LIMIT {limit}")
//    Collection<Movie> graph(@Param("limit") int limit);
}