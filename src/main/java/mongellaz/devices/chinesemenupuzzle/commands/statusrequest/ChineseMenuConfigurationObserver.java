package mongellaz.devices.chinesemenupuzzle.commands.statusrequest;

import mongellaz.devices.chinesemenupuzzle.devicecontroller.ChineseMenuConfiguration;

public interface ChineseMenuConfigurationObserver {
    void update(ChineseMenuConfiguration chineseMenuConfiguration);
}
