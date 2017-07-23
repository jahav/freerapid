package cz.vity.freerapid.plugins.webclient.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author VitasekL
 * Mapper should be used only 1 for 1 thread
 * @since 1.0
 */
public class JsonMapperV2 {

    private final ObjectMapper objectMapper;
    private final static Logger logger = Logger.getLogger(PlugUtils.class.getName());

    public JsonMapperV2() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
    }


    /**
     * Deserializes selected object byt its class. The class should be public and static in case of inner class +
     * public constructor in case of problems.
     * @param contentAsString input string to parse
     * @param objectClass result of instance class
     * @param <T> type of class
     * @return instance of object
     * @throws PluginImplementationException
     */
    public <T> T deserialize(String contentAsString, Class<T> objectClass) throws PluginImplementationException {
        //if (objectMapper.canDeserialize(objectMapper.constructType(objectClass))) {
            try {
                return objectMapper.readValue(contentAsString, objectClass);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "cannot parse json", e);
                throw new PluginImplementationException("Cannot parse JSON for class" + objectClass.getName());
            }
        //}
        //throw new PluginImplementationException("Cannot deserialize JSON for class " + objectClass.getName());
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

}