package sample;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

public class NavigationPageController {

    // Contains the user zoom setting
    @FXML
    private Slider zoom;

    // Contains the user email entry
    @FXML
    private TextField email;

    // Contains the map, object path is necessary otherwise the wrong imageview loads -F
    @FXML
    private javafx.scene.image.ImageView map;

    // Contains the desired user destination
    @FXML
    private TextField destination;

    // Contains stairs option
    @FXML
    private CheckBox stairs;

    // Contains the elevator option
    @FXML
    private  CheckBox elevator;

    // Contains the Invalid email error message
    @FXML
    private static Label invalidEmailText;

    @FXML
    public void dragMap(){
        //map.setX();
        //map.setY();
    }

    // Purpose: Zoom the map ImageView
    @FXML
    public void zoomMap(){
        System.out.println("Zooming ");
        double sliderSetting = zoom.getValue();
        map.setScaleX(sliderSetting * 0.1);
        map.setScaleY(sliderSetting * 0.1);
    }

    // The go button next to the destination text field, starts pathfinding algorithm, direction print, map drawing
    @FXML
    public void go() throws IOException,InterruptedException{
        Vector<Node> Vec = new Vector<Node>(10);
        Node n1 = new Node("FDEPT00101", 1614, 829, 1, "Tower", "DEPT", "Center for International Medecine", "CIM", 'F');
        Vec.addElement(n1);
        Node n2 = new Node("FHALL00201", 1640, 850, 1, "Tower", "HALL", "Chapel Hall Point 1", "CHP1", 'F');
        Vec.addElement(n2);
        Node n3 = new Node("FHALL00301", 1788, 850, 1, "Tower", "HALL", "Chapel Hall Point 2", "CHP2", 'F');
        Vec.addElement(n3);
        Node n4 = new Node("FHALL00701", 1759, 900, 1, "Tower", "HALL", "Chapel Hall Entrance", "CHE", 'F');
        Vec.addElement(n4);
        Node n5 = new Node("FHALL01301", 1760, 952, 1, "Tower", "HALL", "International Hall Point 2", "IHP2", 'F');
        Vec.addElement(n5);
        Node n6 = new Node("FSERV00101", 1724, 930, 1, "Tower", "SERV", "Multifaith Chapel", "MFC", 'F');
        Vec.addElement(n6);

        Vector<Node> InverseVec = new Vector<Node>(10);

        InverseVec.addElement(n6);
        InverseVec.addElement(n5);
        InverseVec.addElement(n4);
        InverseVec.addElement(n3);
        InverseVec.addElement(n3);
        InverseVec.addElement(n2);
        InverseVec.addElement(n1);
        drawDirections(InverseVec);
    }

    // Method to clear the path on the map when the user presses clear map
    @FXML
    public void clear() throws FileNotFoundException{
        map.setImage(new Image(new FileInputStream("./TeamF-0.1/src/sample/UI/Icons/01_thefirstfloor.png")));
    }

    //sets invalid email label when necessary for errorhandling
    @FXML
    public static void setInvalidEmail(){
        invalidEmailText.setVisible(true);
    }

    // User clicks send email button
    @FXML
    public void sendMsg() throws Exception{
        Vector<Node> msgVec = new Vector<Node>(10);

        //here we would add the node elements to the thing however it goes, rn I'm adding manually
        Node n1 = new Node("FDEPT00101", 1614, 829, 1, "Tower", "DEPT", "Center for International Medecine", "CIM", 'F');
        msgVec.addElement(n1);
        Node n2 = new Node("FHALL00201", 1640, 850, 1, "Tower", "HALL", "Chapel Hall Point 1", "CHP1", 'F');
        msgVec.addElement(n2);
        Node n3 = new Node("FHALL00301", 1788, 850, 1, "Tower", "HALL", "Chapel Hall Point 2", "CHP2", 'F');
        msgVec.addElement(n3);
        Node n4 = new Node("FHALL00701", 1759, 900, 1, "Tower", "HALL", "Chapel Hall Entrance", "CHE", 'F');
        msgVec.addElement(n4);
        Node n5 = new Node("FHALL01301", 1760, 952, 1, "Tower", "HALL", "International Hall Point 2", "IHP2", 'F');
        msgVec.addElement(n5);
        Node n6 = new Node("FSERV00101", 1724, 930, 1, "Tower", "SERV", "Multifaith Chapel", "MFC", 'F');
        msgVec.addElement(n6);

        EmailService emailService = new EmailService("teamFCS3733@gmail.com", "FuschiaFairiesSoftEng");
        emailService.sendEmail(NavigationPageController.directions(msgVec), email.getText());
    }

