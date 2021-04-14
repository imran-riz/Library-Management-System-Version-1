import javafx.application.* ;
import javafx.stage.* ;
import javafx.scene.* ;
import javafx.scene.control.* ;
import javafx.scene.layout.* ;
import javafx.scene.shape.* ;
import javafx.scene.paint.* ;
import javafx.scene.input.* ;
import javafx.event.* ;
import javafx.beans.binding.* ;
import javafx.geometry.* ;

public class EditBookBox
{
	private final double SCENE_WIDTH = 500 ;
	private final double SCENE_HEIGHT = 425 ;

	private Stage window ;
	private Scene scene ;
	private Label label_1, label_2,  label_3, label_4, label_5 ;
	private TextField titleField, authorField, isbnField, copiesField, bookIdField ;
	private HBox hbox_1, hbox_2,  hbox_3, hbox_4, hbox_5 ;
	private VBox vbox ;
	private Button saveBtn ;

	private MessageBox msgBox = new MessageBox() ;


	public void show(Book book, Controller controller, TableView table)
	{
		window = new Stage() ;
		window.setTitle("EDIT BOOK") ;
		window.initModality(Modality.APPLICATION_MODAL) ;   // the initModality specifies that the stage will block events from...
		window.setResizable(false) ;

		label_1 = new Label("Title") ;
		label_1.setPrefWidth(75) ;
		titleField = new TextField(book.getTitle()) ;
		titleField.setPrefWidth(250) ;

		label_2 = new Label("Author") ;
		label_2.setPrefWidth(75) ;
		authorField = new TextField(book.getAuthor()) ;
		authorField.setPrefWidth(250) ;

		label_3 = new Label("ISBN") ;
		label_3.setPrefWidth(75) ;
		isbnField = new TextField(book.getIsbn()) ;
		isbnField.setPrefWidth(250) ;

		label_4 = new Label("Book ID") ;
		label_4.setPrefWidth(75) ;
		bookIdField = new TextField(book.getBookID()) ;
		bookIdField.setPrefWidth(250) ;
		bookIdField.setEditable(false) ;

		label_5 = new Label("Copies") ;
		label_5.setPrefWidth(75) ;
		copiesField = new TextField(book.getCopiesAsString()) ;
		copiesField.setPrefWidth(250) ;

		hbox_1 = new HBox() ;
		hbox_1.getChildren().addAll(label_1, titleField) ;

		hbox_2 = new HBox() ;
		hbox_2.getChildren().addAll(label_2, authorField) ;

		hbox_3 = new HBox() ;
		hbox_3.getChildren().addAll(label_3, isbnField) ;
		
		hbox_4 = new HBox() ;
		hbox_4.getChildren().addAll(label_4, bookIdField) ;

		hbox_5 = new HBox() ;
		hbox_5.getChildren().addAll(label_5, copiesField) ;

		saveBtn = new Button("Save Changes") ;		
		saveBtn.setOnAction(event ->
		{
			Book updated_book = new Book(bookIdField.getText(), titleField.getText(), authorField.getText(), isbnField.getText()) ;
			updated_book.setCopies(copiesField.getText()) ;

			if(controller.updateBook(book.getBookID(), updated_book) == false)
			{
				msgBox.show("Failed to update details! Try again later", "ERROR") ;
			}
			else
			{
				int index = table.getItems().indexOf(book) ;

				table.getItems().remove(book) ;
				table.getItems().add(index, updated_book) ;
				window.close() ;				
			}
		}) ;

		VBox saveBtn_vbox = new VBox(saveBtn) ;
		saveBtn_vbox.setPadding(new Insets(20, 0, 0, 0)) ;
		saveBtn_vbox.setAlignment(Pos.BOTTOM_CENTER) ;

		vbox = new VBox(10) ;
		vbox.getChildren().addAll(hbox_1, hbox_2, hbox_3, hbox_4, hbox_5, saveBtn_vbox) ;
		vbox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE) ;
		vbox.setAlignment(Pos.CENTER) ;

		Rectangle rectangle = new Rectangle() ;
		rectangle.setWidth(SCENE_WIDTH-70) ;
		rectangle.setHeight(SCENE_HEIGHT-70) ;
		rectangle.setFill(Color.WHITE) ;

		StackPane stackPane = new StackPane() ;
		stackPane.getChildren().addAll(rectangle, vbox) ;
		stackPane.setStyle("-fx-background-color : black") ;

		scene = new Scene(stackPane, SCENE_WIDTH, SCENE_HEIGHT) ;
		scene.getStylesheets().add("EditBookBox_style.css") ;

		window.setScene(scene) ;
		window.showAndWait() ;
	}
}