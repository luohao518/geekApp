package xyz.geekweb.minio;

/**
 * @author jack.luo
 * @date 2020/8/20
 */
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;

import org.apache.commons.io.FileUtils;
import org.xmlpull.v1.XmlPullParserException;

import io.minio.MinioClient;
import io.minio.errors.MinioException;

public class FileUploader {

    public static final String BUCKET_NAME = "amy.zhao--csmfg.com";

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, InvalidKeyException, XmlPullParserException {
        try {
            // 使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
            MinioClient minioClient = new MinioClient("http://27.115.38.42:9000/", "minioadmin", "minioadmin");

            // 检查存储桶是否已经存在
            boolean isExist = minioClient.bucketExists(BUCKET_NAME);
            if(isExist) {
                System.out.println("Bucket already exists.");
            } else {
                // 创建一个名为asiatrip的存储桶，用于存储照片的zip文件。
                minioClient.makeBucket(BUCKET_NAME);
            }

            // 使用putObject上传一个文件到存储桶中。
            minioClient.putObject(BUCKET_NAME,"16862.eml", "D:\\amy.zhao@csmfg.com\\16862.eml");
            System.out.println("uploaded is successfully.");

            InputStream initialStream = minioClient.getObject(BUCKET_NAME, "16862.eml");


            File targetFile = new File("d:\\16862.eml");

            FileUtils.copyInputStreamToFile(initialStream, targetFile);

            System.out.println("read is successfully.");

            minioClient.removeObject(BUCKET_NAME, "16862.eml");

            System.out.println("remove is successfully.");
        } catch(MinioException e) {
            System.out.println("Error occurred: " + e);
        }
    }
}