import javafx.application.* ;
import javafx.stage.* ;
import javafx.scene.* ;
import javafx.scene.layout.* ;
import javafx.scene.control.* ;
import javafx.geometry.* ;

public class MessageBox
{
	public static void show(String message, String title)	// 2 parameters : the message to be displayed and the box's title
	{
		Stage primary_stage = new Stage() ; 
		primary_stage.initModality(Modality.APPLICATION_MODAL) ;   // the initModality specifies that the stage will block events from...
		primary_stage.setTitle(title) ; 						  //...reaching any other stages in the application
		primary_stage.setResizable(false) ;
		
		Label lbl = new Label() ; 
		lbl.setText(message) ;
		
		Button btnOK = new Button() ; 
		btnOK.setText("OK") ;
		btnOK.setOnAction(e -> primary_stage.close()) ; 	//add an EventHandler. Calls the close() method of the Stage() class
		
		VBox pane = new VBox(20) ; 
		pane.getChildren().addAll(lbl, btnOK) ;
		pane.setAlignment(Pos.CENTER) ; 	  //the setAlignment() method is called specifying that the label and button must be centered
		pane.setMinWidth(220) ;
		pane.setMinHeight(120) ;

		Scene scene = new Scene(pane) ; 
		primary_stage.setScene(scene) ;
		primary_stage.showAndWait() ;
	}
}