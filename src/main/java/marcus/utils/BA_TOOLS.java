package marcus.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.upload.FormFile;


public class BA_TOOLS {

	static Logger loger = Logger.getLogger(BA_TOOLS.class.getName());

	static private BA_TOOLS ba_tools = new BA_TOOLS();
	
	static public BA_TOOLS getInstance() {
		return ba_tools;
	}

	public BA_TOOLS() {

	}
	
	/**
	 * 判斷是否是Null 或 空白
	 * @param str
	 * @return true or false
	 */
	public boolean isEmpty(String str){
		if(str==null)
			return true;
		if(str.equals(""))
			return true;
		return false;
	}
	
	/**
	 * 將resultSet資料同步到form 
	 * @param actionForm
	 * @param rs
	 */
	public void syncActionForm (Object actionForm, ResultSet rs) {

		try {
			Map<String,Method> methods = new HashMap<String,Method>();
			Class<?> pomClass = actionForm.getClass();
			
			Method[] ms = pomClass.getMethods();
			//過濾出所有setter
			for(Method m : ms) {
				if(m.getName().startsWith("set")) {
					methods.put(m.getName().substring(3), m);
				}
			}
			
			ResultSetMetaData metaData = rs.getMetaData();
			
			for(int i=1; i<=metaData.getColumnCount(); i++) {
				String columnName = metaData.getColumnName(i);
				Method method = methods.get(columnName);
				if(null != method) {
					Class<?> param = method.getParameterTypes()[0];
					if(param.equals(String.class)) {
						String typeName = metaData.getColumnTypeName(i);
						if(typeName.contains("CLOB")) {
							try {
								method.invoke(actionForm, rs.getClob(i));
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							method.invoke(actionForm, rs.getString(columnName));
						}
					} else if(param.equals(Integer.class) || param.equals(int.class)) {
						method.invoke(actionForm, rs.getInt(columnName));
					} else if(param.equals(Double.class) || param.equals(double.class)) {
						method.invoke(actionForm, rs.getDouble(columnName));
					} else if(param.equals(BigDecimal.class)) {
						method.invoke(actionForm, rs.getBigDecimal(columnName));
					}
				} else {
					loger.debug(actionForm.getClass().getName() + " column:" + columnName + " not found");
				}
			}
			
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
	}

	
	/**
	 * 下載壓縮檔
	 * @param response
	 * @param fileName 檔案名稱
	 * @param paths 預計打包成zip的檔案[]
	 * @throws FileNotFoundException
	 */
	public void writeZipFileToResponse(HttpServletResponse response, String fileName, String... paths) throws FileNotFoundException {
//		fileName = fileName.toLowerCase().endsWith(".zip") ? fileName : fileName + ".zip";	
		response.setCharacterEncoding("UTF-8");
	    response.setContentType("application/zip");
	    response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
	    GU_ZIP_FILE guZipFile = new GU_ZIP_FILE();
	    try {
			guZipFile.zipFile(response.getOutputStream(), paths);
		} catch (IOException e) {
			e.printStackTrace();
		}
//		response.flushBuffer();
	}
	
	/**
	 * 壓縮檔案至指定路徑
	 * @param zipFileName 路徑檔名
	 * @param paths 檔案路徑[]
	 * @throws FileNotFoundException
	 */
	public void writeZipFileToDirectory(String zipFileName, String... paths) throws FileNotFoundException {
//		zipFileName = zipFileName.toLowerCase().endsWith(".zip") ? zipFileName : zipFileName + ".zip";
		GU_ZIP_FILE guZipFile = new GU_ZIP_FILE();
		guZipFile.zipFile(zipFileName, paths);
	}
	
	/**
	 * 解壓縮已經存在系統上的壓縮檔
	 * @param destDirectory 目的地
	 * @param zipFilePath 壓縮檔路徑
	 * @throws FileNotFoundException
	 */
	public void writeUnzipFileToDirectory(String destDirectory, String zipFilePath) throws FileNotFoundException {
		GU_UNZIP gu_unzip = new GU_UNZIP();
		gu_unzip.unzip(destDirectory, zipFilePath);
	}
	
	/**
	 * 解壓縮user上傳上來的壓縮檔
	 * @param destDirectory 目的地
	 * @param file FormFile物件
	 * @throws IOException
	 */
	public void writeUnzipFileToDirectory(String destDirectory, FormFile file) throws IOException {
		GU_UNZIP gu_unzip = new GU_UNZIP();
		gu_unzip.unzip(destDirectory, file);
	}
}
