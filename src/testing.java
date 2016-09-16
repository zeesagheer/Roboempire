import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;

public class testing {
	static String dir = System.getProperty("user.home") + "/Desktop/EmpireLicenses";
	public static void main(String[] args) throws Exception{
		
		String user = "bhaai";
		String type = "admin";
		int lincenseNr = 78154;
		makeKey("PC",user,lincenseNr,false,type);
		makeKey("AND",user,lincenseNr,false,type);
	}
	static void makeKey(String platform,String licenseAppUserName,
			int licenseNr, boolean duration,String type)
	{
		String licenseEndDate = "99991231";
		if(duration)
			licenseEndDate = durationMonthDay(2,0);
		String licenseType = "COMMERIAL"; 
		if(type.equalsIgnoreCase("admin"))
			 licenseType = "ADMIN";
		
		List<Integer> list = new ArrayList<Integer>();
		
		SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[4];
        random.nextBytes(bytes);
		final int initSalt = ByteBuffer.wrap(bytes).getInt();
		
		list.add(initSalt);
		int keyVersion = 3;
		int destSalt = initSalt + 50735;
		if(platform.equalsIgnoreCase("AND"))
		{
			keyVersion = 4;
			destSalt = initSalt - 32434;
		}
		for (byte c : String.valueOf(initSalt).getBytes()) {
			keyVersion+=c;
		}
		list.add(keyVersion);
		list.add(licenseNr ^ destSalt);
		if(platform.equalsIgnoreCase("PC"))
		{
			encodeString(list,destSalt,licenseType);
			encodeString(list,destSalt,new SimpleDateFormat("yyyyMMdd").format(new Date()));
			encodeString(list,destSalt,new SimpleDateFormat("yyyyMMdd").format(new Date()));
			encodeString(list,destSalt,licenseEndDate);
			encodeStringUTF(list,destSalt,licenseAppUserName);
			encodeString(list,destSalt,"RoboEmpire");
			encodeString(list,destSalt,"6.19.5");
			encodeString(list,destSalt,"SRV_IN_1");
		}
		else if (platform.equalsIgnoreCase("AND"))
		{
			androidEncodeString(list,destSalt,licenseType);
			androidEncodeString(list,destSalt,new SimpleDateFormat("yyyyMMdd").format(new Date()));
			androidEncodeString(list,destSalt,new SimpleDateFormat("yyyyMMdd").format(new Date()));
			androidEncodeString(list,destSalt,licenseEndDate);
			androidEncodeString(list,destSalt,licenseAppUserName);
			androidEncodeString(list,destSalt,"RoboEmpireAndroid");
			androidEncodeString(list,destSalt,"2.18.5");
			androidEncodeString(list,destSalt,"SRV_IN_1");
		}
		
		byte[] result = new byte[list.size()*4];
		for(int i = 0; i < list.size(); i++) {
		    int yourInt =  list.get(i).intValue();
		    byte[] b = ByteBuffer.allocate(4).putInt(yourInt).array();
		    for (int j = 0; j < b.length; j++) {
				result[i*4+j]=b[j];
			}
		}
		String keyString = bytesToHexString(result);
		String hash = null;
		try {
			hash = encrptMD5(result);
		} catch (NoSuchAlgorithmException e1) {}
		
		String finalString = keyString + hash;
		
		try {
			
			new File(dir).mkdir();
			new File(dir+=("/"+licenseAppUserName)).mkdir();
			File file = new File(dir + "/" + licenseAppUserName + "_" + platform + ".lsk");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(finalString);
			bw.close();
			//System.out.println("Generated: "+ licenseAppUserName + "_" + platform + ".lsk");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	static void encodeBytes(final List<Integer> list, final int salt , byte[] b) {
		list.add(b.length ^ salt);
		SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[4];
		for(int i=0;i<b.length;i++) {
			random.nextBytes(bytes);
	        int charSalt = ByteBuffer.wrap(bytes).getInt();
			list.add(charSalt);
			list.add(b[i] ^ salt - charSalt);
		}
    }
	static void encodeString(final List<Integer> list, final int salt ,String str){
		encodeBytes(list, salt, str.getBytes());
    }
	static void encodeStringUTF(final List<Integer> list, final int salt ,String str){
		encodeBytes(list, salt, str.getBytes(Charset.forName("UTF-8")));
    }
	public static String bytesToHexString(final byte[] bytes) {
        final StringBuilder sb = new StringBuilder(bytes.length * 2);
        final Formatter formatter = new Formatter(sb);
        for (final byte b : bytes) {
            formatter.format("%02x", b);
        }
        formatter.close();
        return sb.toString();
    }
	
	public static String encrptMD5(byte[] data) throws NoSuchAlgorithmException{
		return bytesToHexString(MessageDigest.getInstance("MD5").digest(data));
	}
	
	static void androidEncodeString(final List<Integer> list, final int salt ,String str){
		androidEncodeBytes(list, salt, str.getBytes(Charset.forName("UTF-8")));
    }
	static void androidEncodeBytes(final List<Integer> list, final int salt , byte[] b) {
		list.add(b.length ^ salt);
		//System.out.println(b.length);
		SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[4];
		for(int i=0;i<b.length;i++) {
			random.nextBytes(bytes);
	        int charSalt = ByteBuffer.wrap(bytes).getInt();
			random.nextBytes(bytes);
	        int charSalt2 = ByteBuffer.wrap(bytes).getInt();
			list.add(charSalt);
			list.add(charSalt2);
			list.add(b[i] ^ salt + charSalt ^ charSalt2);
		}
    }
	public static String durationMonthDay(int months, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, months);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
    }

}
