package com.fuzzy.subsystem.core.remote.department;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.*;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.infomaximum.platform.sdk.function.Consumer;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.domainobject.department.DepartmentEditable;
import com.fuzzy.subsystem.core.domainobject.department.DepartmentReadable;
import com.fuzzy.subsystem.core.grouping.DepartmentGrouping;
import com.fuzzy.subsystem.core.securitylog.CoreEvent;
import com.fuzzy.subsystem.core.securitylog.CoreParameter;
import com.fuzzy.subsystem.core.securitylog.CoreTarget;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.remote.RemovalData;
import com.fuzzy.subsystems.security.SecurityLog;
import com.fuzzy.subsystems.security.struct.data.event.SyslogStructDataEvent;
import com.fuzzy.subsystems.security.struct.data.target.SyslogStructDataTarget;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class RControllerDepartmentControlImpl extends AbstractQueryRController<CoreSubsystem>
        implements RControllerDepartmentControl {

    private ReadableResource<DepartmentReadable> departmentReadableResource;
    private RemovableResource<DepartmentEditable> departmentEditableResource;
    private DepartmentGrouping departmentGrouping;
    private Set<RControllerDepartmentNotification> rControllerDepartmentNotifications;
    private Set<RControllerDepartmentLogNotification> rControllerDepartmentLogNotifications;

    public RControllerDepartmentControlImpl(CoreSubsystem subSystem, ResourceProvider resources) {
        super(subSystem, resources);
        departmentReadableResource = resources.getReadableResource(DepartmentReadable.class);
        departmentEditableResource = resources.getRemovableResource(DepartmentEditable.class);
        departmentGrouping = new DepartmentGrouping(resources);
        rControllerDepartmentNotifications =
                resources.getQueryRemoteControllers(RControllerDepartmentNotification.class);
        rControllerDepartmentLogNotifications =
                resources.getQueryRemoteControllers(RControllerDepartmentLogNotification.class);
    }

    @Override
    public DepartmentReadable create(DepartmentBuilder departmentBuilder, ContextTransaction context)
            throws PlatformException {
        DepartmentEditable departmentEditable = departmentEditableResource.create(context.getTransaction());
        setFieldsFor(departmentEditable, departmentBuilder, context.getTransaction());
        departmentEditableResource.save(departmentEditable, context.getTransaction());

        SecurityLog.info(
                new SyslogStructDataEvent(CoreEvent.Department.TYPE_CREATE)
                        .withParam(CoreParameter.Department.NAME, departmentEditable.getName())
                        .withParam(CoreParameter.Department.ID, String.valueOf(departmentEditable.getId())),
                new SyslogStructDataTarget(CoreTarget.TYPE_DEPARTMENT, departmentEditable.getId())
                .withParam(CoreParameter.Department.NAME, departmentEditable.getName()),
                context
        );

        return departmentEditable;
    }

    @Override
    public DepartmentReadable update(
            long departmentId,
            DepartmentBuilder departmentBuilder,
            ContextTransaction context
    ) throws PlatformException {
        DepartmentEditable departmentEditable = departmentEditableResource.get(departmentId, context.getTransaction());
        if (departmentEditable == null) {
            throw GeneralExceptionBuilder.buildNotFoundDomainObjectException(DepartmentEditable.class, departmentId);
        }
        for (RControllerDepartmentNotification rcDepartmentNotification : rControllerDepartmentNotifications) {
            rcDepartmentNotification.onBeforeUpdateDepartment(departmentEditable, departmentBuilder, context);
        }
        boolean parentChanged = departmentBuilder.isContainParentId() &&
                !Objects.equals(departmentBuilder.getParentId(), departmentEditable.getParentDepartmentId());
        String oldName = departmentEditable.getName();
        setFieldsFor(departmentEditable, departmentBuilder, context.getTransaction());
        if (parentChanged) {
            notifyLog(rControllerDepartmentLogNotification ->
                    rControllerDepartmentLogNotification.startChangeParentDepartment(departmentId, context));
        }
        departmentEditableResource.save(departmentEditable, context.getTransaction());
        if (parentChanged) {
            for (RControllerDepartmentNotification rControllerDepartmentNotification :
                    rControllerDepartmentNotifications) {
                rControllerDepartmentNotification.onAfterUpdateParentDepartment(departmentId, context);
            }
            notifyLog(rControllerDepartmentLogNotification ->
                    rControllerDepartmentLogNotification.endChangeParentDepartment(departmentId, context));
        }
        if (departmentBuilder.isContainName() && !Objects.equals(oldName, departmentBuilder.getName())) {
            SecurityLog.info(
                    new SyslogStructDataEvent(CoreEvent.Department.TYPE_UPDATE)
                            .withParam(CoreParameter.Department.OLD_NAME, oldName)
                            .withParam(CoreParameter.Department.NEW_NAME, departmentBuilder.getName())
                            .withParam(CoreParameter.Department.ID, String.valueOf(departmentEditable.getId())),
                    new SyslogStructDataTarget(CoreTarget.TYPE_DEPARTMENT, departmentEditable.getId())
                            .withParam(CoreParameter.Department.NAME, oldName),
                    context
            );
        }
        return departmentEditable;
    }

    @Override
    public RemovalData removeWithCauses(HashSet<Long> departmentIds, ContextTransaction context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        RemovalData removalData = new RemovalData();
        HashSet<Long> notFound = new HashSet<>();
        HashSet<Long> notEmpty = new HashSet<>();
        for (Long departmentId : departmentIds) {
            if (departmentId == null) {
                continue;
            }
            DepartmentEditable departmentEditable = departmentEditableResource.get(departmentId, transaction);
            if (departmentEditable == null) {
                notFound.add(departmentId);
                continue;
            }
            if (!departmentGrouping.getChildItemsRecursively(departmentId, transaction).isEmpty()) {
                notEmpty.add(departmentId);
                continue;
            }
            HashSet<Long> childDepartments = departmentGrouping.getChildNodes(departmentId, transaction);
            if (!childDepartments.isEmpty()) {
                removeWithCauses(childDepartments, context);
            }
            for (RControllerDepartmentNotification rControllerDepartmentNotification :
                    rControllerDepartmentNotifications) {
                rControllerDepartmentNotification.onBeforeRemoveDepartment(departmentId, context);
            }
            departmentEditableResource.remove(departmentEditable, transaction);
            removalData.getRemoved().add(departmentId);

            SecurityLog.info(
                    new SyslogStructDataEvent(CoreEvent.Department.TYPE_REMOVE)
                            .withParam(CoreParameter.Department.NAME, departmentEditable.getName())
                            .withParam(CoreParameter.Department.ID, String.valueOf(departmentEditable.getId())),
                    new SyslogStructDataTarget(CoreTarget.TYPE_DEPARTMENT, departmentEditable.getId())
                            .withParam(CoreParameter.Department.NAME, departmentEditable.getName()),
                    context
            );
        }
        removalData.addNonRemoved(GeneralExceptionBuilder.NOT_FOUND_DOMAIN_OBJECT_CODE, notFound);
        removalData.addNonRemoved(GeneralExceptionBuilder.NOT_EMPTY_DOMAIN_OBJECT_CODE, notEmpty);
        return removalData;
    }

    private void setFieldsFor(
            DepartmentEditable targetDepartment,
            DepartmentBuilder departmentBuilder,
            QueryTransaction transaction
    ) throws PlatformException {
        if (departmentBuilder.isContainName()) {
            targetDepartment.setName(departmentBuilder.getName());
        }
        if (departmentBuilder.isContainParentId()) {
            setParentId(targetDepartment, departmentBuilder.getParentId(), transaction);
        }
    }

    private void setParentId(DepartmentEditable targetDepartment, Long parentId, QueryTransaction transaction)
            throws PlatformException {
        if (parentId != null) {
            DepartmentReadable parentDepartment = departmentReadableResource.get(parentId, transaction);
            if (parentDepartment == null) {
                throw GeneralExceptionBuilder.buildNotFoundDomainObjectException(DepartmentEditable.class, parentId);
            }
            if (parentId.equals(targetDepartment.getId())) {
                throw GeneralExceptionBuilder.buildHierarchyException(
                        DepartmentReadable.class, DepartmentReadable.FIELD_PARENT_ID, parentId);
            }
            boolean[] valid = {true};
            departmentGrouping.forEachParentOfNodeRecursively(parentId, transaction, currentParentId -> {
                valid[0] = targetDepartment.getId() != currentParentId;
                return valid[0];
            });
            if (!valid[0]) {
                throw GeneralExceptionBuilder.buildHierarchyException(
                        DepartmentReadable.class, DepartmentReadable.FIELD_PARENT_ID, parentId);
            }
        }
        targetDepartment.setParentId(parentId);
    }

    private void notifyLog(Consumer<RControllerDepartmentLogNotification> handler) throws PlatformException {
        for (RControllerDepartmentLogNotification rControllerDepartmentLogNotification :
                rControllerDepartmentLogNotifications) {
            handler.accept(rControllerDepartmentLogNotification);
        }
    }
}
