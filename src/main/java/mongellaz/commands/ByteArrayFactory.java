package mongellaz.commands;

@FunctionalInterface
public interface ByteArrayFactory {
    byte[] generate();

    byte END_OF_TRANSMISSION = 0x00;
}
