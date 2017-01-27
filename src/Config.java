import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Config{

	private File propFile;
	private Properties p;

	private String ck, cs, at, ats, targetDir;
	private boolean removeConverted, removeCapture;

	public Config(){
		String dirPath = new File(System.getProperty("java.class.path")).getParent();
		propFile = new File(dirPath + "/Captter.conf");
		try{
			if(!propFile.exists())
				propFile.createNewFile();
			p = new Properties();
			FileInputStream fis = new FileInputStream(propFile);
			p.load(fis);
			fis.close();
		}catch(IOException e){
		}
	}

	public void loadTwitter(){
		ck = Keys.ck;
		cs = Keys.cs;
		at = p.getProperty("AT", "");
		ats = p.getProperty("ATS", "");
	}

	public void loadConfig(){
		targetDir = p.getProperty("targetDir", "");
		removeConverted = Boolean.valueOf(p.getProperty("removeConverted", "false"));
		removeCapture = Boolean.valueOf(p.getProperty("removeCapture", "false"));
	}

	public String[] getTwitter(){
		return new String[]{ck, cs, at, ats};
	}

	public String getTargetDir(){
		return targetDir;
	}

	public boolean[] getRemoves(){
		return new boolean[]{removeConverted, removeCapture};
	}

	public void addProperties(String key, String value){
		String at = p.getProperty("AT", "");
		String ats = p.getProperty("ATS", "");
		String targetDir = p.getProperty("targetDir", "");
		String removeConverted = p.getProperty("removeConverted", "false");
		String removeCapture = p.getProperty("removeCapture", "false");

		switch(key){
		case "AT":
			at = value;
			break;
		case "ATS":
			ats = value;
			break;
		case "targetDir":
			targetDir = value;
			break;
		case "removeConverted":
			removeConverted = value;
			break;
		case "removeCapture":
			removeCapture = value;
			break;
		}

		p = new Properties();
		p.setProperty("AT", at);
		p.setProperty("ATS", ats);
		p.setProperty("targetDir", targetDir);
		p.setProperty("removeConverted", removeConverted);
		p.setProperty("removeCapture", removeCapture);

		this.at = at;
		this.ats = ats;
		this.targetDir = targetDir;
		this.removeConverted = Boolean.valueOf(removeConverted);
		this.removeCapture = Boolean.valueOf(removeCapture);

		try{
			FileOutputStream fos = new FileOutputStream(propFile);
			p.store(fos, null);
			fos.close();
		}catch(IOException e){
		}
	}
}
