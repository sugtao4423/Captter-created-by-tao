package sugtao4423.captter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class Config{

	private File propFile;
	private Properties p;

	private String at, ats, targetDir;
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
			System.exit(1);
		}
		at = p.getProperty("AT", null);
		ats = p.getProperty("ATS", null);
		targetDir = p.getProperty("targetDir", "");
		removeConverted = Boolean.valueOf(p.getProperty("removeConverted", "false"));
		removeCapture = Boolean.valueOf(p.getProperty("removeCapture", "false"));
	}

	public Twitter getTwitter(){
		if(at == null || ats == null){
			return null;
		}
		Configuration conf = new ConfigurationBuilder().setOAuthConsumerKey(Keys.ck).setOAuthConsumerSecret(Keys.cs).build();
		AccessToken accessToken = new AccessToken(at, ats);
		return new TwitterFactory(conf).getInstance(accessToken);
	}

	public String getTargetDir(){
		return targetDir;
	}

	public boolean removeConverted(){
		return removeConverted;
	}

	public boolean removeCapture(){
		return removeCapture;
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
