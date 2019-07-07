package marcus.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.struts.upload.FormFile;

public class GU_UNZIP {

	public static void main(String[] args) {
		try {
			GU_UNZIP gu = new GU_UNZIP();
			gu.unzip("D:\\Work\\zipFileName\\", "D:\\Work\\zipFileName.zip");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public GU_UNZIP () {
		
	}
	

	
	/**
	 * 
	 * @param destDirectory 解壓縮目的路徑
	 * @param zipFilePath zip file 檔案路徑
	 * @throws FileNotFoundException
	 */
	public void unzip(String destDirectory, String zipFilePath) throws FileNotFoundException {
		FileInputStream fis = new FileInputStream(zipFilePath);
	    File destDir = new File(destDirectory);
	    if (!destDir.exists()) {
	        destDir.mkdir();
	    }
	    ZipInputStream zis = new ZipInputStream(fis);
	    ZipEntry entry;
		try {
			entry = zis.getNextEntry();
		    while (entry != null) {
		        String filePath = destDirectory + File.separator + entry.getName();
		        if (!entry.isDirectory()) {
		            // unzip file
		        	FileOutputStream fos = new FileOutputStream(filePath);
		            byte[] bytes = new byte[1024];
		            int length;
		            while ((length = zis.read(bytes)) >= 0) {
		            	fos.write(bytes, 0, length);
					}
		            fos.close();
		        } else {
		            // make the directory
		            File dir = new File(filePath);
		            dir.mkdir();
		        }
		        zis.closeEntry();
		        entry = zis.getNextEntry();
		    }
		    zis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 解壓縮上傳上來的zip檔
	 * @param destDirectory 解壓縮目的路徑
	 * @param file struts1 FormFile
	 * @throws IOException
	 */
	public void unzip(String destDirectory, FormFile file) throws IOException {
	    File destDir = new File(destDirectory);
	    if (!destDir.exists()) {
	        destDir.mkdir();
	    }
	    ZipInputStream zis = new ZipInputStream(file.getInputStream());
	    ZipEntry entry = zis.getNextEntry();
	    try {
		    while (entry != null) {
		        String filePath = destDirectory + File.separator + entry.getName();
		        if (!entry.isDirectory()) {
		            // unzip file
		        	FileOutputStream fos = new FileOutputStream(filePath);
		            byte[] bytes = new byte[1024];
		            int length;
		            while ((length = zis.read(bytes)) >= 0) {
		            	fos.write(bytes, 0, length);
					}
		            fos.close();
		        } else {
		            // make the directory
		            File dir = new File(filePath);
		            dir.mkdir();
		        }
		        zis.closeEntry();
		        entry = zis.getNextEntry();
		    }
		    zis.close();
	    } catch(IOException e) {
	    	e.printStackTrace();
	    }
	}
	
}
