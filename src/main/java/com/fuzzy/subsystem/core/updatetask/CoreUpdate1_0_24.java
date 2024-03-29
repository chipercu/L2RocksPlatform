package com.fuzzy.subsystem.core.updatetask;

import com.fuzzy.main.rdao.database.anotation.Entity;
import com.fuzzy.main.rdao.database.domainobject.Transaction;
import com.fuzzy.main.rdao.database.exception.DatabaseException;
import com.fuzzy.main.rdao.database.schema.Schema;
import com.fuzzy.main.rdao.database.schema.table.TField;
import com.fuzzy.main.platform.update.UpdateTask;
import com.fuzzy.main.platform.update.annotation.Update;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.domainobject.additionalfield.AdditionalFieldReadable;

@Update(
        componentUUID = CoreSubsystemConsts.UUID,
        version = "1.0.24.x",
        previousVersion = "1.0.23.x"
)
public class CoreUpdate1_0_24 extends UpdateTask<CoreSubsystem> {

    public CoreUpdate1_0_24(CoreSubsystem subsystem) {
        super(subsystem);
    }

    @Override
    protected void updateComponent(Transaction transaction) throws DatabaseException {
        final Schema schema = getSchema(transaction);
        final Entity entity = AdditionalFieldReadable.class.getAnnotation(Entity.class);
        final TField listSourceField = new TField("list_source", String.class);
        final TField orderField = new TField("order", Integer.class);
        schema.createField(listSourceField, entity.name(), entity.namespace());
        schema.createField(orderField, entity.name(), entity.namespace());
    }
}
