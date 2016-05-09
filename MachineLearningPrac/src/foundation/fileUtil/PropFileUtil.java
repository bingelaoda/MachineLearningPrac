package foundation.fileUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

public class PropFileUtil {

	// 属性集对象哈希表
	private static Hashtable<String, Properties> propHashtable = new Hashtable<String, Properties>();

	// 属性文件名
	private String curProFileName = "";
	// 属性文件的内存映象
	private Properties curProp = null;

	/**
	 * 私有构造方法
	 * 
	 * @param PROFILENAME
	 *            ：属性文件名
	 */
	private PropFileUtil(String PROFILENAME) {
		// 属性集合对象
		Properties prop = propHashtable.get(PROFILENAME);
		if (prop == null) {
			// 属性文件输入流
			FileInputStream fis = null;
			try {
				prop = new Properties();
				fis = new FileInputStream(PROFILENAME);
				// 将属性文件流装载到Properties对象中
				prop.load(fis);
				fis.close();
				propHashtable.put(PROFILENAME, prop);
				curProp = prop;
				curProFileName = PROFILENAME;
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else {
			curProp = prop;
			curProFileName = PROFILENAME;
		}

	}

	/**
	 * 单例模式
	 * 
	 * @param PROFILENAME
	 *            ：属性文件名
	 * @return：属性工具类对象
	 */
	public static PropFileUtil getInstance(String PROFILENAME) {
		PropFileUtil propFileUtil = new PropFileUtil(PROFILENAME);
		return propFileUtil;
	}

	/**
	 * 增加一个参数
	 * 
	 * @param paraName
	 *            :参数名
	 * @param value
	 *            ：参数值
	 */
	public void saveParameter(String paraName, String value) {
		curProp.setProperty(paraName, value);
		refresh(curProFileName);
	}

	/**
	 * 根据参数名，获取值
	 * 
	 * @param paraName
	 *            ：参数名
	 * @return：指定参数的值
	 */
	public String getParameterValue(String paraName) {
		return curProp.getProperty(paraName);
	}

	/**
	 * 根据参数名，从属性文件中删除该参数
	 * @param paraName：参数名
	 */
	public void removeParameter(String paraName) {
		curProp.remove(paraName);
		refresh(curProFileName);
	}

	/**
	 * 将内存中的属性数据同步到属性文件中
	 * @param proFileName：属性文件名
	 */
	private void refresh(String proFileName) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(proFileName);
			// 将Properties集合保存到流中
			curProp.store(fos, "");
			fos.flush();// 清空缓存，写入磁盘
			fos.close();// 关闭输出流
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
