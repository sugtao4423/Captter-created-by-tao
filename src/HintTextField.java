import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

public class HintTextField extends JTextField{
	private static final long serialVersionUID = 1L;
	private String hint;

	public void setHint(String hint){
		this.hint = hint;
	}

	public String getHint(){
		if(hint == null)
			hint = "";
		return hint;
	}

	public void setText(String arg0){
		super.setText(arg0);
		repaint();
	}

	public HintTextField(){
		super();

		addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent arg0){
				repaint();
			}

			public void focusLost(FocusEvent arg0){
				repaint();
			}
		});
	}

	protected void paintComponent(Graphics g){
		super.paintComponent(g);

		if(hasFocus())
			return;
		if(getText().length() > 0)
			return;
		if(getHint().length() < 1)
			return;

		Font oldFont = g.getFont();
		Color oldColor = g.getColor();
		{
			g.setFont(getFont());
			g.setColor(Color.LIGHT_GRAY);

			Insets insets = getBorder().getBorderInsets(this);
			int h = g.getFontMetrics().getAscent();
			g.drawString(getHint(), insets.left, insets.top + h);
		}
		g.setFont(oldFont);
		g.setColor(oldColor);
	}
}