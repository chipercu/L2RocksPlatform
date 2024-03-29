package com.fuzzy.subsystem.core.updatetask;

import com.infomaximum.database.anotation.Entity;
import com.infomaximum.database.domainobject.Transaction;
import com.infomaximum.database.exception.DatabaseException;
import com.infomaximum.database.schema.Schema;
import com.infomaximum.database.schema.table.TField;
import com.infomaximum.platform.update.UpdateTask;
import com.infomaximum.platform.update.annotation.Update;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.domainobject.apikey.ApiKeyReadable;

@Update(
        componentUUID = CoreSubsystemConsts.UUID,
        version = "1.0.25.x",
        previousVersion = "1.0.24.x"
)
public class CoreUpdate1_0_25 extends UpdateTask<CoreSubsystem> {

    public CoreUpdate1_0_25(CoreSubsystem subsystem) {
        super(subsystem);
    }

    @Override
    protected void updateComponent(Transaction transaction) throws DatabaseException {
        final Schema schema = getSchema(transaction);
        final Entity entity = ApiKeyReadable.class.getAnnotation(Entity.class);
        final TField contentField = new TField("content", byte[].class);
        schema.createField(contentField, entity.name(), entity.namespace());
    }
}
