package com.fuzzy.subsystems.subsystem;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.component.Component;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.main.platform.sdk.struct.querypool.QuerySystem;
import com.fuzzy.main.rdao.database.anotation.Entity;
import com.fuzzy.main.rdao.database.schema.Schema;
import com.fuzzy.main.rdao.database.schema.StructEntity;
import com.fuzzy.subsystems.Subsystems;
import com.fuzzy.subsystems.scheduler.SubsystemScheduler;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;

public abstract class Subsystem extends Component {

    private final static Logger log = LoggerFactory.getLogger(Subsystem.class);

    private SubsystemScheduler scheduler;
    private SubsystemMessageSource messageSource;

    public Subsystem() {}

    public abstract SubsystemConfig getConfig();

    @Override
    public abstract Info getInfo();


    @Override
    public final void onStarting() throws PlatformException {
        super.onStarting();

        this.messageSource = new SubsystemMessageSource(this);
        this.scheduler = new SubsystemScheduler(this, Subsystems.getInstance().getScheduler());
    }

    @Override
    public QuerySystem<Void> onStop() {
        return new QuerySystem<>() {
            @Override
            public void prepare(ResourceProvider resources) {

            }

            @Override
            public Void execute(ContextTransaction context) throws PlatformException {
                scheduler.clearJobs();
                return null;
            }
        };
    }

    public void remove() throws Exception {
        log.info("Removing module: " + this.getInfo().getUuid());
        if (getConfig() != null) {
            Files.deleteIfExists(getConfig().getConfigPath());
        }

        for (Class domainObjectClass : new Reflections(getInfo().getUuid()).getTypesAnnotatedWith(Entity.class, true)) {
            StructEntity entity = Schema.getEntity(domainObjectClass);
            getSchema().dropTable(entity.getName(), entity.getNamespace());
        }
    }

    @Override
    public String toString() {
        return "Subsystem (" +
                "uuid: " + getInfo().getUuid() + ", " +
                "id: " + getId() +
                ")";
    }

    public SubsystemScheduler getScheduler() {
        return scheduler;
    }

    public SubsystemMessageSource getMessageSource() {
        return messageSource;
    }

}
