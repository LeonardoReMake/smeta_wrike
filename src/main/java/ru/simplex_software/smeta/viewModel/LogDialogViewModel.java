package ru.simplex_software.smeta.viewModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.ExecutionArgParam;
import ru.simplex_software.smeta.util.ImportInfo;

public class LogDialogViewModel {
    private static final Logger LOG = LoggerFactory.getLogger(LogDialogViewModel.class);

    private ImportInfo importInfo;

    @AfterCompose
    public void afterCompose(@ExecutionArgParam("info") ImportInfo info) {
        this.importInfo = info;
    }

    public ImportInfo getImportInfo() {
        return importInfo;
    }

    public void setImportInfo(ImportInfo importInfo) {
        this.importInfo = importInfo;
    }
}
