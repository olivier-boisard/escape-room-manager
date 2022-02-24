package mongellaz.commands;

import mongellaz.commands.statusrequest.PiccReaderStatus;

public interface PiccReaderStatusesObserver {
    void update(Iterable<PiccReaderStatus> piccReaderStatuses);
}
