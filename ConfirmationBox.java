import javafx.application.* ;		// we're gonna create a confirmation box
import javafx.stage.* ;
import javafx.scene.* ;
import javafx.scene.layout.* ;
import javafx.scene.control.* ;
import javafx.scene.text.* ;
import javafx.event.* ;
import javafx.geometry.* ;

public class ConfirmationBox 
{
	static Stage primary_stage ; 
	static boolean btnYesClicked ; 				// keep track of which button is clicked

	public static boolean show(String message, String title, String textYes, String textNo)	
	{					// 4 parameters : the label to be displayed, the title of the ConfirmationBox, the text for the yes_btn and the text for the no_btn
		btnYesClicked = false ;

		primary_stage = new Stage() ; 
		primary_stage.initModality(Modality.APPLICATION_MODAL) ;   // the initModality specifies that the stage will block events from...
		primary_stage.setTitle(title) ;							  //...reaching any other stages in the application
		primary_stage.setResizable(false) ;

		Label lbl = new Label() ; 
		lbl.setText(message) ;

		Button yes_btn = new Button() ; 
		yes_btn.setText(textYes) ;
		yes_btn.setPrefWidth(50) ;
		yes_btn.setPrefHeight(20) ;
		yes_btn.setOnAction(e -> yes_btn_clicked() ) ;

		Button no_btn = new Button() ;
		no_btn.setText(textNo) ;
		no_btn.setPrefWidth(50) ;
		no_btn.setPrefHeight(20) ;
		no_btn.setOnAction(e -> no_btn_clicked() ) ;

		HBox paneBtn = new HBox(20) ; 
		paneBtn.getChildren().addAll(yes_btn, no_btn) ;
		paneBtn.setAlignment(Pos.CENTER) ;

		VBox pane = new VBox(20) ; 						// note that the VBox pane is used as the root node
		pane.getChildren().addAll(lbl, paneBtn) ;
		pane.setAlignment(Pos.CENTER) ;
		pane.setMinWidth(250) ;
		pane.setMinHeight(150) ;

		Scene scene = new Scene(pane) ; 
		primary_stage.setScene(scene) ;
		primary_stage.showAndWait() ;

		return btnYesClicked ; 		// return the value of btnYesClicked to notify what the user has selected
	}
	
	private static void yes_btn_clicked() 
	{
		btnYesClicked = true ;		// the yes_btn has been clicked
		primary_stage.close() ;
	}
	
	private static void no_btn_clicked() 
	{
		btnYesClicked = false ;		// the no_btn has been clicked
		primary_stage.close() ;
	}
}