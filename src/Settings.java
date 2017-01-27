import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JCheckBox;

public class Settings extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JTextField textField;
	private JButton find;
	private JLabel lb;
	private JButton save;

	private JCheckBox exit_RemoveConverted, exit_RemoveCapture;
	private Config config;

	/**
	 * Create the frame.
	 */
	public Settings(){
		config = new Config();
		config.loadConfig();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("設定");
		setBounds(120, 120, 420, 240);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		lb = new JLabel("監視するフォルダー");
		lb.setBounds(6, 25, 130, 16);
		contentPane.add(lb);

		textField = new JTextField();
		textField.setBounds(6, 45, 250, 28);
		textField.setText(config.getTargetDir());
		contentPane.add(textField);

		find = new JButton("参照");
		find.setBounds(260, 46, 70, 29);
		find.addActionListener(this);
		contentPane.add(find);

		save = new JButton("保存");
		save.setBounds(277, 163, 117, 29);
		save.addActionListener(this);
		contentPane.add(save);

		boolean[] removes = config.getRemoves();

		exit_RemoveConverted = new JCheckBox("プログラム終了時にconvertedフォルダの中身を削除");
		exit_RemoveConverted.setBounds(6, 85, 414, 23);
		exit_RemoveConverted.setSelected(removes[0]);
		contentPane.add(exit_RemoveConverted);

		exit_RemoveCapture = new JCheckBox("プログラム終了時にキャプチャフォルダの中身を削除");
		exit_RemoveCapture.setBounds(6, 110, 414, 23);
		exit_RemoveCapture.setSelected(removes[1]);
		contentPane.add(exit_RemoveCapture);
	}

	public void actionPerformed(ActionEvent event){
		if(event.getSource() == find)
			selectDirectory();
		else if(event.getSource() == save)
			save();
	}

	public void selectDirectory(){
		JFileChooser dirChoose = new JFileChooser();
		dirChoose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if(dirChoose.showOpenDialog(contentPane) == JFileChooser.APPROVE_OPTION){
			File fl = dirChoose.getSelectedFile();
			textField.setText(fl.getAbsolutePath());
		}
	}

	public void save(){
		config.addProperties("targetDir", textField.getText());
		config.addProperties("removeConverted", String.valueOf(exit_RemoveConverted.isSelected()));
		config.addProperties("removeCapture", String.valueOf(exit_RemoveCapture.isSelected()));

		try{
			Captter.main(new String[]{});
		}catch(IOException e){
		}
		dispose();
	}
}
