package mongellaz.devices.chinesemenupuzzle.commands.configure;

import java.util.Arrays;

public class ChineseMenuConfigureRequestFactory {
    public byte[] generate(ChineseMenuConfiguration chineseMenuConfiguration) {
        final byte configureCode = 0x30;
        final byte endMessageCode = 0x00;
        final int outputBufferSize = 64;
        final byte[] outputBuffer = new byte[outputBufferSize];
        int nWrittenBytes = 0;

        // Write command code
        outputBuffer[nWrittenBytes++] = configureCode;

        // Write number as strings
        nWrittenBytes = writeValueAsAStringInBuffer(chineseMenuConfiguration.minWeightInGrams(), outputBuffer, nWrittenBytes);
        nWrittenBytes = writeValueAsAStringInBuffer(chineseMenuConfiguration.maxWeightInGrams(), outputBuffer, nWrittenBytes);
        nWrittenBytes = writeValueAsAStringInBuffer(chineseMenuConfiguration.holdingTimeMs(), outputBuffer, nWrittenBytes);

        // Write end message code
        outputBuffer[nWrittenBytes++] = endMessageCode;

        return Arrays.copyOf(outputBuffer, nWrittenBytes);
    }

    private int writeValueAsAStringInBuffer(int value, byte[] outputBuffer, int index) {
        String valueAsString = String.format("%010d", value);
        for (int i = 0; i < valueAsString.length(); i++) {
            outputBuffer[index++] = (byte) valueAsString.charAt(i);
        }
        return index;
    }
}
