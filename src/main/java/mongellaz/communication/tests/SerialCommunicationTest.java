package mongellaz.communication.tests;

import mongellaz.commands.ByteArrayFactory;
import mongellaz.communication.SerialCommunicationManager;

public record SerialCommunicationTest(ByteArrayFactory commandFactory) {
    public void run() {
        try (SerialCommunicationManager serialCommunicationManager = new SerialCommunicationManager()) {
            Thread.sleep(3000);
            ResponseProcessor responseProcessor = new ResponseProcessor();
            final int nChecks = 10;
            for (int i = 0; i < nChecks; i++) {
                serialCommunicationManager.write(commandFactory.generate());
                byte[] response = serialCommunicationManager.read();
                responseProcessor.process(response);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
