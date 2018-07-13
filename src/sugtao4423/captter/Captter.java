package sugtao4423.captter;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Timer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import sugtao4423.captter.AutoChecker.OnNewFileListener;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JSeparator;
import javax.swing.JCheckBox;

public class Captter extends JFrame implements ActionListener{

	private static final long serialVersionUID = 6920733301604906297L;

	private JPanel contentPane;
	private JButton tweet, settings;
	private JTextArea textArea;
	private HintTextField textField;
	private JLabel imageField;
	private JCheckBox convert;

	private File convertedDir;
	private Twitter twitter;
	private String backgroundImagePath, backgroundImageName;
	private boolean is16_9;
	private JCheckBox quick;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args){
		Config config = new Config();
		if(config.getTwitter() == null){
			new OAuth().setVisible(true);
			return;
		}
		if(config.getTargetDir().equals("")){
			new Settings().setVisible(true);
			return;
		}

		EventQueue.invokeLater(new Runnable(){
			public void run(){
				try{
					Captter frame = new Captter();
					frame.setVisible(true);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Captter(){
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			SwingUtilities.updateComponentTreeUI(this);
		}catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1){
		}

		setAlwaysOnTop(true);
		setTitle("Captter created by tao");

		Config config = new Config();
		twitter = config.getTwitter();
		convertedDir = new File(new File(System.getProperty("java.class.path")).getParent() + "/converted/");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(120, 120, 420, 240);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		tweet = new JButton("Tweet(Ctrl+Enter)");
		tweet.setBounds(260, 168, 140, 30);
		tweet.addActionListener(this);
		contentPane.add(tweet);

		textArea = new JTextArea();
		textArea.setBounds(0, 0, 400, 130);
		textArea.setFont(new Font("メイリオ", Font.BOLD, 20));
		textArea.setLineWrap(true);
		contentPane.add(textArea);

		textField = new HintTextField();
		textField.setBounds(0, 140, 400, 28);
		textField.setFont(new Font("メイリオ", Font.PLAIN, 12));
		textField.setHint("固定テキスト");
		contentPane.add(textField);

		JSeparator separator = new JSeparator();
		separator.setBounds(0, 130, 400, 10);
		contentPane.add(separator);

		imageField = new JLabel("");
		imageField.setBounds(0, 0, 400, 130);
		contentPane.add(imageField);

		settings = new JButton("設定");
		settings.setBounds(0, 169, 70, 30);
		settings.addActionListener(this);
		contentPane.add(settings);

		convert = new JCheckBox("convert 16:9");
		convert.setSelected(true);
		convert.setBounds(70, 170, 111, 23);
		contentPane.add(convert);

		quick = new JCheckBox("Quick");
		quick.setBounds(185, 170, 70, 23);
		contentPane.add(quick);

		textArea.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e){
				// VK_ENTER: EnterKey, CTRL_DOWN_MASK: CtrlKey, META_DOWN_MASK: OS X's CommandKey
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					if((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) > 0 || (e.getModifiersEx() & InputEvent.META_DOWN_MASK) > 0){
						tweet();
					}
				}
			}
		});

		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e){
				Config config = new Config();
				if(config.removeConverted()){
					for(String s : convertedDir.list()){
						if(s.endsWith(".png"))
							new File(convertedDir + "/" + s).delete();
					}
				}
				if(config.removeCapture()){
					File captureDir = new File(config.getTargetDir());
					String[] captureFiles = captureDir.list();
					for(String s : captureFiles){
						if(s.matches("^.*\\.(jpg|png|bmp)$"))
							new File(captureDir + "/" + s).delete();
					}
				}
			}
		});

		AutoChecker ac = new AutoChecker(config.getTargetDir());
		ac.setOnNewFileListener(new OnNewFileListener(){

			@Override
			public void onNewFile(String filename){
				backgroundImagePath = ac.getTargetDir() + "/" + filename;
				backgroundImageName = filename;
				setBackgroundImage();
			}
		});
		new Timer(false).schedule(ac, 0, 1000);
	}

	@Override
	public void actionPerformed(ActionEvent event){
		if(event.getSource() == tweet){
			tweet();
		}else if(event.getSource() == settings){
			new Settings().setVisible(true);
			dispose();
		}
	}

	public void tweet(){
		String textAreaText = textArea.getText();
		String textFieldText = textField.getText();
		new Thread(new Runnable(){

			@Override
			public void run(){
				StatusUpdate status = new StatusUpdate(textAreaText + " " + textFieldText);
				if(backgroundImagePath != null){
					String path;
					if(!is16_9 && convert.isSelected())
						path = getConverted16_9ImagePath();
					else
						path = backgroundImagePath;
					status.media(new File(path));
				}
				textArea.setText("");
				textArea.requestFocus();
				imageField.setText("");
				imageField.setIcon(null);
				backgroundImagePath = null;
				backgroundImageName = null;
				try{
					twitter.updateStatus(status);
				}catch(TwitterException e){
					JOptionPane.showMessageDialog(Captter.this, "ツイートできませんでした");
					return;
				}
			}
		}).start();
	}

	public void setBackgroundImage(){
		imageField.setIcon(new ImageIcon(getResizedBackgroundImage()));
		textArea.setOpaque(false);
		if(quick.isSelected())
			tweet();
	}

	public BufferedImage getResizedBackgroundImage(){
		try{
			int maxWidth = 400;
			int maxHeight = 130;

			BufferedImage sourceImage = ImageIO.read(new File(backgroundImagePath));

			int sourceWidht = sourceImage.getWidth();
			int sourceHeight = sourceImage.getHeight();

			BigDecimal bdW = new BigDecimal(maxWidth);
			bdW = bdW.divide(new BigDecimal(sourceWidht), 8, BigDecimal.ROUND_HALF_UP);
			BigDecimal bdH = new BigDecimal(maxHeight);
			bdH = bdH.divide(new BigDecimal(sourceHeight), 8, BigDecimal.ROUND_HALF_UP);

			if(bdH.compareTo(bdW) < 0)
				maxWidth = -1;
			else
				maxHeight = -1;

			Image targetImage = sourceImage.getScaledInstance(maxWidth, maxHeight, Image.SCALE_DEFAULT);

			BufferedImage targetBufferedImage =
					new BufferedImage(targetImage.getWidth(null), targetImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
			Graphics2D g = targetBufferedImage.createGraphics();
			g.drawImage(targetImage, 0, 0, null);

			g.setColor(new Color(255, 255, 255, 170));
			g.fill(new Rectangle2D.Double(0, 0, sourceWidht, sourceHeight));
			g.dispose();

			int gcd = getGCD(sourceWidht, sourceHeight);
			int w = sourceWidht / gcd;
			int h = sourceHeight / gcd;
			imageField.setText(w + ":" + h);
			if(w == 16 && h == 9)
				is16_9 = true;
			else
				is16_9 = false;

			return targetBufferedImage;
		}catch(IOException e){
			System.exit(1);
			return null;
		}
	}

	public int getGCD(int m, int n){
		return n == 0 ? m : getGCD(n, m % n);
	}

	public String getConverted16_9ImagePath(){
		try{
			int width = 1280;
			int height = 720;
			File source = new File(backgroundImagePath);
			String filename = source.getName().substring(0, backgroundImageName.lastIndexOf("."));
			BufferedImage src = ImageIO.read(source);
			Image img = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			BufferedImage b = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = b.createGraphics();
			g.drawImage(img, 0, 0, null);
			g.dispose();
			if(!convertedDir.exists())
				convertedDir.mkdirs();
			String outFilePath = convertedDir + "/" + filename + "_convert.png";
			ImageIO.write(b, "png", new File(outFilePath));
			return outFilePath;
		}catch(IOException e){
			return null;
		}
	}

}