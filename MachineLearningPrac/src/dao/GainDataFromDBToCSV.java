package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import foundation.dbUtil.DBException;
import foundation.dbUtil.DBUtil;
import foundation.fileUtil.FileNameUtil;
import foundation.fileUtil.RWCsvFileUtil;
/**在demo环境下测试不同的机器学习方法对于不同案例产生的效果，构造训练以及
 * 测试数据，从已有的数据库中，到出成csv文件
 * @author wuxb
 *
 */
public class GainDataFromDBToCSV {
	private int dimension;
	public int getDimension() {
		return dimension;
	}

	public void setDimension(int dimension) {
		this.dimension = dimension;
	}
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
	public  boolean dbTocsv(String sqlTxt,List<String> fileds,String desCSVFilePath){
		try {
			/*
			 * 从数据库中读取 code [19.618303, 7.0241666, 15.844223, 6.1579275, server1, server2, server3, 
			 * server2, server3, server1, server3, server1, server2, WebServer2]
			 * 和响应时间的值0.423892
			 * 
			 * 不同的案例在code字段上维数有所不同
			 */
			PreparedStatement prst = dbUtil.getPrepStmt(sqlTxt);
			ResultSet rSet = prst.executeQuery();
			List<String> codes = new ArrayList<>();
			List<Float> restValues = new ArrayList<>();
			int z=0;
			while(rSet.next()){
					String code = rSet.getString(fileds.get(0));
					int length = code.trim().substring(1, code.length()-3).split(",").length;
					setDimension(length+1);
					codes.add(code);
					float restValue = rSet.getFloat(fileds.get(1));
					restValues.add(restValue);
					
					if(z>30001){
						break;
					}
					z++;
			}
			
			/*
			 * 将得到的数据按照机器学习想要的CSV格式生成List<List<StringBuffer>>形式
			 */
			List<List<StringBuffer>> rlts = new ArrayList<List<StringBuffer>>();
			
			for(int i=0;i<codes.size();i++){
				List<StringBuffer> rlt =DataSetConver.dataSetConver(codes.get(i), restValues.get(i));
				rlts.add(rlt);
			}
			String[] attrs = getAttrs(dimension);
			/*
			 *将数据写到制定的csv文件
			 */
			RWCsvFileUtil.writeCSV(rlts, attrs,desCSVFilePath);
		} catch (DBException e) {
			e.printStackTrace();
		}catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				dbUtil.closeConn();
			} catch (DBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return true;
	}
	
	private String[] getAttrs(int dimension) {
		String[] attrs = new String[dimension];
		for(int i=0;i<dimension;i++){
			attrs[i] = "Column"+(i+1);
		}
		return attrs;
	}

	public static void main(String[] args){
		String sqlTxt = "select * from t_temp_run";
		List<String> fileds = new ArrayList<>();
		fileds.add("Code");
		fileds.add("RestValue");
		String desCSVFilePath = FileNameUtil.getPrjPath()+"dataSource/brsLatin.csv";
		
		GainDataFromDBToCSV gainDataFromDBToCSV = new GainDataFromDBToCSV();
		gainDataFromDBToCSV.dbTocsv(sqlTxt, fileds, desCSVFilePath);
	}
	
	
}
