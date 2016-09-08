package net.datatp.crawler.rest;

import java.util.Iterator;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RestController
@CrossOrigin(origins = "*")
public class UploadController {
  @RequestMapping(value = "/service/upload", method = RequestMethod.POST)
  public String upload(MultipartHttpServletRequest request) throws Exception {
    Iterator<String> itr = request.getFileNames();
    while(itr.hasNext()) {
      MultipartFile mFile = request.getFile(itr.next());
      String fileName = mFile.getOriginalFilename();
      System.out.println("File: name = " +fileName + ", length = " + mFile.getBytes().length + ", type = " + mFile.getContentType());
      
    }
    return "{ message: 'File Uploaded successfully.'}";
  }
}