package sugtao4423.captter;

import java.io.File;
import java.util.ArrayList;
import java.util.TimerTask;

public class AutoChecker extends TimerTask{

	interface OnNewFileListener{
		void onNewFile(String filename);
	}

	private File targetDir;
	private ArrayList<String> cache;
	private OnNewFileListener onNewFileListener;

	public AutoChecker(String targetDir){
		this.targetDir = new File(targetDir);
		cache = new ArrayList<String>();
	}

	public void setOnNewFileListener(OnNewFileListener onNewFileListener){
		this.onNewFileListener = onNewFileListener;
	}

	public File getTargetDir(){
		return targetDir;
	}

	@Override
	public void run(){
		String[] files = targetDir.list();
		for(String file : files){
			if(!cache.contains(file)){
				cache.add(file);
				if(file.matches("^.*\\.(jpg|png|bmp)$") && onNewFileListener != null){
					onNewFileListener.onNewFile(file);
				}
			}
		}
	}

}
