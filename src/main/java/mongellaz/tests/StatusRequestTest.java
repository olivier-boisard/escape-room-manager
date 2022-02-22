package mongellaz.tests;

import mongellaz.commands.statusrequest.StatusRequestFactory;
import mongellaz.commands.statusrequest.StatusRequestResponseProcessor;

public class StatusRequestTest {
    public static void main(String[] args) {
        int nChecks = 100;
        new SerialCommunicationTest(new StatusRequestFactory(), new StatusRequestResponseProcessor()).run(nChecks);
    }

}
