package com.kbs.repository.service;

import org.springframework.web.multipart.MultipartFile;

public interface PlatformReadFileService {

	boolean saveDataFromUploadFile(MultipartFile file);


}
