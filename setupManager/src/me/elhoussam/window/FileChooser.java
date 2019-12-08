package me.elhoussam.window;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import me.elhoussam.interfaces.infoInterface;
import me.elhoussam.util.log.Tracking;
import me.elhoussam.util.sys.StringHandler;
class FileChooser {

  private infoInterface currentRemoteObj = null;

  private JDialog dialog = new JDialog();
  JLabel selectedPathLabel = new JLabel("Path:");

  private final JPanel contentPanel = new JPanel();
  //static ArrayList<String> filePathsBase = new ArrayList<String>();
  static ArrayList<String> fileNamesBase = new ArrayList<String>();
  static String currentPath = "";
  static Boolean lock = true ;

  private String   finalPaths = ""  ;
  /**
   * Launch the application.
   */

  public infoInterface getCurrentRef() {
    return currentRemoteObj;
  }
  public int showOpenDialog() {
    dialog.setVisible(true);
    while( lock ) {
      try {
        Thread.sleep(2*1000);
      } catch (InterruptedException e) {
        Tracking.error(true,"showOpenDialog "+e.getLocalizedMessage());
        e.printStackTrace();
        return -1;
      }
    }
    return 0;
  }
  /**
   * Create the dialog.
   */
  public FileChooser(infoInterface selectedObj ) throws RemoteException{
    dialog.setBounds(100, 100, 243, 319);

    currentRemoteObj = selectedObj ;
    dialog.setTitle("Navigate: "+currentRemoteObj.get("os.name")+" ip "+currentRemoteObj.getIpAddress());

    ArrayList<String> rootNames =  currentRemoteObj.getRootDir(false);
    ArrayList<String> rootPaths =  currentRemoteObj.getRootDir(true);
    //filePathsBase = (ArrayList<String>) rootPaths.clone();

    dialog.getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    dialog.getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(new BorderLayout(0, 0));


    JList<String> lsListOfCurrentDirAndfile =  new JList<String>( new DefaultListModel<String>() ){
      // This method is called as the cursor moves within the list.
      @Override
      public String getToolTipText(MouseEvent evt) {
        // Get item index
        int index = locationToIndex(evt.getPoint());
        if( index >= 0) {
          // Get item
          Object item = getModel().getElementAt(index);

          // Return the tool tip text

          return "Path : " +StringHandler.separatorsToSystem( currentPath  + item);
        }
        return "";
      }
    };
    lsListOfCurrentDirAndfile.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    lsListOfCurrentDirAndfile.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent mouseEvent) {
            JList<String> theList = (JList<String>) mouseEvent.getSource();
            if (mouseEvent.getClickCount() == 2) {
              int index = theList.locationToIndex(mouseEvent.getPoint());
              if (index >= 0) {

                String selectedElement = theList.getModel().getElementAt(index);
                selectedElement = StringHandler.separatorsToSystem( selectedElement );

                if ( selectedElement.endsWith(File.separator) ) {
                  // change path
                  currentPath = StringHandler.fixEndingOf(currentPath) + selectedElement;
                  selectedPathLabel.setText("Path:"+currentPath);
                  //change JList Element (model)
                  try {
                    Tracking.echo("list#Current Path `"+currentPath+"`" );
                    String [] newItems =  currentRemoteObj.changeDirAndListContent(currentPath);

                    DefaultListModel<String> model = getNewListModel( newItems ) ;
                    //filePathsBase = new ArrayList<String>( Arrays.asList( newItems) );
                    lsListOfCurrentDirAndfile.setModel(  model  );

                  } catch (RemoteException e1) {
                    Tracking.error(true,"can't reach this directory"+e1.getLocalizedMessage());
                    e1.printStackTrace();
                  }
                }else {
                  // change path

                  Tracking.echo("selected file " +   StringHandler.fixEndingOf(currentPath) + selectedElement   );
                }

                //String a [] = selectedElement.split("*")[1];
                //mainList = false ;
                //Tracking.echo("try to reach " +   currentPath   );
                //theList.setModel ( getNewListModel (changeDirAndListContent( filePathsBase.get(index) )) );
              }
            }
          }


        }

        );
    JScrollPane scrollPane = new JScrollPane(lsListOfCurrentDirAndfile);

    contentPanel.add(  scrollPane, BorderLayout.CENTER);

    JComboBox<String> cmbListOfMainDir  = new JComboBox<String>(  );
    for (String a : rootNames) {
      cmbListOfMainDir.addItem(a);
    }
    cmbListOfMainDir.addActionListener( e -> {
      JComboBox<String> cb = (JComboBox<String>)e.getSource();
      int selectedIndex = cb.getSelectedIndex();
      cb.getSelectedItem();
      String fullPath = StringHandler.fixEndingOf( StringHandler.separatorsToSystem(rootPaths.get(selectedIndex)) ) ;
      try {
        currentPath = fullPath ;
        selectedPathLabel.setText("Path:"+currentPath);

        Tracking.echo("Current Path `"+currentPath+"`" );
        String [] newItems =  currentRemoteObj.changeDirAndListContent(fullPath);

        DefaultListModel<String> model = getNewListModel( newItems ) ;
        if( newItems != null ) {
          //filePathsBase = new ArrayList<String>( Arrays.asList( newItems) );
        }
        lsListOfCurrentDirAndfile.setModel(  model  );
      } catch (RemoteException e1) {
        e1.printStackTrace();
      }
    } );
    JPanel panel = new JPanel();
    panel.setLayout( new BoxLayout(panel, BoxLayout.Y_AXIS) );

    panel.add(cmbListOfMainDir);

    JPanel a = new JPanel( new FlowLayout(FlowLayout.LEFT) ) ;
    a.add(selectedPathLabel);

    panel.add(a  );

    contentPanel.add(panel, BorderLayout.NORTH);

    //contentPanel.add(cmbListOfMainDir, BorderLayout.NORTH);



    lsListOfCurrentDirAndfile.setFont(new Font("Tahoma", Font.BOLD, 15));

    {
      JPanel buttonPane = new JPanel();
      JButton btUpBtn = new JButton("UP");
      btUpBtn.addActionListener(e-> {
        Tracking.echo("UP\t"+currentPath);
        //
        if( !currentPath.isEmpty() ) {
          if(  !StringHandler.checkIfExist(currentPath, rootPaths) ) {

            String currentPathParts [] = currentPath.split( Pattern.quote(File.separator) );
            String upDir =( currentPath.startsWith(File.separator) )?File.separator:"";
            for( String el : currentPathParts ) {
              if( !currentPathParts[ currentPathParts.length-1 ].trim().equals(el) ) {
                upDir += el+((el.trim().isEmpty())?"":File.separator)   ;
              }
            }
            currentPath = upDir ;

            selectedPathLabel.setText("Path:"+currentPath);
            Tracking.echo(currentPath);
            try {
              String [] newItems =  currentRemoteObj.changeDirAndListContent(currentPath);

              DefaultListModel<String> model = getNewListModel( newItems ) ;
              //filePathsBase = new ArrayList<String>( Arrays.asList( newItems) );
              lsListOfCurrentDirAndfile.setModel(  model  );
            } catch (RemoteException ex) {

              Tracking.error(true,"up btn addActionListener "+ex.getLocalizedMessage());
              ex.printStackTrace();
            }
          }else {

            JOptionPane.showMessageDialog(dialog, currentPath+" root directory",
                "hint directory...", JOptionPane.INFORMATION_MESSAGE );

            Tracking.echo(currentPath+" Root directory");
          }
        }else{

          JOptionPane.showMessageDialog(dialog, "Explore my friend",
              "hint directory...", JOptionPane.INFORMATION_MESSAGE );

          Tracking.echo("Explore my friend");
        }
      }
          );

      buttonPane.add(btUpBtn);

      //

      JButton btCopyBtn = new JButton("Copy");
      btCopyBtn.addActionListener(e-> {
        String selectedElem = lsListOfCurrentDirAndfile.getSelectedValue();
        selectedElem = ( selectedElem == null )?"":selectedElem;
        String selectedPath = StringHandler.separatorsToSystem(currentPath+selectedElem ).trim();
        if( !selectedPath.isEmpty() && !selectedPath.endsWith( File.separator )) {
          finalPaths = selectedPath;
          Tracking.echo("------------ " +finalPaths);

          synchronized (FileChooser.class) {
            lock = false ;
          }
          dialog.dispose();
        }else {
          JOptionPane.showMessageDialog(dialog, "you must choose a file",
              "Error message...", JOptionPane.ERROR_MESSAGE);
        }
      });
      buttonPane.add(btCopyBtn);
      FlowLayout fLay = ((new FlowLayout(FlowLayout.RIGHT)));
      fLay.setVgap(2);
      buttonPane.setLayout(fLay);
      dialog.getContentPane().add(buttonPane, BorderLayout.SOUTH);

    }
    dialog.setDefaultCloseOperation( dialog.DISPOSE_ON_CLOSE );
    dialog.setModal(true);
    dialog.setContentPane( dialog.getContentPane() );
    //pack();
    //this.setModalityType(DEFAULT_MODALITY_TYPE);
    dialog.setLocationRelativeTo(null);
    dialog.requestFocus();
    dialog.setModalityType( ModalityType.APPLICATION_MODAL);
    dialog.setAlwaysOnTop(true);
    dialog.setResizable(false);
    dialog.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent evt) {
        synchronized (FileChooser.class) {
          lock = false ;
        }
        dialog.dispose();
        //System.exit(0);
      }
    });



  }
  public String getSelectedPaths() {
    return finalPaths ;
  }

  private static DefaultListModel<String> getNewListModel(String[] arr) {
    if( arr ==  null ) return new DefaultListModel<String>();
    ArrayList<String>  a = new ArrayList<String>( Arrays.asList(arr) );
    return getNewListModel( a );
  }
  private static DefaultListModel<String> getNewListModel(ArrayList<String> arr)  {
    if( arr ==  null ) return new DefaultListModel<String>();
    DefaultListModel<String> demoList = new DefaultListModel<String>();
    for( String a : arr ) {
      //      String st = "**  ";
      //
      //      // Tracking.echo(a);
      //      if(  a.endsWith("/") || a.endsWith("\\") ) st="D*  ";
      //      else   st ="-*  ";
      demoList.addElement(a);
    }
    return demoList ;
  }

}
