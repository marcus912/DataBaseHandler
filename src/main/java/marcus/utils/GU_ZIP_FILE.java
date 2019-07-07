package marcus.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class GU_ZIP_FILE {

	public static void main(String[] args) {
		try {
			GU_ZIP_FILE gzf = new GU_ZIP_FILE();
			gzf.zipFile("D:\\Work\\zipFileName.zip", "D:\\Work\\poi升級.xlsx", "D:\\Work\\poi升級-程式清單.xlsx", "D:\\Work\\資訊與流程優化處(公告)).pdf");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public GU_ZIP_FILE() {
		
	}
	
	/**
	 * 寫出zip檔到指定路徑
	 * @param zipFileName 路徑檔名
	 * @param filePaths 檔案路徑[]
	 * @throws FileNotFoundException
	 */
	public void zipFile(String zipFileName, String... filePaths) throws FileNotFoundException {
		List<String> srcFiles = Arrays.asList(filePaths);
		FileOutputStream fos = null;
		ZipOutputStream zipOut = null;
		try {
			fos = new FileOutputStream(zipFileName);
			zipOut = new ZipOutputStream(fos);
			for (String srcFile : srcFiles) {
				try {
					File fileToZip = new File(srcFile);
					FileInputStream fis = new FileInputStream(fileToZip);
					ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
					zipOut.putNextEntry(zipEntry);

					byte[] bytes = new byte[1024];
					int length;
					while ((length = fis.read(bytes)) >= 0) {
						zipOut.write(bytes, 0, length);
					}
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} finally {
			if(zipOut != null) {
				try {
					zipOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 可用此API透過response zip檔
	 * @param OutputStream
	 * @param paths 檔案路徑[]
	 * @throws FileNotFoundException
	 */
	public void zipFile(OutputStream os, String... paths) throws FileNotFoundException {
	    ZipOutputStream zipOut = null;
	    byte[] buffer = new byte[1024];
	    try {
	    	zipOut = new ZipOutputStream(new BufferedOutputStream(os));
	        for (String srcFile : paths) {
	        	File fileToZip = new File(srcFile);
				InputStream fis = new FileInputStream(fileToZip);
	            InputStream input = null;
	            try {
	                input = new BufferedInputStream(fis, 1024);
	                zipOut.putNextEntry(new ZipEntry(fileToZip.getName()));
	                int length;
	                while ((length = input.read(buffer)) > 0) {
	                	zipOut.write(buffer, 0, length);
	                }
	                zipOut.closeEntry();
	            } catch(IOException e) {
	            	e.printStackTrace();
	            } finally {
					if (input != null)
						try {
							input.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
	            }
	        }
	    } finally {
			if (zipOut != null)
				try {
					zipOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	    }
	}
}
