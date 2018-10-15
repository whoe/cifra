package com.groupstp.cifra.service;

public interface UploadedFilesFinderService {
    String NAME = "cifra_UploadedFilesFinderService";

    /**
     *
     * <p>выбирая в cifra$Document строки с file_id == null,
     * используя external_link, находит соотвествующие fileId
     * (требуется компонент google-drive).</p>
     * <p>Меняет колонку file_id таблицы cifra$Document</p>
     * <p>Создает FileDescriptor-ы (таблица sys$File)</p>
    */
    void find();
}
