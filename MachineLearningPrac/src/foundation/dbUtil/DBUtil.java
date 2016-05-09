package foundation.dbUtil;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import foundation.fileUtil.PropFileUtil;

public class DBUtil {
	private static Connection conn = null;
	private static DBUtil dbUtil = null;
	private String dbCfgFileName = "";

	private DBUtil(String dbCfgFileName) {
		this.dbCfgFileName = dbCfgFileName;

	}

	public static DBUtil getInstance(String dbCfgFileName) {
		if (dbUtil == null) {
			dbUtil = new DBUtil(dbCfgFileName);
		}
		return dbUtil;
	}

	/**
	 * 根据配置文件dbConf.properties指定的驱动程序名、数据库名、用户名和密码等数据 获取数据库连接
	 * 
	 * @return：数据库连接
	 * @throws DBException
	 */
	public Connection getConn() throws DBException {
		if (conn == null) {
			PropFileUtil dbcfgProp = PropFileUtil.getInstance(dbCfgFileName);
			try {
				Class.forName(dbcfgProp.getParameterValue("driverName")); // 加载mysq驱动"com.mysql.jdbc.Driver"
			} catch (ClassNotFoundException e) {
				System.out.println("驱动加载错误");
				e.printStackTrace();// 打印出错详细信息
			}
			try {
				String url = dbcfgProp.getParameterValue("url");
				// 连接数据库 数据库名称
				String user = dbcfgProp.getParameterValue("user");// 用户名
				String password = dbcfgProp.getParameterValue("password"); // 密码
				conn = (Connection) DriverManager.getConnection(url, user, password);
			} catch (SQLException e) {
				System.out.println("数据库链接错误");
				e.printStackTrace();
			}
		}
		return conn;
	}

	public PreparedStatement getPrepStmt(String sqlCmdTxt) throws DBException {
		PreparedStatement prepStmt = null;
		if (conn == null) {
			conn = getConn();
		}
		try {
			prepStmt = conn.prepareStatement(sqlCmdTxt);
		} catch (SQLException e) {
			throw new DBException("获取preparedStatement异常！" + e.getMessage());
		}
		return prepStmt;
	}

	public CallableStatement getcallableStmt(String sqlCmdTxt) throws DBException {
		CallableStatement callableStmt = null;
		if (conn == null) {
			conn = getConn();
		}
		try {
			callableStmt = conn.prepareCall(sqlCmdTxt);
		} catch (SQLException e) {
			throw new DBException("获取callableStatement对象异常！");
		}
		return callableStmt;
	}

	public void closeConn() throws DBException {
		try {
			if (conn != null && !conn.isClosed()) {
				conn.close();
				conn=null;
			}
		} catch (SQLException e) {
			throw new DBException("关闭连接对象异常！");
		}
	}

}
