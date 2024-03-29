package com.fuzzy.subsystem.core.updatetask;

import com.infomaximum.database.domainobject.Transaction;
import com.infomaximum.database.exception.DatabaseException;
import com.infomaximum.database.schema.Schema;
import com.infomaximum.database.schema.table.TField;
import com.infomaximum.database.schema.table.Table;
import com.infomaximum.platform.update.UpdateTask;
import com.infomaximum.platform.update.annotation.Update;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.updatetask.dataconverterqueries.DataConverterQueries1_0_21;

import java.util.List;

@Update(
        componentUUID = CoreSubsystemConsts.UUID,
        version = "1.0.21.x",
        previousVersion = "1.0.20.x"
)
public class CoreUpdate1_0_21 extends UpdateTask<CoreSubsystem> {

    public CoreUpdate1_0_21(CoreSubsystem subsystem) {
        super(subsystem);
    }

    @Override
    protected void updateComponent(Transaction transaction) throws DatabaseException {
        Schema schema = getSchema(transaction);
        createLicenseTable(schema);
        migrateLicense();
    }

    private void migrateLicense() {
        executeQuery(new DataConverterQueries1_0_21());
    }

    private void createLicenseTable(Schema schema) {

        final TField licenseKeyField = new TField("license_key", byte[].class);

        List<TField> fields = List.of(
                licenseKeyField);

        schema.createTable(new Table(
                "License",
                CoreSubsystemConsts.UUID,
                fields)
        );
    }

}
