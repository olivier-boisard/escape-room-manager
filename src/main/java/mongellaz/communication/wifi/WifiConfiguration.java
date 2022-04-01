package mongellaz.communication.wifi;

import java.util.Arrays;
import java.util.Objects;

public record WifiConfiguration(String ssid, char[] password) {
    void resetPassword() {
        Arrays.fill(password, '\0');
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WifiConfiguration that = (WifiConfiguration) o;
        return ssid.equals(that.ssid) && Arrays.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(ssid);
        result = 31 * result + Arrays.hashCode(password);
        return result;
    }

    @Override
    public String toString() {
        return "WifiConfiguration{" +
                "ssid='" + ssid + '\'' +
                ", password=" + Arrays.toString(password) +
                '}';
    }
}
