package com.fuzzy.subsystem.entityprovidersdk.remote.provider;

import com.fuzzy.cluster.core.remote.struct.ClusterInputStream;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.subsystem.entityprovidersdk.data.EntityClassInfo;
import com.fuzzy.subsystem.entityprovidersdk.data.EntityFieldInfo;
import org.springframework.lang.NonNull;

import java.util.ArrayList;

public interface EntityProvider {

    ArrayList<EntityClassInfo> getClassInfos() throws PlatformException;

    ArrayList<EntityFieldInfo> getFieldInfos(EntityClassInfo classInfo) throws PlatformException;

    ClusterInputStream getInputStream(@NonNull EntityClassInfo classInfo, @NonNull ArrayList<EntityFieldInfo> fields) throws PlatformException;

}
