package com.fuzzy.subsystem.core.service.licenseservice;

import com.fuzzy.subsystem.core.license.enums.LicenseParameter;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class LicenseJobService {

    private final AtomicReference<String> setCommonLicenseJobId = new AtomicReference<>(null);

    private final ConcurrentHashMap<LicenseParameter, Set<String>> resetParameterJobs = new ConcurrentHashMap<>();

    public AtomicReference<String> getCommonLicenseJobId() {
        return setCommonLicenseJobId;
    }

    public void addParameterResetJobId(LicenseParameter licenseParameter, String jobId) {
        if (!resetParameterJobs.containsKey(licenseParameter)) {
            resetParameterJobs.put(licenseParameter, ConcurrentHashMap.newKeySet());
        }
        resetParameterJobs.get(licenseParameter).add(jobId);
    }

    public void removeParameterResetJobId(LicenseParameter licenseParameter, String jobId) {
        resetParameterJobs.get(licenseParameter).remove(jobId);
        if (resetParameterJobs.get(licenseParameter).isEmpty()) {
            resetParameterJobs.remove(licenseParameter);
        }
    }

    public ArrayList<String> getAllResetParameterJobs() {
        ArrayList<String> result = new ArrayList<>();
        for (Map.Entry<LicenseParameter, Set<String>> entry : resetParameterJobs.entrySet()) {
            Set<String> jobs = entry.getValue();
            result.addAll(jobs);
        }
        return result;
    }

    public void clearResetParameterJobs() {
        resetParameterJobs.clear();
    }
}
