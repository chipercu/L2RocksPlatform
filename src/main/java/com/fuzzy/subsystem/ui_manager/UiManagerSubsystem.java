package com.fuzzy.subsystem.ui_manager;

import com.fuzzy.main.Subsystems;
import com.fuzzy.platform.component.database.DatabaseComponent;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.subsystems.subsystem.Info;
import com.fuzzy.subsystems.subsystem.SdkInfoBuilder;
import com.fuzzy.subsystems.subsystem.Subsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@com.fuzzy.cluster.anotation.Info(uuid = UiManagerSubsystemConsts.UUID)
public class UiManagerSubsystem extends Subsystem {

    private final static Logger log = LoggerFactory.getLogger(UiManagerSubsystem.class);
    public static final Info INFO = new SdkInfoBuilder(UiManagerSubsystemConsts.UUID, UiManagerSubsystem.class)
            .withDependence(DatabaseComponent.class)
            .build();

    private final UiManagerConfig config;
    private final UI_Manager uiManager;


    public UiManagerSubsystem() throws PlatformException {
        this.config = new UiManagerConfig.Builder(INFO, Subsystems.getInstance().getConfig()).build();
        this.uiManager = new UI_Manager();
        new Thread(this.uiManager::launchManager).start();
    }

    @Override
    public Info getInfo() {
        return INFO;
    }

    @Override
    public UiManagerConfig getConfig() {
        return config;
    }

}