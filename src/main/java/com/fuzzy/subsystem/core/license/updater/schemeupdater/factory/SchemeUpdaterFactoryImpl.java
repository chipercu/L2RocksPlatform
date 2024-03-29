package com.fuzzy.subsystem.core.license.updater.schemeupdater.factory;

import com.fuzzy.subsystem.core.license.updater.schemeupdater.LicenseSchemeUpdater;
import com.fuzzy.subsystem.core.license.updater.schemeupdater.SchemeUpdater_2_to_3;
import com.fuzzy.subsystem.core.license.updater.schemeupdater.SchemeUpdater_3_to_4;
import com.fuzzy.subsystem.core.license.updater.schemeupdater.SchemeUpdater_4_to_5;

public class SchemeUpdaterFactoryImpl implements SchemeUpdaterFactory {

    @Override
    public LicenseSchemeUpdater getUpdater(int currentSchemeVersion) {
        return switch (currentSchemeVersion) {
            case 2 -> new SchemeUpdater_2_to_3();
            case 3 -> new SchemeUpdater_3_to_4();
            case 4 -> new SchemeUpdater_4_to_5();
            default -> null;
        };
    }
}
