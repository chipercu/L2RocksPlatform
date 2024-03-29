package com.fuzzy.main.cluster;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NetworkConfig {

    private final String name;
    private final Integer port;
    private final Path certChainPath;
    private final Path privateKeyPath;

    private final List<Path> trustCertificates;

    private final List<String> nodes;

    private NetworkConfig(String name, Integer port, Path certChainPath, Path privateKeyPath, List<Path> trustCertificates, List<String> nodes) {
        this.name = name;
        this.port = port;
        this.certChainPath = certChainPath;
        this.privateKeyPath = privateKeyPath;
        this.trustCertificates = trustCertificates;
        this.nodes = nodes;
    }

    public String getName() {
        return name;
    }

    public Integer getPort() {
        return port;
    }

    public boolean isSSL() {
        return (certChainPath != null);
    }

    public byte[] getFileContentCertChain() throws IOException {
        return Files.readAllBytes(certChainPath);
    }

    public byte[] getFileContentPrivateKey() throws IOException {
        return Files.readAllBytes(privateKeyPath);
    }

    public byte[][] getFileContentTrustCertificates() throws IOException {
        byte[][] contents = new byte[trustCertificates.size()][];
        for (int i = 0; i < trustCertificates.size(); i++) {
            contents[i] = Files.readAllBytes(trustCertificates.get(i));
        }
        return contents;
    }

    public List<String> getNodes() {
        return nodes;
    }

    public static NetworkConfig load(JSONObject json, Path dataDir) {
        JSONObject jCurrent = (JSONObject) json.get("current");
        String name = null;
        Integer port = null;
        Path certChainPath = null;
        Path privateKeyPath = null;
        List<Path> trustCertificates = Collections.emptyList();

        if (jCurrent != null) {
            name = (jCurrent.containsKey("name")) ? jCurrent.get("name").toString().trim() : null;
            port = (jCurrent.containsKey("port")) ? jCurrent.getAsNumber("port").intValue() : null;

            JSONObject jSsl = (JSONObject) jCurrent.get("ssl");
            if (jSsl != null) {
                certChainPath = getFilePath(jSsl.getAsString("cert_chain_path"), dataDir);
                privateKeyPath = getFilePath(jSsl.getAsString("private_key_path"), dataDir);
                JSONArray jTrustCerts = (JSONArray) jSsl.get("trust_certs");
                if (jTrustCerts != null) {
                    List<Path> certificates = new ArrayList<>();
                    for (Object oTrustCert : jTrustCerts) {
                        Path trustCert = getFilePath((String) oTrustCert, dataDir);
                        certificates.add(trustCert);
                    }
                    trustCertificates = Collections.unmodifiableList(certificates);
                }
            }
        }

        //load nodes
        JSONArray jsonNodes = (JSONArray) json.get("nodes");
        List<String> nodes = new ArrayList<>();
        for (Object item : jsonNodes) {
            if (item instanceof JSONObject jNode) {//TODO удалить после 01.05.2024
                String target = jNode.getAsString("target");
                nodes.add(target);
            } else if (item instanceof String target) {
                nodes.add(target);
            } else {
                throw new RuntimeException("Unknown configuration");
            }
        }

        return new NetworkConfig(name, port, certChainPath, privateKeyPath, trustCertificates, nodes);
    }

    private static Path getFilePath(String path, Path dataDir) {
        Path result = Paths.get(path);
        if (!result.isAbsolute()) {
            result = dataDir.resolve(result).toAbsolutePath();
        }
        if (Files.isDirectory(result) || !Files.exists(result)) {
            throw new RuntimeException("File: " + result + " is not fount");
        }

        long size;
        try {
            size = Files.size(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (size == 0) {
            throw new RuntimeException("File: " + result + " is empty");
        }
        return result;
    }
}
