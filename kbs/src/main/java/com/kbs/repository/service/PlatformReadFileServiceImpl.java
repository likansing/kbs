package com.kbs.repository.service;

import java.util.Iterator;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kbs.model.Platform;
import com.kbs.repository.PlatformRepository;

@Service
@Transactional
public class PlatformReadFileServiceImpl implements PlatformReadFileService {

	@Autowired
	private PlatformRepository platformRepository;
	
	@Override
	public boolean saveDataFromUploadFile(MultipartFile file) {
		
		boolean isFlag = false;
		String extension = FilenameUtils.getExtension(file.getOriginalFilename());
		
		if(extension.equalsIgnoreCase("xls") || extension.equalsIgnoreCase("xlsx")) {
			isFlag = readDataFromExcel(file);
		}
				
		return isFlag;
	}

	private boolean readDataFromExcel(MultipartFile file) {

		Workbook workbook = getWorkBook(file);
		
		Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rows = sheet.iterator();
		
		rows.next(); //linha do cabecalho
		
		while(rows.hasNext()) {
			Row row = rows.next();
			Platform platform = new Platform();
			
			if(row.getCell(0).getCellType() == Cell.CELL_TYPE_STRING) {
				platform.setPlatformName(row.getCell(0).getStringCellValue());
			}
			
			if(row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING) {
				platform.setMarketName(row.getCell(1).getStringCellValue());
			}
			
			if(row.getCell(2).getCellType() == Cell.CELL_TYPE_STRING) {
				platform.setSysid(row.getCell(2).getStringCellValue());
			} else if(row.getCell(2).getCellType() == Cell.CELL_TYPE_NUMERIC) {
				String sysid = NumberToTextConverter.toText(row.getCell(2).getNumericCellValue());
				platform.setSysid(sysid);
			}
			
			//verify if platform already save in database
			Platform p1 = new Platform();
			Platform p2 = new Platform();
			Platform p3 = new Platform();
			p1 = platformRepository.findPlatformBySysidEqual(platform.getSysid());
			p2 = platformRepository.findPlatformByMarketNameEqual(platform.getMarketName());
			p3 = platformRepository.findPlatformByPlatformNameEqual(platform.getPlatformName());
			
			 
			 if(p1 == null && p2 == null && p3 == null) {
				 platformRepository.save(platform);
			 }
			
		}
		return true;
	}

	private Workbook getWorkBook(MultipartFile file) {

		Workbook workbook = null;
		String extension = FilenameUtils.getExtension(file.getOriginalFilename());
		
		try {
			if(extension.equalsIgnoreCase("xlsx")) {
				workbook = new XSSFWorkbook(file.getInputStream());
			} else if (extension.equalsIgnoreCase("xls")) {
				workbook = new HSSFWorkbook(file.getInputStream());				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return workbook;
	}

}
