package org.onetwo.plugins.admin.service;

import org.springframework.web.multipart.MultipartFile;

public interface DictionaryImportService {
	public int importDatas(MultipartFile dataFile);

}