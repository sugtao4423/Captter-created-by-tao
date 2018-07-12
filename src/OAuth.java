import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JLabel;

public class OAuth extends JFrame implements ActionListener{

	private static final long serialVersionUID = -4736794014597982202L;

	private JPanel contentPane;
	private JTextField textField;
	private JButton start;
	private RequestToken req;
	private Twitter twitter;

	private Config config;

	/**
	 * Create the frame.
	 */
	public OAuth(){
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			SwingUtilities.updateComponentTreeUI(this);
		}catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1){
		}

		config = new Config();

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("OAuth認証");
		setBounds(100, 100, 163, 175);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		start = new JButton("認証");
		start.setBounds(6, 6, 140, 70);
		start.addActionListener(this);
		contentPane.add(start);

		textField = new JTextField();
		textField.setBounds(6, 105, 134, 28);
		textField.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e){
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					try{
						String pin = textField.getText();
						AccessToken token = twitter.getOAuthAccessToken(req, pin);
						config.addProperties("AT", token.getToken());
						config.addProperties("ATS", token.getTokenSecret());
						new Settings().setVisible(true);
						dispose();
					}catch(TwitterException e1){
					}
				}
			}
		});
		contentPane.add(textField);

		JLabel lblEnterGetPin = new JLabel("Enter get Pin Code");
		lblEnterGetPin.setBounds(6, 88, 134, 16);
		contentPane.add(lblEnterGetPin);
	}

	@Override
	public void actionPerformed(ActionEvent event){
		if(event.getSource() == start){
			try{
				Configuration jconf = new ConfigurationBuilder().setOAuthConsumerKey(Keys.ck).setOAuthConsumerSecret(Keys.cs).build();
				twitter = new TwitterFactory(jconf).getInstance();
				req = twitter.getOAuthRequestToken();
				Desktop.getDesktop().browse(new URI(req.getAuthorizationURL()));
			}catch(IOException | TwitterException | URISyntaxException e){
			}
		}
	}

}