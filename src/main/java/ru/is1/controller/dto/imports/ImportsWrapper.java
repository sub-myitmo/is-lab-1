package ru.is1.controller.dto.imports;

import java.util.List;

public class ImportsWrapper {
    public List<ImportResponse> userImports;
    public long totalCount;
    public int currentPage;
    public int pageSize;

    public ImportsWrapper(List<ImportResponse> userImports, long totalCount, int currentPage, int pageSize) {
        this.userImports = userImports;
        this.totalCount = totalCount;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }
}
