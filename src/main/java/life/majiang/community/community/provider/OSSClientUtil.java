package life.majiang.community.community.provider;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.StorageClass;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;
@Component
public class OSSClientUtil {

    @Value("${oss.endpoint}")
    private String endpoint;
    //阿里云OSS账号
    @Value("${oss.accessKeyId}")
    private String accessKeyId;
    //阿里云OSS密钥
    @Value("${oss.accessKeySecret}")
    private String accessKeySecret;
    //阿里云OSS上的存储块bucket名字
    @Value("${oss.bucketName}")
    private String bucketName;
    //阿里云图片文件存储目录
    @Value("${oss.filedir}")
    private String fileDir;

    private OSS ossClient;

    public void init(){
    }
    public void close(){
        ossClient.shutdown();
    }
    public String uploadFile(File file) {
// 创建OSSClient实例。
        try{
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        }catch (Exception e){
            e.printStackTrace();
        }
// 创建PutObjectRequest对象。
// 依次填写Bucket名称（例如examplebucket）、Object完整路径（例如exampledir/exampleobject.txt）和本地文件的完整路径。Object完整路径中不能包含Bucket名称。
// 如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件。
        String fileKey = fileDir +
                UUID.randomUUID().toString().replace("-", "") + "-" + file.getName();
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileKey, file);

// 如果需要上传时设置存储类型和访问权限，请参考以下示例代码。
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
        metadata.setObjectAcl(CannedAccessControlList.PublicRead);
        putObjectRequest.setMetadata(metadata);
        System.out.println("1");

// 上传文件。
        ossClient.putObject(putObjectRequest);
// 设置访问url
        String returnFileAccessUrl = "https://" + bucketName + "." + endpoint + "/" + fileKey;
// 关闭OSSClient。
        ossClient.shutdown();
        return returnFileAccessUrl;
    }

}
