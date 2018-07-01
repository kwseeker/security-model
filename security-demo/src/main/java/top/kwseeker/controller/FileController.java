package top.kwseeker.controller;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.kwseeker.dto.FileInfo;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;

@RestController
@RequestMapping("/file")
public class FileController {

    private String folder = "F:\\mywork\\web_module\\spring-security\\spring-security-model\\security-demo\\";

    /**
     * 文件上传
     * @param file A representation of an uploaded file received in a multipart request.
     * @return  文件路径
     * @throws Exception
     */
    @PostMapping
    public FileInfo upload(//@RequestParam(value = "name", required = false) String name,
                           @RequestParam("file") MultipartFile file) throws Exception {
        System.out.println("file name: " + file.getName() + "\n"
                + "file origin name: " + file.getOriginalFilename() + "\n"
                + "contentType: " + file.getContentType() + "\n"
                + "file size: " + file.getSize());

        File localFile = new File(folder, new Date().getTime() + ".txt");
        file.transferTo(localFile);

        return new FileInfo(localFile.getAbsolutePath());
    }

    @GetMapping("/{id}")
    public void download(@PathVariable String id, HttpServletResponse response) {
        try {
            InputStream inputStream = new FileInputStream(new File(folder, id + ".txt"));
            OutputStream outputStream = response.getOutputStream();

            response.setContentType("application/x-download");
            response.addHeader("Content-Disposition", "attachment;filename=test.txt");

            IOUtils.copy(inputStream, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
