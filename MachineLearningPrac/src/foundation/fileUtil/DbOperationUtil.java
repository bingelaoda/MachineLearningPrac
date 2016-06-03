package foundation.fileUtil;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.omg.CORBA.PUBLIC_MEMBER;

import foundation.dbUtil.DBException;
import foundation.dbUtil.DBUtil;
import foundation.fileUtil.TxtFileUtil;

public class DbOperationUtil {
//	static String path = "F:/PCM/！！！实验数据/SM/PCS/30次/";
//	static String fileName = path+"Cloud11_ms_NSRulSeqGA_SM_36000"+".sql";
	static File newFile = new File("d:/2.sql");
	
	public static List<String> writeToDat(String path) {
		File file = new File(path);
		List<String> list = new ArrayList<String>();
		List<String> nums = new ArrayList<String>();
		try {
			BufferedReader bw = new BufferedReader(new FileReader(file));
			String line = null;
			// 因为不知道有几行数据，所以先存入list集合中
			while ((line = bw.readLine()) != null) {
				list.add(line);
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 确定数组长度
		for (int i = 0; i < list.size(); i++) {
			String s = (String) list.get(i);
			nums.add(s);
		}
		return nums;
	}
	
	public static void writeFile(String str, String savePath) throws Exception {
		BufferedWriter bw = new BufferedWriter(new FileWriter(savePath));
		bw.write(str);
		bw.close();
	}
	
	public static List<Integer> getLineNum(String fileName) throws Exception {
		List<Integer> lstIndex = new ArrayList<Integer>();
		String atoi = "/*Data for the table `t_change` */";
		String atoi2 = "/*Table structure for table `t_dof` */";
		String atoi3 = "/*Data for the table `t_run` */";
		String atoi4 = "/*Table structure for table `t_temp_run` */";
		BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
		String s = "";
		int index = 0;
		while ((s = br.readLine()) != null) {
			index++;
			if(s.contains(atoi3)){
				System.out.println(index);
				lstIndex.add(index);
			}
			if(s.contains(atoi4)){
				System.out.println(index);
				lstIndex.add(index);
			}
//			if (s.contains(atoi)) {
//				System.out.println(index);
//				lstIndex.add(index);
//			}
//			if(s.contains(atoi2)){
//				System.out.println(index);
//				lstIndex.add(index);
//			}
		}

		br.close();
		return lstIndex;
	}
	
	/**
	 * 将字符内容追加到目标文件的最后一行的下一行
	 * 
	 * @param content
	 *            ：字符串内容
	 * @param destfile
	 *            ：目标文件 @return：如果追回成功返回真，目标文件内容发生变化；否则返回假。
	 */
	public static boolean appendlastToFile(String content, File destfile) {
		boolean result = false;
		try {
			String lastStr = "\n" + content;
			FileWriter writer = null;
			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			writer = new FileWriter(destfile, true);
			writer.write(lastStr);
			result = true;
			writer.close();

		} catch (Exception e) {

			e.printStackTrace();
		}
		return result;

	}
	
	public static void out(String fileName) throws Exception{
		List<String> nums = writeToDat(fileName);
		List<Integer> lstIndex = getLineNum(fileName);
		appendlastToFile("USE `saopt`;",newFile);
		for (int i = lstIndex.get(0); i < lstIndex.get(1); i++) {
//			System.out.println(nums.get(i));		
			appendlastToFile(nums.get(i),newFile);
		}
		
	}

	public static void outSingleFieldFromDB(String sqlTxt){
		String dbCfgFileName = FileNameUtil.getPrjPath()+"src\\foundation\\dBUtil\\dbConf.properties";
		DBUtil dbUtil = DBUtil.getInstance(dbCfgFileName);
		PreparedStatement prst=null;
		try {
			prst = dbUtil.getPrepStmt(sqlTxt);
		} catch (DBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			ResultSet rSet = prst.executeQuery();
			while(rSet.next()){
				String runID = rSet.getString("runID");
				System.out.println(runID);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	}
	
	public static void iteratorDefineValue(String sqlTxt){
		String dbCfgFileName = FileNameUtil.getPrjPath()+"src\\foundation\\dBUtil\\dbConf.properties";
		DBUtil dbUtil = DBUtil.getInstance(dbCfgFileName);
		PreparedStatement prst=null;
		try {
			prst = dbUtil.getPrepStmt(sqlTxt);
			
		} catch (DBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ResultSet rSet = null;
		try {
			rSet = prst.executeQuery();
			float tempRestValue=1000;
			String tempRunID="";
			String runID="";
			while(rSet.next()){
				 runID = rSet.getString("runID");
				if(runID.equals(tempRunID)){
					float restValue = rSet.getFloat("RestValue");
					if(restValue<tempRestValue){
						tempRestValue = restValue;
					}
				}else{
					System.out.println(tempRestValue);
					tempRestValue=0;
					float restValue = rSet.getFloat("RestValue");
					if(restValue>tempRestValue){
						tempRestValue = restValue;
					}
					tempRunID=rSet.getString("runID");
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	public static void main(String[] args) throws Exception {
		String path = "M:\\论文数据\\DFGA\\jssbrs\\sql\\";
		ArrayList<String> fileNms = TxtFileUtil.findFileNms(path, ".sql");
		for(int i=0;i<fileNms.size();i++){
//			for(int i=0;i<1;i++){
			out(path+fileNms.get(i));
			System.out.println("finish");
		}
//		String sqlTxt = "SELECT * FROM t_run";
//		iteratorDefineValue(sqlTxt);
		
	}
}