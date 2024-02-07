package com.scm.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.web.multipart.MultipartFile;

public class UploadHelper {

	public static void createDir(File dir) {
		if(!dir.exists()) {
			System.out.println(dir + " does not exists");
			if(dir.mkdirs())
				System.out.println(dir + " created");
		}
	}
	
	public static void copyFile(MultipartFile file, String target) throws IOException {
		Files.copy(file.getInputStream(), Paths.get(target), StandardCopyOption.REPLACE_EXISTING);
	}

	public static void deleteImage(String image) {
		try {
			File file = new File(image);
			if(file.exists())
				file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
