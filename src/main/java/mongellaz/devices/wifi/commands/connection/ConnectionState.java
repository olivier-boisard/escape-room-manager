package mongellaz.devices.wifi.commands.connection;

import java.util.Arrays;

public record ConnectionState(int[] ipAddress) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectionState that = (ConnectionState) o;
        return Arrays.equals(ipAddress, that.ipAddress);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(ipAddress);
    }

    @Override
    public String toString() {
        return "ConnectionState{" +
                "ipAddress=" + Arrays.toString(ipAddress) +
                '}';
    }
}
