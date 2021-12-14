package nl.novi.uploaddownload.fileUploadDownloadControllers;

import nl.novi.uploaddownload.dto.FileDocument;
import nl.novi.uploaddownload.dto.FileUploadResponse;
import nl.novi.uploaddownload.service.DocFileDao;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
public class UploadDownloadWithDatabaseController {

    private DocFileDao docFileDao;

    public UploadDownloadWithDatabaseController(DocFileDao docFileDao) {
        this.docFileDao = docFileDao;
    }

    @PostMapping("single/uploadDb")
    FileUploadResponse singleFileUpload(@RequestParam("file") MultipartFile file) throws IOException {

        String name = StringUtils.cleanPath(file.getOriginalFilename());
        FileDocument fileDocument = new FileDocument();
        fileDocument.setFileName(name);
        fileDocument.setDocFile(file.getBytes());

        docFileDao.save(fileDocument);

        // next line makes url. example "http://localhost:8080/download/naam.jpg"
        String url = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFromDB/").path(name).toUriString();

        String contentType = file.getContentType();

        FileUploadResponse response = new FileUploadResponse(name, contentType, url );

        return response;
    }

    //    get for single download
    @GetMapping("/downloadFromDB/{fileName}")
    ResponseEntity<byte[]> downLoadSingleFile(@PathVariable String fileName, HttpServletRequest request) {

        FileDocument doc = docFileDao.findByFileName(fileName);

//        this mediaType decides witch type you accept if you only accept 1 type
//        MediaType contentType = MediaType.IMAGE_JPEG;
//        this is going to accept multiple types

        String mimeType = request.getServletContext().getMimeType(doc.getFileName());

//        for download attachment use next line
//        return ResponseEntity.ok().contentType(contentType).header(HttpHeaders.CONTENT_DISPOSITION, "attachment;fileName=" + resource.getFilename()).body(resource);
//        for showing image in browser
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(mimeType)).header(HttpHeaders.CONTENT_DISPOSITION, "inline;fileName=" + doc.getFileName()).body(doc.getDocFile());
    }
}
