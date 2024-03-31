package com.fuzzy.platform.component.frontend.engine.service.statistic.listener;

import com.fuzzy.platform.component.frontend.engine.service.statistic.StatisticService;
import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.Request;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class HttpRequestListener implements HttpChannel.Listener {

    private ConcurrentMap<Request, AtomicLong> fileDownloadRequests;

    public HttpRequestListener() {
        fileDownloadRequests = new ConcurrentHashMap<>();
    }

    @Override
    public void onResponseBegin(Request request) {
        Long downloadFileSize = (Long) request.getAttribute(StatisticService.ATTRIBUTE_DOWNLOAD_FILE_SIZE);
        if (downloadFileSize != null) {
            fileDownloadRequests.put(request, new AtomicLong(downloadFileSize));
        }
    }

    @Override
    public void onResponseContent(Request request, ByteBuffer content) {
        AtomicLong size = fileDownloadRequests.get(request);
        if (size != null) {
            int capacity = content.capacity();
            size.updateAndGet(n -> (n > capacity) ? n - capacity : 0);
        }
    }

    @Override
    public void onComplete(Request request) {
        fileDownloadRequests.remove(request);
    }

    @Override
    public void onResponseFailure(Request request, Throwable failure) {
        fileDownloadRequests.remove(request);
    }

    public long getQueueDownloadBytes() {
        return fileDownloadRequests.values()
                .stream().mapToLong(i -> i.longValue()).sum();
    }
}
