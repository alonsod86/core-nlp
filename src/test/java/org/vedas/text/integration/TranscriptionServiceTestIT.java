package org.vedas.text.integration;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.vedas.text.service.TranscriptionService;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"no-nlp"})
public class TranscriptionServiceTestIT {

    @Autowired
    private TranscriptionService transcriptionService;

    @Before
    public void init() throws Exception {
        String credentials = new File("src/main/resources/google/advaita.json").getAbsolutePath();
        injectEnvironmentVariable("GOOGLE_APPLICATION_CREDENTIALS", credentials);
    }

    @Ignore
    @Test
    public void testLongTranscription() throws InterruptedException, ExecutionException, IOException {
        transcriptionService.longTranscription("gs://advaita/test.flac", "es-ES");
    }

    @Test
    public void testShortTranscription() {
        transcriptionService.shortTranscription("/home/dgutierrez/Descargas/test_1m.flac", "es-ES");
    }

    private static void injectEnvironmentVariable(String key, String value)
        throws Exception {

        Class<?> processEnvironment = Class.forName("java.lang.ProcessEnvironment");

        Field unmodifiableMapField = getAccessibleField(processEnvironment, "theUnmodifiableEnvironment");
        Object unmodifiableMap = unmodifiableMapField.get(null);
        injectIntoUnmodifiableMap(key, value, unmodifiableMap);

        Field mapField = getAccessibleField(processEnvironment, "theEnvironment");
        Map<String, String> map = (Map<String, String>) mapField.get(null);
        map.put(key, value);
    }

    private static Field getAccessibleField(Class<?> clazz, String fieldName)
        throws NoSuchFieldException {

        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

    private static void injectIntoUnmodifiableMap(String key, String value, Object map)
        throws ReflectiveOperationException {

        Class unmodifiableMap = Class.forName("java.util.Collections$UnmodifiableMap");
        Field field = getAccessibleField(unmodifiableMap, "m");
        Object obj = field.get(map);
        ((Map<String, String>) obj).put(key, value);
    }
}
