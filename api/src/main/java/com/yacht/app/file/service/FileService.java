package com.yacht.app.file.service;

import com.yacht.app.file.domain.File;
import com.yacht.app.file.domain.FileRepository;
import com.yacht.app.file.dto.FileDto;
import com.yacht.app.file.dto.FileDtoMapper;
import com.yacht.aws.s3.service.AwsS3Service;
import com.yacht.exception.BadRequestException;
import com.yacht.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FileService {

  private final FileRepository fileRepository;
  private final AwsS3Service awsS3Service;

  public FileDto.Response getDetail(Long id) {
    File file = fileRepository.findByIdAndStatus(id, File.Status.DONE).orElseThrow(() -> new NotFoundException("File 을 찾을 수 없습니다"));
    return FileDtoMapper.INSTANCE.toResponse(file);
  }

  public FileDto.Response upload(MultipartFile multipartFile) {
    LocalDateTime now = LocalDateTime.now();
    File file = new File();
    file.setName(multipartFile.getOriginalFilename());
    file.setContentType(multipartFile.getContentType());
    file.setSize(multipartFile.getSize());
    file.setServerPath(now.format(DateTimeFormatter.ofPattern("yyyy/MM")));
    file.setExtension(FilenameUtils.getExtension(multipartFile.getOriginalFilename()));

    fileRepository.save(file);
    Path path = Paths.get(file.getServerPath());
    try {
      awsS3Service.uploadFile(multipartFile, path);
    } catch (IOException e) {
      file.setStatus(File.Status.ERROR);
      fileRepository.save(file);
      throw new RuntimeException(e);
    }

    file.setStatus(File.Status.DONE);
    file = fileRepository.save(file);

    return FileDtoMapper.INSTANCE.toResponse(file);
  }

  public FileDto.CreateMultipartUploadResponse createMultipartUpload(FileDto.CreateMultipartUpload request) {
    LocalDateTime now = LocalDateTime.now();
    File file = new File();
    file.setName(request.getName());
    file.setContentType(request.getContentType());
    file.setSize(request.getSize());
    file.setServerPath(now.format(DateTimeFormatter.ofPattern("yyyy/MM")));
    file.setExtension(FilenameUtils.getExtension(request.getName()));
    file.setStatus(File.Status.READY);

    file = fileRepository.save(file);

    Path path = Path.of(file.getServerPath());

    CreateMultipartUploadResponse createMultipartUploadResponse = awsS3Service.createMultipartUpload(path);

    file.setUploadId(createMultipartUploadResponse.uploadId());

    file = fileRepository.save(file);

    return FileDtoMapper.INSTANCE.toCreateMultipartUploadResponse(file);
  }

  public FileDto.UploadPartResponse uploadPart(Long id, MultipartFile multipartFile, Integer partNumber) {
    File file = fileRepository.findById(id).orElseThrow(() -> new NotFoundException("File 을 찾을 수 없습니다"));

    Path path = Path.of(file.getServerPath());

    if (!List.of(File.Status.READY, File.Status.PROGRESS).contains(file.getStatus())) {
      throw new BadRequestException("Part 를 Upload 할 수 없는 상태입니다");
    }

    if (file.getStatus().equals(File.Status.READY)) {
      file.setStatus(File.Status.PROGRESS);
      fileRepository.save(file);
    }

    FileDto.UploadPartResponse response = new FileDto.UploadPartResponse();

    try {
      UploadPartResponse uploadPartResponse = awsS3Service.uploadPart(path, file.getUploadId(), multipartFile, partNumber);
      response.setEtag(uploadPartResponse.eTag());
    } catch (Exception e) {
      file.setStatus(File.Status.ERROR);
      fileRepository.save(file);
      throw new RuntimeException(e);
    }

    return response;
  }

  public FileDto.Response completeMultipartUpload(Long id, FileDto.CompleteMultipartUpload request) {
    File file = fileRepository.findById(id).orElseThrow(() -> new NotFoundException("File 을 찾을 수 없습니다"));

    if (!file.getStatus().equals(File.Status.PROGRESS)) {
      throw new BadRequestException("Part 를 Complete 할 수 없는 상태입니다");
    }

    Path path = Path.of(file.getServerPath());

    List<CompletedPart> completedParts = request.getParts().stream().map((x) -> CompletedPart.builder().partNumber(x.getPartNumber()).eTag(x.getEtag()).build()).toList();

    try {
      awsS3Service.completeMultipartUpload(path, file.getUploadId(), completedParts);
      file.setStatus(File.Status.DONE);
    } catch (Exception e) {
      file.setStatus(File.Status.ERROR);
      fileRepository.save(file);
      throw new RuntimeException(e);
    }

    file = fileRepository.save(file);

    return FileDtoMapper.INSTANCE.toResponse(file);
  }

}