    // Button to return to the welcome screen
    @FXML
    public void back(){
        Main.startScreen();
    }

    // Purpose: Print out directions for a path of nodes
    public static String directions(Vector<Node> in){
        String out = "";
        Node a, b, c;
        if(in.size()<2){
            out = out.concat("Path too short");
        }
        a = in.get(0);
        b = in.get(1);
        out = out.concat("Start at " + a.getLongName()+"<br>");
        out = out.concat("Go towards " + b.getLongName()+"<br>");

        for(int i = 2; i < in.size(); i++){
            a = in.get(i-2);
            b = in.get(i-1);
            c = in.get(i);
            String turn = "";
            double angle = NodeMath.findAngle(a.getxCoordinate(), a.getyCoordinate(), b.getxCoordinate(), b.getyCoordinate(), c.getxCoordinate(), c.getyCoordinate());
            if(angle<45){
                turn = "sharply right";
            }else if(angle < 135){
                turn = "right";
            }else if(angle < 225){
                turn = "straight";
            }else if(angle <315){
                turn = "left";
            }else{
                turn = "sharply left";
            }

            out = out.concat("When you arrive at " + b.getLongName() + " go " + turn + " towards " + c.getLongName() + "<br>");
        }
        return out;
    }

    // Purpose: Draw a path on the map
    @FXML
    public void drawDirections(Vector<Node> path) throws IOException,InterruptedException {
        String nameDep = path.get(0).getShortName();
        int length = path.size();
        String nameDest = path.get(length - 1).getShortName();

        // Opening the image
        BufferedImage firstFloor = ImageIO.read(getClass().getResource("/sample/UI/Icons/01_thefirstfloor.png"));
        Graphics2D pathImage =  firstFloor.createGraphics();

        // Setting up the proper color settings
        pathImage.setStroke(new BasicStroke(7)); // Controlling the width of the shapes drawn
        pathImage.setColor( new java.awt.Color(2,97,187)); // This color is the same blue as logo

        // Iterate through all the path nodes to draw the path
        for(int i = 0; i < length ; i++) {
            Node node = path.get(i);
            pathImage.drawOval(node.getxCoordinate() - 10,node.getyCoordinate() - 10,20,20);
            pathImage.fillOval(node.getxCoordinate() - 10,node.getyCoordinate() - 10,20,20);
            if(i + 1 < length){
                Node node2 = path.get(i+1);
                // Lines are drawn offset,
                pathImage.drawLine(node.getxCoordinate(), node.getyCoordinate(),node2.getxCoordinate() ,node2.getyCoordinate());
            }
        }

        // Saving the image in a new file, uses the departure location and destination in the name of the map
        ImageIO.write(firstFloor, "PNG", new File("./TeamF-0.1/src/sample/UI/GeneratedImages/path" + nameDep + "-" + nameDest + ".png"));
        Thread.sleep(500); // Wait before reading and setting the image as the new map
        // Set the saved image as the new map
        map.setImage(new Image(new FileInputStream("./TeamF-0.1/src/sample/UI/GeneratedImages/path" + nameDep + "-" + nameDest + ".png")));
        System.out.println("Image edited and saved");
    }
}
/*sample.UI.GeneratedImages ".sample.UI.GeneratedImages.path"
        src\sample\UI\GeneratedImages*/
//sample/UI/GeneratedImages/pathMFC-CIM.png