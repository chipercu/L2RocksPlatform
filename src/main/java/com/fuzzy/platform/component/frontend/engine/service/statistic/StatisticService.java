package com.fuzzy.platform.component.frontend.engine.service.statistic;

public interface StatisticService {

    String ATTRIBUTE_DOWNLOAD_FILE_SIZE = "com.fuzzy.download.file.size";

    /**
     * Возвроаем какой объем данных ожидает отравки(учитываются только передаваемые файлы)
     *
     * @return
     */
    long getQueueDownloadBytes();

}
