package eionet.datalake.service;

import eionet.datalake.dao.DatasetService;
import eionet.datalake.dao.QATestService;
import eionet.datalake.dao.TestResultService;
import eionet.datalake.dao.EditionsService;
import eionet.datalake.model.Dataset;
import eionet.datalake.model.QATest;
import eionet.datalake.model.QATestType;
import eionet.datalake.model.TestResult;
import eionet.datalake.model.Edition;
import eionet.datalake.util.Filenames;
import eionet.datalake.util.UniqueId;
import java.io.IOException;
import java.util.List;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


/**
 * Service for handling uploads
 */

@Service
public class UploadService {

    @Autowired
    private DatasetService datasetService;

    @Autowired
    private EditionsService editionsService;

    /**
     * Service for QA Test storage.
     */
    @Autowired
    private QATestService qaTestService;

    @Autowired
    private QATestRunService qaTestRunService;

    @Autowired
    private TestResultService testResultService;

    /**
     * Handle an upload. If there is no datasetId, then create one.
     * FIXME: If there is a datasetId, then it must already exist the database.
     */
    public String uploadFile(MultipartFile myFile, String datasetId) throws IOException {
        String editionId = UniqueId.generateEditionId();
        boolean knownDataset = true;
        if (datasetId == null || "".equals(datasetId)) {
            datasetId = UniqueId.generateDatasetId();
            knownDataset = false;
            Dataset datasetRec = new Dataset();
            datasetRec.setDatasetId(datasetId);
            String fileName = myFile.getOriginalFilename();
            String datasetTitle;
            if (fileName == null || "".equals(fileName)) {
                datasetTitle = "Unnamed dataset";
            } else {
                datasetTitle = Filenames.removePath(myFile.getOriginalFilename());
                if (datasetTitle.toLowerCase().endsWith(".mdb") || datasetTitle.toLowerCase().endsWith(".accdb")) {
                    datasetTitle = datasetTitle.substring(0, datasetTitle.lastIndexOf("."));
                }
            }
            datasetRec.setTitle(datasetTitle);
            datasetRec.setLatestEdition(editionId);
            datasetService.save(datasetRec);
        }
        editionsService.storeFile(myFile, editionId, datasetId);
        if (knownDataset) {
            qaTestRunService.runQAOnEdition(editionId);
        }
        datasetService.updateToLatest(datasetId);
        return editionId;
    }

}
