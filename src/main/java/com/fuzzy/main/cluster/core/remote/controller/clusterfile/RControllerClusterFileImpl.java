package com.fuzzy.main.cluster.core.remote.controller.clusterfile;

import com.fuzzy.main.cluster.core.remote.AbstractRController;
import com.fuzzy.main.cluster.core.remote.struct.ClusterInputStream;
import com.fuzzy.main.cluster.struct.Component;
import com.fuzzy.main.cluster.struct.storage.SourceClusterFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by kris on 02.11.16.
 */
public class RControllerClusterFileImpl extends AbstractRController<Component> implements RControllerClusterFile {

    private final List<SourceClusterFile> sources;

    private RControllerClusterFileImpl(Component component, List<SourceClusterFile> sources) {
        super(component);
        this.sources = Collections.unmodifiableList(sources);
    }

    @Override
    public long getSize(String clusterFileUUID) throws IOException {
        for (SourceClusterFile source : sources) {
            if (source.contains(clusterFileUUID)) {
                return source.getSize(clusterFileUUID);
            }
        }
        throw new FileNotFoundException("File not found: " + clusterFileUUID);
    }

    @Override
    public ClusterInputStream getInputStream(String clusterFileUUID) throws IOException {
        for (SourceClusterFile source : sources) {
            if (source.contains(clusterFileUUID)) {
                return new ClusterInputStream(source.getInputStream(clusterFileUUID));
            }
        }
        throw new FileNotFoundException("File not found: " + clusterFileUUID);
    }

    @Override
    public void delete(String clusterFileUUID) throws IOException {
        for (SourceClusterFile source : sources) {
            if (source.contains(clusterFileUUID)) {
                source.delete(clusterFileUUID);
                return;
            }
        }
        throw new FileNotFoundException("File not found: " + clusterFileUUID);
    }

    @Override
    public void deleteIfExists(String clusterFileUUID) throws IOException {
        for (SourceClusterFile source : sources) {
            if (source.contains(clusterFileUUID)) {
                source.deleteIfExists(clusterFileUUID);
                return;
            }
        }
    }

    public static class Builder {

        private final Component component;
        private final List<SourceClusterFile> sources;

        public Builder(Component component, SourceClusterFile source) {
            if (component == null || source == null) throw new IllegalArgumentException();

            this.component = component;

            this.sources = new ArrayList<SourceClusterFile>();
            this.sources.add(source);
        }

        public Builder withSource(SourceClusterFile source) {
            if (source == null) throw new IllegalArgumentException();
            sources.add(source);
            return this;
        }

        public RControllerClusterFileImpl build() {
            return new RControllerClusterFileImpl(component, sources);
        }
    }
}


