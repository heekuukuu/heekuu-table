package heekuu.table.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class S3Uploader {

  private final S3Client s3Client; // AWS S3 클라이언트
  private final String bucketName; // S3 버킷 이름
  private final String region; // AWS 리전

  /**
   * S3Uploader 생성자
   */
  public S3Uploader() {
    String accessKey = System.getenv("S3_ACCESS_KEY"); // AWS 액세스 키
    String secretKey = System.getenv("S3_SECRET_KEY"); // AWS 비밀 키
    this.bucketName = System.getenv("S3_BUCKET_NAME"); // S3 버킷 이름
    this.region = System.getenv("S3_REGION"); // AWS 리전

    AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
    this.s3Client = S3Client.builder()
        .region(Region.of(region)) // 리전 설정
        .credentialsProvider(StaticCredentialsProvider.create(awsCredentials)) // 자격 증명 설정
        .build();
  }

  /**
   * 파일을 S3에 업로드합니다.
   *
   * @param file    업로드할 파일
   * @param dirName 업로드할 디렉터리 이름
   * @return 업로드된 파일의 S3 URL
   * @throws IOException 파일 처리 중 예외
   */
  public String upload(MultipartFile file, String dirName) throws IOException {
    // 파일 경로 생성
    String fileName = dirName + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

    // S3에 업로드할 요청 생성
    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucketName) // S3 버킷 이름
        .key(fileName) // 저장할 파일 경로
        .contentType(file.getContentType()) // 파일의 콘텐츠 타입
        .build();

    // 파일 업로드
    s3Client.putObject(
        putObjectRequest,
        software.amazon.awssdk.core.sync.RequestBody.fromInputStream(
            file.getInputStream(), // 업로드할 파일 스트림
            file.getSize() // 파일 크기
        )

    );

    // 업로드 성공 로그
    log.info("파일 업로드 성공: {}", fileName);

    // S3 URL 반환
    return generateS3Url(fileName);
  }

  /**
   * S3 URL을 생성합니다.
   *
   * @param fileName S3에 저장된 파일 이름
   * @return S3 URL
   */
  private String generateS3Url(String fileName) {

    return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + fileName;

  }
}