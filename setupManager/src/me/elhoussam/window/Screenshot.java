package me.elhoussam.window;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import me.elhoussam.util.log.Tracking;

public class Screenshot extends JFrame {

  private JPanel contentPane;

  /**
   * Launch the application.
   */
  public int showOpenDialog()  {
    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    this.setAlwaysOnTop(true);
    this.setVisible(true);
    return 1;
  }

  /**
   * Create the frame.
   */
  public Screenshot(String args) {
    setType(Type.POPUP);
    setTitle("Screenshot");
    //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 832, 484);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    contentPane.setLayout(new BorderLayout(0, 0));

    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

    JSlider slider = new JSlider(0, 99, 50);
    slider.setBorder(new TitledBorder(null, "Zoom", TitledBorder.LEFT, TitledBorder.TOP, null, null));
    slider.setPaintTrack(true);
    slider.setPaintTicks(true);
    slider.setPaintLabels(true);
    panel.add(slider);

    slider.setMajorTickSpacing(50);
    slider.setMinorTickSpacing(5);

    JScrollPane scrollPane = new JScrollPane();

    ImageIcon ii = new ImageIcon(args);
    // try to resize image
    Image image = ii.getImage(); // transform it
    int width = ii.getIconWidth(), height = ii.getIconHeight();
    Tracking.echo("W="+width+" H="+height);

    JLabel lbl1 = new JLabel( new ImageIcon(
        image.getScaledInstance(
            (int)(ii.getIconWidth()*0.5),
            (int)(ii.getIconHeight()*0.5),  java.awt.Image.SCALE_SMOOTH)
        )  );

    slider.addChangeListener(event -> {
      int percentage = slider.getValue() ;
      Image newimgg = ii.getImage();
      newimgg = image.getScaledInstance(
          (ii.getIconWidth()*percentage/100),
          (ii.getIconHeight()*percentage/100),  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
      ImageIcon iii = new ImageIcon(newimgg);
      lbl1.setIcon(iii);
      Tracking.echo("Update  W="+iii.getIconWidth()+" H="+iii.getIconHeight());
    });

    scrollPane.setViewportView(lbl1);

    setContentPane(contentPane);
    contentPane.add(panel, BorderLayout.SOUTH);
    contentPane.add(scrollPane, BorderLayout.CENTER);
  }

}
