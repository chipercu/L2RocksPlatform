package com.fuzzy.subsystems.security;

import com.fuzzy.main.Subsystems;
import com.fuzzy.platform.sdk.context.Context;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystems.security.build.SyslogStructDataBuilder;
import com.fuzzy.subsystems.security.struct.data.SyslogStructData;
import com.fuzzy.subsystems.security.struct.data.event.SyslogStructDataEvent;
import com.fuzzy.subsystems.security.struct.data.source.SyslogStructDataSource;
import com.fuzzy.subsystems.security.struct.data.system.SyslogStructDataSystem;
import com.fuzzy.subsystems.security.struct.data.target.SyslogStructDataTarget;
import com.fuzzy.subsystems.syslog.*;
import com.fuzzy.subsystems.utils.ProcessInfoUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Основано на спецификации: RFC 5424
 */
public class SecurityLog {

    private static final Logger log = LoggerFactory.getLogger(SecurityLog.class);

    private static final String ENTERPRISE_ID = "729368";       // должно быть ASCII max 128 символов
    private static final String PROCESS_NAME = "L2RocksPlatform";   // должно быть ASCII max 48 символов
    private static final String HOST_NAME = getHostName();
    private static final String PROCESS_PID = getProcId();

    private final static AtomicInteger sequenceId = new AtomicInteger(1);

    public static void info(SyslogStructDataEvent event, SyslogStructDataTarget target, Context context) {
        String str = getInfo(event, target, context);
        if (context instanceof ContextTransaction &&
                !((ContextTransaction) context).getTransaction().closed()) {
            ContextTransaction contextTransaction = (ContextTransaction) context;
            contextTransaction.getTransaction().addCommitListener(() -> log.info(str));
        } else {
            log.info(str);
        }
    }


    public static String getInfo(SyslogStructDataEvent event, SyslogStructDataTarget target, @NonNull Context context) {
        Header header = new Header(
                Facility.SECURITY_AUTHORIZATION_MESSAGES,
                Severity.NOTICE,
                ZonedDateTime.now(),
                HOST_NAME,
                PROCESS_NAME,
                PROCESS_PID,
                event != null ? event.getType() : null
        );
        StructuredData structuredData = new StructuredData();
        structuredData.addElement(
                Meta.ofSequenceId(sequenceId.getAndUpdate(operand -> operand == Integer.MAX_VALUE ? 1 : operand + 1))
        );
        if (Subsystems.getInstance() != null && Subsystems.getInstance().getPlatform() != null && Subsystems.getInstance().getCluster() != null) {
            structuredData.addElement(buildSdElement(new SyslogStructDataSystem()));
        }

        SyslogStructDataSource syslogStructDataSource;
        syslogStructDataSource = SyslogStructDataBuilder.build(context);

        structuredData.addElement(buildSdElement(syslogStructDataSource));

        if (event != null) {
            structuredData.addElement(buildSdElement(event));
        }
        if (target != null) {
            structuredData.addElement(buildSdElement(target));
        }

        return SysLog.buildMessage(header, structuredData);
    }

    private static UserSdElement buildSdElement(SyslogStructData structData) {
        UserSdElement sdElement = new UserSdElement(structData.getName() + '@' + ENTERPRISE_ID);
        for (Map.Entry<String, String> entry : structData.getData().entrySet()) {
            sdElement.addParam(entry.getKey(), entry.getValue());
        }
        return sdElement;
    }

    private static String getHostName() {
        if (ProcessInfoUtils.getPID() != null) {
            String hostName = ProcessInfoUtils.getFQDN();
            if (SysLogUtils.isAsciiAndCorrectLength(hostName, 255)) {
                return hostName;
            }
        }
        return null;
    }

    private static String getProcId() {
        if (ProcessInfoUtils.getPID() != null) {
            String procId = ProcessInfoUtils.getPID();
            if (SysLogUtils.isAsciiAndCorrectLength(procId, 128)) {
                return procId;
            }
        }
        return null;
    }
}
