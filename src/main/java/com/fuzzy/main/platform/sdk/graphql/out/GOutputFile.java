package com.fuzzy.main.platform.sdk.graphql.out;

import com.fuzzy.main.cluster.core.io.ClusterFile;
import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.platform.component.frontend.struct.MimeType;
import com.fuzzy.main.platform.component.frontend.utils.MimeTypeUtils;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GOutputFile implements RemoteObject {

    public final String fileName;

    public final URI uri;
    public final byte[] body;

    public final MimeType mimeType;

    public final boolean temp;

    public final boolean cache;

    private GOutputFile(Builder builder) {
        this.fileName = builder.fileName;

        this.uri = builder.uri;
        this.body = builder.body;

        this.mimeType = builder.mimeType;

        this.temp = builder.temp;

        this.cache = builder.cache;
    }

    public long getSize() {
        if (uri != null) {
            try {
                return Files.size(Paths.get(uri));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (body != null) {
            return body.length;
        } else {
            throw new RuntimeException("Not support mode");
        }
    }

    public static class Builder {

        private String fileName;

        private URI uri;
        private byte[] body;

        private MimeType mimeType;

        private boolean temp = false;
        private boolean cache = false;

        public Builder(String fileName, ClusterFile clusterFile) {
            this.fileName = fileName;
            this.uri = clusterFile.getUri();
            this.mimeType = MimeTypeUtils.findAutoMimeType(fileName);
        }

        public Builder(String fileName, byte[] body) {
            this.fileName = fileName;
            this.body = body;
            this.mimeType = MimeTypeUtils.findAutoMimeType(fileName);
        }

        public Builder withMimeType(MimeType mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public Builder withTemp(boolean temp) {
            this.temp = temp;
            return this;
        }

        public Builder withCache(boolean cache) {
            this.cache = cache;
            return this;
        }

        public GOutputFile build(){
            return new GOutputFile(this);
        }
    }
}
