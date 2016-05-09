package foundation.dbUtil;

public class DBException extends Exception {

	private static final long serialVersionUID = -4677738075353379508L;
	private String errorMsg="";
	public DBException(String errMsg){
		errorMsg=errMsg;
	}
	@Override
	public String getMessage() {
		String msg="database access error!\n";
		if(errorMsg !=null){
			msg=errorMsg+"\n";
		}
		return ""+msg;
	}
}
