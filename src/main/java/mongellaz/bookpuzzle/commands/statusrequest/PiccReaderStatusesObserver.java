package mongellaz.bookpuzzle.commands.statusrequest;

public interface PiccReaderStatusesObserver {
    void update(Iterable<PiccReaderStatus> piccReaderStatuses);
}
