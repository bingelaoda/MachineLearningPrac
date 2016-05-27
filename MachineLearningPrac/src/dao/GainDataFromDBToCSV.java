package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import foundation.dbUtil.DBException;
import foundation.dbUtil.DBUtil;
import foundation.fileUtil.FileNameUtil;
import foundation.fileUtil.RWCsvFile;
/**在demo环境下测试不同的机器学习方法对于不同案例产生的效果，构造训练以及
 * 测试数据，从已有的数据库中，到出成csv文件
 * @author wuxb
 *
 */
public class GainDataFromDBToCSV {
	private String dbCfgFileName = "";
	private DBUtil dbUtil=null;
	String prjPath = FileNameUtil.getPrjPath();
	
	public GainDataFromDBToCSV(){
		dbCfgFileName = "src/foundation/dbUtil/dbConf.properties";
		dbUtil = DBUtil.getInstance(dbCfgFileName);
	}
	
	public GainDataFromDBToCSV(String dbCfgFileName){
		this.dbCfgFileName = dbCfgFileName;
		dbUtil = DBUtil.getInstance(dbCfgFileName);
	}
	
	/**
	 * @param sqlTxt 从数据库中查询数据的语句
	 * @param fileds 需要的数据库的字段名
	 * @param filedsTypes 相应的字段类型
	 */
	public  void dbTocsv(String sqlTxt,List<String> fileds,String fileNM){
		try {
			PreparedStatement prst = dbUtil.getPrepStmt(sqlTxt);
			ResultSet rSet = prst.executeQuery();
			List<String> codes = new ArrayList<>();
			List<Float> restValues = new ArrayList<>();
			while(rSet.next()){
					String code = rSet.getString(fileds.get(0));
					codes.add(code);
					float restValue = rSet.getFloat(fileds.get(1));
					restValues.add(restValue);
			}
			List<List<StringBuffer>> convcode = new ArrayList<List<StringBuffer>>();
			List<Integer> startIndexs = new ArrayList<>();
			startIndexs.add(3);
			startIndexs.add(6);
			for(int i=0;i<codes.size();i++){
				List<StringBuffer> str = new ArrayList<>();
				List<Float> dataconversFloats = DataSetConver.dataSetConver(codes.get(i), startIndexs);
				for(int j=0;j<dataconversFloats.size();j++){
					StringBuffer sinStr = new StringBuffer();
					sinStr.append(dataconversFloats.get(j));
					str.add(sinStr);
				}
				StringBuffer strrest = new StringBuffer();
				strrest.append(restValues.get(i));
				str.add(strrest);
				convcode.add(str);
			}
			String file =prjPath+"dataSource\\"+fileNM; 
			RWCsvFile.writeCSV(convcode, file);
		} catch (DBException e) {
			e.printStackTrace();
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public  void dbTocsv1(String sqlTxt,List<String> fileds,String fileNM){
		try {
			PreparedStatement prst = dbUtil.getPrepStmt(sqlTxt);
			ResultSet rSet = prst.executeQuery();
			List<String> codes = new ArrayList<>();
			List<Integer> tsks = new ArrayList<>();
			while(rSet.next()){
					String code = rSet.getString(fileds.get(0));
					codes.add(code);
					int tsk = rSet.getInt(fileds.get(1));
					tsks.add(tsk);
			}
			List<List<StringBuffer>> convcode = new ArrayList<List<StringBuffer>>();
			for(int i=0;i<codes.size();i++){
				List<StringBuffer> str = new ArrayList<>();
				String temp = codes.get(i);
				String[] templi = temp.split(",");
				for(int j=0;j<templi.length;j++){
					StringBuffer sinStr = new StringBuffer();
					sinStr.append(templi[j]);
					str.add(sinStr);
				}
				StringBuffer strrest = new StringBuffer();
				strrest.append(tsks.get(i));
				str.add(strrest);
				convcode.add(str);
			}
			String file =prjPath+"dataSource\\"+fileNM; 
			RWCsvFile.writeCSV(convcode, file);
		} catch (DBException e) {
			e.printStackTrace();
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args){
		String sqlTxt = "select * from t_maxutiltsk";
		List<String> fileds = new ArrayList<>();
		fileds.add("dofCode");
		fileds.add("maxutiltsk");
		String fileNM = "tskDataSource.csv";
		new DBToCSV().dbTocsv1(sqlTxt, fileds,fileNM);
	}
	
	
}
