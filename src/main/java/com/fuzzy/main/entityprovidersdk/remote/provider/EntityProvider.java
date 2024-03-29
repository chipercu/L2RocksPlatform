package com.fuzzy.main.entityprovidersdk.remote.provider;

import com.fuzzy.main.cluster.core.remote.struct.ClusterInputStream;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.entityprovidersdk.data.EntityClassInfo;
import com.fuzzy.main.entityprovidersdk.data.EntityFieldInfo;
import org.springframework.lang.NonNull;

import java.util.ArrayList;

public interface EntityProvider {

    ArrayList<EntityClassInfo> getClassInfos() throws PlatformException;

    ArrayList<EntityFieldInfo> getFieldInfos(EntityClassInfo classInfo) throws PlatformException;

    ClusterInputStream getInputStream(@NonNull EntityClassInfo classInfo, @NonNull ArrayList<EntityFieldInfo> fields) throws PlatformException;

}
