package me.elhoussam.window;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import me.elhoussam.interfaces.infoInterface;
import me.elhoussam.util.log.Tracking;

public class FileChooser2 {

  private static infoInterface currentRemoteObj = null;

  private JDialog dialog = new JDialog();
  private final JPanel contentPanel = new JPanel();
  static ArrayList<String> filePathsBase = new ArrayList<String>();
  static ArrayList<String> fileNamesBase = new ArrayList<String>();
  static String currentPath = "";
  static Boolean lock = true ;

  private byte level = 0;
  private String   finalPaths = ""  ;
  /**
   * Launch the application.
   */

  public int showOpenDialog() throws RemoteException{
    // FileChooser2.currentRemoteObj = selectedObj ;
    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    dialog.setVisible(true);
    while( lock ) {
      try {
        Thread.sleep(2*1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
        return -1;
      }
    }
    return 0;
  }

  public static byte showUp(infoInterface selectedObj) throws RemoteException {
    new FileChooser2(selectedObj);
    //a.setVisible( true );
    //a.setModal(true);
    while( lock ) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    return 1;
  }

  /**
   * Create the dialog.
   */
  public FileChooser2(infoInterface selectedObj ) throws RemoteException{
    dialog.setBounds(100, 100, 243, 319);

    FileChooser2.currentRemoteObj = selectedObj ;
    ArrayList<String> rootNames = FileChooser2.currentRemoteObj.getRootDir(false);
    ArrayList<String> rootPaths = FileChooser2.currentRemoteObj.getRootDir(true);
    filePathsBase = (ArrayList<String>) rootPaths.clone();

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
          return "Path : " +separatorsToSystem( currentPath  + item);
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
                selectedElement = separatorsToSystem( selectedElement );

                if ( selectedElement.endsWith(File.separator) ) {
                  // change path
                  currentPath = fixEndingOf(currentPath) + selectedElement;
                  //change JList Element (model)
                  try {
                    Tracking.echo("list#Current Path `"+currentPath+"`" );
                    String [] newItems = FileChooser2.currentRemoteObj.changeDirAndListContent(currentPath);

                    DefaultListModel<String> model = getNewListModel( newItems ) ;
                    filePathsBase = new ArrayList<String>( Arrays.asList( newItems) );
                    lsListOfCurrentDirAndfile.setModel(  model  );
                  } catch (RemoteException e1) {
                    e1.printStackTrace();
                  }
                }else {
                  // change path

                  Tracking.echo("selected file " +   fixEndingOf(currentPath) + selectedElement   );
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
    contentPanel.add( new JScrollPane(lsListOfCurrentDirAndfile), BorderLayout.CENTER);

    JComboBox<String> cmbListOfMainDir  = new JComboBox<String>(  );
    for (String a : rootNames) {
      cmbListOfMainDir.addItem(a);
    }
    cmbListOfMainDir.addActionListener( e -> {
      JComboBox<String> cb = (JComboBox<String>)e.getSource();
      int selectedIndex = cb.getSelectedIndex();
      cb.getSelectedItem();
      String fullPath = fixEndingOf( separatorsToSystem(rootPaths.get(selectedIndex)) ) ;
      try {
        currentPath = fullPath ;
        Tracking.echo("Current Path `"+currentPath+"`" );
        String [] newItems = FileChooser2.currentRemoteObj.changeDirAndListContent(fullPath);

        DefaultListModel<String> model = getNewListModel( newItems ) ;
        filePathsBase = new ArrayList<String>( Arrays.asList( newItems) );
        lsListOfCurrentDirAndfile.setModel(  model  );
      } catch (RemoteException e1) {
        e1.printStackTrace();
      }
    } );
    contentPanel.add(cmbListOfMainDir, BorderLayout.NORTH);



    lsListOfCurrentDirAndfile.setFont(new Font("Tahoma", Font.BOLD, 15));

    {
      JPanel buttonPane = new JPanel();
      JButton btUpBtn = new JButton("UP");
      btUpBtn.addActionListener(e-> {

        Tracking.echo("UP\t"+currentPath);
        //
        if( !currentPath.isEmpty() ) {
          if(  !checkIfExist(currentPath, rootPaths) ) {
            String currentPathParts [] = currentPath.split( Pattern.quote(File.separator) );
            String upDir = "";//( currentPath.startsWith(File.separator) )?File.separator:""
            for( String el : currentPathParts ) {
              if( !currentPathParts[ currentPathParts.length-1 ].trim().equals(el) ) {
                upDir += el+((el.trim().isEmpty())?"":File.separator)   ;
              }
            }
            currentPath = upDir ;
            Tracking.echo(currentPath);
            try {
              String [] newItems = FileChooser2.currentRemoteObj.changeDirAndListContent(currentPath);

              DefaultListModel<String> model = getNewListModel( newItems ) ;
              filePathsBase = new ArrayList<String>( Arrays.asList( newItems) );
              lsListOfCurrentDirAndfile.setModel(  model  );
            } catch (RemoteException e1) {
              e1.printStackTrace();
            }
          }else {

            Tracking.echo(currentPath+" Root directory");
          }
        }else{
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
        String selectedPath = separatorsToSystem(currentPath+selectedElem ).trim();
        if( !selectedPath.isEmpty() && !selectedPath.endsWith( File.separator )) {
          finalPaths = selectedPath;
          Tracking.echo("------------ " +finalPaths);

          //Tracking.echo(" level "+level);
          synchronized (FileChooser2.class) {
            lock = false ;
          }
          dialog.dispose();
        }
      });
      buttonPane.add(btCopyBtn);



      buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
      dialog.getContentPane().add(buttonPane, BorderLayout.SOUTH);

    }
    dialog.setDefaultCloseOperation( dialog.DISPOSE_ON_CLOSE );
    //setModal(true);
    dialog.setContentPane( dialog.getContentPane() );
    //pack();
    //this.setModalityType(DEFAULT_MODALITY_TYPE);
    dialog.setLocationRelativeTo(null);
    dialog.requestFocus();
    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    dialog.setAlwaysOnTop(true);
    dialog.setResizable(false);



  }
  public String getSelectedPaths() {
    return finalPaths ;
  }
  public byte getLevel() {
    return level ;
  }
  static Boolean checkIfExist(String e, ArrayList<String> arr) {
    for (byte i = 0; i < arr.size(); i++) {
      if ( separatorsToSystem(arr.get(i)).equals(e))
        return true;
    }
    return false;
  }
  private static String fixEndingOf( String str ) {
    if ( str.endsWith( File.separator ) ) {
      return str ;
    }else {
      return str.concat( File.separator);
    }
  }
  private static DefaultListModel<String> getNewListModel(String[] arr) {
    if( arr ==  null ) return new DefaultListModel<String>();
    ArrayList<String>  a = new ArrayList<String>( Arrays.asList(arr) );
    return getNewListModel( a  );
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
  private static String separatorsToSystem(String res) {
    if (res == null)
      return null;
    if (File.separatorChar == '\\') {
      // From Windows to Linux/Mac
      return res.replace('/', File.separatorChar);
    } else {
      // From Linux/Mac to Windows
      return res.replace('\\', File.separatorChar);
    }
  }

}