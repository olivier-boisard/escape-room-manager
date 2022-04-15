package mongellaz.userinterface;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import mongellaz.communication.implementations.socket.SocketConfigurationHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class PersistentConfigurationHandler implements SocketConfigurationHandler {

    @Inject
    PersistentConfigurationHandler(@Named("PersistentConfigurationHandlerNameSpace") String nameSpace) {
        String userHomeDirectory = System.getProperty("user.home");
        String configurationFileName = ".enqueteSensorielle.bin";
        configurationFilePath = Paths.get(userHomeDirectory, configurationFileName);
        logger.info("Configuration file path: {}", configurationFilePath);
        hostNameKey = nameSpace + "HostName";
    }


    @Override
    public void setHostName(String hostName) {
        Map<String, String> configurationMap = readConfiguration();
        configurationMap.put(hostNameKey, hostName);
        writeConfiguration(configurationMap);
    }

    @Override
    public String getHostName() {
        return readConfiguration().getOrDefault(hostNameKey, "");
    }

    private Map<String, String> readConfiguration() {
        File file = getFile();
        Map<String, String> configurationMap = new HashMap<>();
        if (file.exists()) {
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                try (ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
                    //noinspection unchecked
                    configurationMap = (Map<String, String>) objectInputStream.readObject();
                }
            } catch (FileNotFoundException e) {
                logger.error("Could not read file at {}: {}", configurationFilePath, e.getMessage());
            } catch (IOException e) {
                logger.error("Error while reading/writing into file at {}: {}", configurationFilePath, e.getMessage());
            } catch (ClassNotFoundException e) {
                logger.error("Unhandled class: {}", e.getMessage());
            }
        }
        return configurationMap;
    }

    private void writeConfiguration(Map<String, String> configurationMap) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(getFile())) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
                objectOutputStream.writeObject(configurationMap);
            }
        } catch (FileNotFoundException e) {
            logger.error("Could not read file at {}: {}", configurationFilePath, e.getMessage());
        } catch (IOException e) {
            logger.error("Error while reading/writing into file at {}: {}", configurationFilePath, e.getMessage());
        }
    }

    private File getFile() {
        return configurationFilePath.toFile();
    }

    private final Path configurationFilePath;
    private final String hostNameKey;
    private final Logger logger = LogManager.getLogger();
}
