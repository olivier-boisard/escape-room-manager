package mongellaz.bookpuzzle.commands.handshake;

import com.google.inject.Inject;
import mongellaz.communication.ByteArrayObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class BookPuzzleHandshakeResponseProcessor implements ByteArrayObserver {

    @Inject
    BookPuzzleHandshakeResponseProcessor(BookPuzzleHandshakeResultObserver bookPuzzleHandshakeResultObserver) {
        this.bookPuzzleHandshakeResultObserver = bookPuzzleHandshakeResultObserver;
    }

    @Override
    public void update(final byte[] response) {
        final byte commandCode = 0x10;
        if (response[0] == commandCode) {
            final byte[] expectedResponse = {commandCode, -123, -14, -98, -29, 67, 25, -22, -10};
            BookPuzzleHandshakeResult bookPuzzleHandshakeResult;
            if (Arrays.equals(response, expectedResponse)) {
                bookPuzzleHandshakeResult = BookPuzzleHandshakeResult.SUCCESS;
                logger.info("Received expected response");
            } else {
                bookPuzzleHandshakeResult = BookPuzzleHandshakeResult.FAILURE;
                if (logger.isErrorEnabled()) {
                    logger.error("Invalid response: {}", Arrays.toString(response));
                }
            }
            notifyHandshakeResultObserver(bookPuzzleHandshakeResult);
        } else {
            logger.debug("Ignoring command with code {}", commandCode);
        }
    }

    private void notifyHandshakeResultObserver(BookPuzzleHandshakeResult bookPuzzleHandshakeResult) {
        bookPuzzleHandshakeResultObserver.update(bookPuzzleHandshakeResult);
    }

    private final Logger logger = LogManager.getLogger();
    private final BookPuzzleHandshakeResultObserver bookPuzzleHandshakeResultObserver;
}
