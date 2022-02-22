package mongellaz.communication.tests;

import mongellaz.commands.ByteArrayFactory;
import mongellaz.communication.SerialCommunicationManager;

public record SerialCommunicationTest(ByteArrayFactory commandFactory, ResponseProcessor responseProcessor) {

    public void run(int nChecks) {
        try (SerialCommunicationManager serialCommunicationManager = new SerialCommunicationManager()) {
            Thread.sleep(3000);
            for (int i = 0; i < nChecks; i++) {
                byte[] command = commandFactory.generate();
                serialCommunicationManager.write(command);
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
