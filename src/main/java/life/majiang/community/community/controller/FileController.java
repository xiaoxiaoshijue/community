package life.majiang.community.community.controller;

import life.majiang.community.community.dto.FileDTO;
import life.majiang.community.community.exception.ErrorCodeEnum;
import life.majiang.community.community.exception.CustomizeException;
import life.majiang.community.community.provider.OSSClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;


@Controller
public class FileController {

    @Autowired
    private OSSClientUtil ossClientUtil;

    @RequestMapping("/file/upload")
    @ResponseBody
    public FileDTO upload(@RequestParam("editormd-image-file") MultipartFile file) {
        String filename = file.getOriginalFilename();
        System.out.println(filename);
        File newFile = new File(filename);
        String url = "";
        try {
            FileOutputStream outputStream = new FileOutputStream(newFile);
            outputStream.write(file.getBytes());
            outputStream.close();
            file.transferTo(newFile);
            url = ossClientUtil.uploadFile(newFile);
        } catch (Exception e) {
            throw new CustomizeException(ErrorCodeEnum.FILE_NOT_FOUND);
        }
        FileDTO fileDTO = new FileDTO();
        fileDTO.setSuccess(1);
        fileDTO.setUrl(url);
        return fileDTO;
    }

    /*
  #### Settings

```javascript
{
    imageUpload    : false,
    imageFormats   : ["jpg", "jpeg", "gif", "png", "bmp", "webp"],
    imageUploadURL : "./php/upload.php",
}
```

#### JSON data

```json
{
    success : 0 | 1,           // 0 表示上传失败，1 表示上传成功
    message : "提示的信息，上传成功或上传失败及错误信息等。",
    url     : "图片地址"        // 上传成功时才返回
}
```*/
}
