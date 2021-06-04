import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.event.* ;
import javafx.collections.* ;
import javafx.scene.control.* ;
import javafx.scene.control.cell.PropertyValueFactory ;
import javafx.scene.control.cell.TextFieldTableCell ;
import javafx.scene.layout.* ;
import javafx.scene.Group ;
import javafx.scene.shape.*;
import javafx.scene.input.* ;
import javafx.scene.image.* ;
import javafx.scene.paint.*;
import javafx.scene.text.* ;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.util.* ;
import javafx.beans.binding.* ;
import javafx.geometry.* ;
import java.util.* ;
import java.io.* ;

// javac --module-path "%PATH_TO_FX%" --add-modules javafx.controls -cp ".;mysql-connector-java-8.0.23.jar" App.java
// java --module-path "%PATH_TO_FX%" --add-modules javafx.controls -cp ".;mysql-connector-java-8.0.23.jar" App

public class App extends Application
{
	private final int SCENE_WIDTH = 1020 ;
	private final int SCENE_HEIGHT = 550 ;

	private Stage window ;
	private Scene startScene, selectAcct_scene, memberPageScene, adminPageScene ;
	private Pane root ;

	private TreeView<String> theTree ;

	private TableView<Book> theTable = new TableView<Book>() ;
	private TableView<Book> currentTableInView ;	// node in lookAtMyshelf()

	private Controller controller ;
	private ConfirmationBox confirmBox = new ConfirmationBox() ;
	private EditBookBox editBookBox = new EditBookBox() ;
	private MessageBox msgBox = new MessageBox() ;
	private Person theUser ;

	private ImageView openingImage, searchImage, memberImage, adminImage ;
	private Rectangle rect = new Rectangle() ;
	private ObservableList<Book> listOfAllBooks ;


	public static void main(String[] args) 
	{
		Application.launch(args) ;
	}

	@Override
	public void start(Stage primaryStage)
	{
		window = primaryStage ;
		window.setTitle("L I B R A R Y") ;
		window.setResizable(false) ;

		try
		{
			controller = new Controller() ;
			listOfAllBooks = controller.getBookList() ;
		}
		catch(Exception e)
		{
			System.out.println(e);
		}

		loadImgs() ;

		Label mainHeader = new Label("THE LIBRARY IN THE MIDDLE OF NOWHERE") ;
		mainHeader.setPrefHeight(15) ;
		mainHeader.setId("welcomeLabel_style") ;

		Rectangle rectangle0 = new Rectangle() ;
		rectangle0.setWidth(SCENE_WIDTH) ;
		rectangle0.setHeight(75) ;
		rectangle0.setFill(Color.GRAY) ;
		rectangle0.setOpacity(0.7) ;

		VBox vbox_header = new VBox(mainHeader) ;
		vbox_header.setAlignment(Pos.TOP_CENTER) ;
		vbox_header.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE) ;
		vbox_header.setPadding(new Insets(15, 0, 0, 0)) ;

		Button search_btn = new Button("SEARCH") ;
		search_btn.setId("optionBtn_style") ;
		search_btn.setPrefWidth(150) ;
		search_btn.setOnAction(event -> 
		{
			Label header = new Label("LOOK FOR A BOOK") ;
			header.setId("header_style") ;
			
			VBox vbox = new VBox(header) ;
			vbox.setPadding(new Insets(10)) ;
			vbox.setAlignment(Pos.TOP_CENTER) ;

			VBox searchTable_vbox = searchBookLayout(false, 250, null) ;	// the number passed is the max height of the TableView
			searchTable_vbox.setAlignment(Pos.CENTER) ;
			searchTable_vbox.setPadding(new Insets(10)) ;

			Button backBtn = new Button("BACK") ;
			backBtn.setOnAction(e -> window.setScene(startScene)) ;
			backBtn.setId("backBtn_style") ;

			VBox vbox2 = new VBox(backBtn) ;
			vbox2.setPadding(new Insets(10)) ;
			vbox2.setAlignment(Pos.BOTTOM_CENTER) ;

			VBox mainVBox = new VBox(10) ;
			mainVBox.getChildren().addAll(vbox, searchTable_vbox, vbox2) ;
			mainVBox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE) ;
			mainVBox.setAlignment(Pos.CENTER) ;

			rect.setWidth(870) ;
			rect.setHeight(450) ;
			rect.setFill(Color.GRAY) ;
			rect.setOpacity(0.7) ;

			StackPane stack_pane = new StackPane() ;
			stack_pane.getChildren().addAll(searchImage, rect, mainVBox) ;

			Scene tempScene = new Scene(stack_pane, SCENE_WIDTH, SCENE_HEIGHT) ;
			tempScene.getStylesheets().add("OpeningStyle.css") ;

			window.setScene(tempScene) ;
		}) ;

		Button signInBtn = new Button("SIGN IN") ;
		signInBtn.setPrefWidth(150) ;
		signInBtn.setId("optionBtn_style") ;
		signInBtn.setOnAction(e -> signInScreen()) ;

		VBox vbox = new VBox(10) ;
		vbox.getChildren().addAll(search_btn, signInBtn) ;
		vbox.setAlignment(Pos.CENTER) ;
		vbox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE) ;

		Rectangle rectangle = new Rectangle() ;
		rectangle.setWidth(300) ;
		rectangle.setHeight(200) ;
		rectangle.setFill(Color.ORANGE) ;
		rectangle.setOpacity(0.7) ;

		StackPane stack_pane1 = new StackPane(rectangle0, vbox_header) ;
		stack_pane1.setAlignment(Pos.TOP_CENTER) ;

		StackPane stack_pane2 = new StackPane(rectangle, vbox) ;
		stack_pane2.setAlignment(Pos.CENTER) ;

		BorderPane b_pane = new BorderPane() ;
		b_pane.setTop(stack_pane1) ;
		b_pane.setCenter(stack_pane2) ;

		StackPane mainStackPane = new StackPane() ;
		mainStackPane.getChildren().addAll(openingImage, b_pane) ;

		startScene = new Scene(mainStackPane, SCENE_WIDTH, SCENE_HEIGHT) ;
		startScene.getStylesheets().add("OpeningStyle.css") ;

		window.setScene(startScene) ;
		window.show() ;
	}


	private void signInScreen()
	{
		Label header = new Label("SIGN IN TO YOUR ACCOUNT") ;
		header.setId("header_style") ;

		VBox vbox_header = new VBox(header) ;
		vbox_header.setPadding(new Insets(0, 0, 30, 0)) ;
		vbox_header.setAlignment(Pos.CENTER) ;
		vbox_header.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE) ;

		Label prompt = new Label("I am a ") ;
		prompt.setId("instruction_style") ;

		ComboBox<String> acctType = new ComboBox<String>() ;
		acctType.getItems().addAll("MEMBER", "ADMIN") ;
		acctType.setValue("MEMBER") ;
		acctType.setEditable(false) ;

		HBox hbox = new HBox(10) ;
		hbox.setPadding(new Insets(10)) ;
		hbox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE) ;
		hbox.getChildren().addAll(prompt, acctType) ;

		Label warning = new Label("") ;
		warning.setId("warning_style") ;

		Label lbl_1 = new Label("Username ") ;
		lbl_1.setId("instruction_style") ;
		lbl_1.setPrefWidth(100) ;

		TextField user_field = new TextField() ;
		user_field.setPrefWidth(170) ;
		user_field.setOnKeyPressed(event -> warning.setText("")) ;

		HBox hbox_1 = new HBox(10) ;
		hbox_1.getChildren().addAll(lbl_1, user_field) ;
		hbox_1.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE) ;

		Label lbl_2 = new Label("Password ") ;
		lbl_2.setId("instruction_style") ;
		lbl_2.setPrefWidth(100) ;

		PasswordField pswrd_field = new PasswordField() ;
		pswrd_field.setPrefWidth(170) ;
		pswrd_field.setOnKeyPressed(event -> warning.setText("")) ;

		HBox hbox_2 = new HBox(10) ;
		hbox_2.getChildren().addAll(lbl_2, pswrd_field) ;
		hbox_2.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE) ;

		Button signInBtn = new Button("SIGN IN") ;
		signInBtn.setId("signInBtn_style") ;
		signInBtn.setPrefWidth(100) ;
		signInBtn.setPrefHeight(25) ;		
		signInBtn.setOnAction(e -> 
		{
			theUser = controller.signInToAcct(acctType.getValue(), user_field.getText(), pswrd_field.getText()) ;

			if(theUser != null)
			{
				if(acctType.getValue().equals("ADMIN"))
					adminPage() ;
				else
					memberPage() ;
			}
			else
			{
				warning.setText("Incorrect username or password!") ; 
			}
		}) ;

		Button backBtn = new Button("BACK") ;
		backBtn.setId("backBtn_style") ;
		backBtn.setOnAction(e -> window.setScene(startScene)) ;

		VBox vbox1 = new VBox(backBtn) ;
		vbox1.setPadding(new Insets(10)) ;
		vbox1.setAlignment(Pos.TOP_LEFT) ;
		vbox1.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE) ;

		VBox vbox2 = new VBox(10) ;
		vbox2.getChildren().addAll(warning, signInBtn) ;
		vbox2.setPadding(new Insets(12, 0, 0, 0)) ;
		vbox2.setAlignment(Pos.CENTER) ;
		vbox2.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE) ;

		VBox vbox = new VBox(10) ;
		vbox.getChildren().addAll(vbox_header, hbox, hbox_1, hbox_2, vbox2) ;
		vbox.setAlignment(Pos.CENTER) ;

		BorderPane b_pane = new BorderPane() ;		
		b_pane.setCenter(vbox) ;
		b_pane.setTop(vbox1) ;

		Rectangle rectangle = new Rectangle() ;
		rectangle.setWidth(350) ;
		rectangle.setHeight(400) ;
		rectangle.setFill(Color.WHITE) ;
		rectangle.setOpacity(0.7) ;

		StackPane stack_pane = new StackPane() ;
		stack_pane.setId("stack_pane_style") ;
		stack_pane.getChildren().addAll(rectangle, b_pane) ;

		Scene signInScreen_scene = new Scene(stack_pane, SCENE_WIDTH, SCENE_HEIGHT) ;
		signInScreen_scene.getStylesheets().add("LoginStyle.css") ;

		window.setScene(signInScreen_scene) ;
	}


	private void adminPage()
	{
		BorderPane b_pane = new BorderPane() ;		

		String str = "Welcome, " + theUser.getFirstName() + "!" ;

		Label acctHeader = new Label("ADMINISTRATOR") ;
		acctHeader.setId("acctHeader_style") ;
		
		Label welcomeLabel = new Label(str) ;
		welcomeLabel.setId("welcomeLabel_style") ;

		Label lbl_1 = new Label(("Your ID is " + theUser.getId())) ;
		lbl_1.setPrefWidth(120) ;
		lbl_1.setId("justSomeText") ;
		lbl_1.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE) ;

		VBox vbox1 = new VBox(15) ;
		vbox1.setPadding(new Insets(10)) ;
		vbox1.getChildren().addAll(acctHeader, welcomeLabel) ;
		vbox1.setAlignment(Pos.TOP_CENTER) ;

		VBox vbox2 = new VBox(10) ;
		vbox2.setPadding(new Insets(10)) ;
		vbox2.getChildren().addAll(lbl_1) ;
		vbox2.setAlignment(Pos.CENTER) ;

		VBox searchTable_vbox = searchBookLayout(true, 200, "ADMIN") ;	// the number passed is the max height of the TableView
		searchTable_vbox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE) ;
		searchTable_vbox.setAlignment(Pos.CENTER) ;

		Button signOut_btn = new Button("SIGN OUT") ;
		signOut_btn.setId("signOutBtn_style") ;
		signOut_btn.setOnAction(event ->
		{
			if(confirmBox.show(" Sign out of account? ", "SIGN OUT", "Yes", "No") == true)
				window.setScene(startScene) ;
		}) ;

		VBox vbox3 = new VBox(signOut_btn) ;
		vbox3.setPadding(new Insets(100, 10, 15, 10)) ;		// top, right, bottom, left
		vbox3.setAlignment(Pos.BOTTOM_RIGHT) ;

		StackPane stack_pane = new StackPane() ;

		TreeItem<String> root, homeItem, newBookItem, borrowRequestsItem, returnsMadeItem, issuedBooksItem, editProfileItem ;

		root = new TreeItem<>() ;
		root.setExpanded(true) ;

		homeItem = makeBranch("HOME", root) ;
		newBookItem = makeBranch("Add A New Book", root) ;
		borrowRequestsItem = makeBranch("Reservations Made", root) ;
		returnsMadeItem = makeBranch("Returns Made", root) ;
		issuedBooksItem = makeBranch("Issued Books", root) ;
		editProfileItem = makeBranch("Edit Profile", root) ;

		theTree = new TreeView<>(root) ;
		theTree.setShowRoot(false) ;
		theTree.getSelectionModel().selectedItemProperty()
			.addListener((observable, oldValue, newValue) ->
			{
				if(newValue != null)
				{
					if(newValue.getParent() == root)
					{
						if(newValue.getValue().equals("HOME"))
						{
							b_pane.setCenter(stack_pane) ;
						}
						else
						{
							if(newValue.getValue().equals("Add A New Book"))
							{
								addBook(b_pane) ;
							}
							else if(newValue.getValue().equals("Reservations Made"))
							{
								lookAtReservationsMade(b_pane) ;
							}
							else if(newValue.getValue().equals("Returns Made"))
							{
								lookAtReturnRequests(b_pane) ;
							}
							else if(newValue.getValue().equals("Issued Books"))
							{
								lookAtAllIssuedBooks(b_pane) ;
							}
							else if(newValue.getValue().equals("Edit Profile"))
							{
								VBox editProfile_vbox = editProfile("ADMIN") ;
								editProfile_vbox.setAlignment(Pos.CENTER) ;
								editProfile_vbox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE) ;

								Rectangle rectangle = new Rectangle() ;
								rectangle.setWidth(SCENE_WIDTH-270) ;
								rectangle.setHeight(SCENE_HEIGHT-140) ;
								rectangle.setFill(Color.BLACK) ;
								rectangle.setOpacity(0.5) ;

								StackPane stack_pane2 = new StackPane() ;
								stack_pane2.getChildren().addAll(rectangle, editProfile_vbox) ;

								b_pane.setCenter(stack_pane2) ;
							}
						}
					}
				}
			}) ;
        theTree.setPrefWidth(165) ;
        theTree.setPrefHeight(SCENE_HEIGHT) ;
        theTree.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE) ;

        MultipleSelectionModel multipleSelection_model = theTree.getSelectionModel() ;
        int row = theTree.getRow(homeItem) ;
        multipleSelection_model.select(row) ;

		Rectangle rect = new Rectangle() ;
		rect.setWidth(SCENE_WIDTH-170) ;
		rect.setHeight(SCENE_HEIGHT-30) ;
		rect.setFill(Color.BLACK) ;
		rect.setOpacity(0.5) ;

		VBox mainVBox = new VBox(10) ;
		mainVBox.setPadding(new Insets(10)) ;
		mainVBox.getChildren().addAll(vbox1, vbox2, searchTable_vbox, vbox3) ;
		mainVBox.setAlignment(Pos.TOP_CENTER) ;
		
		stack_pane.getChildren().addAll(rect, mainVBox) ;

		b_pane.setLeft(theTree) ;
		b_pane.setCenter(stack_pane) ;

		StackPane stackPane = new StackPane() ;
		stackPane.getChildren().addAll(adminImage, b_pane) ;

		adminPageScene = new Scene(stackPane, SCENE_WIDTH, SCENE_HEIGHT) ;
		adminPageScene.getStylesheets().add("AdminPageStyle.css") ;

		window.setScene(adminPageScene) ;
	}


	private void addBook(BorderPane borderPane)
	{
		Label header = new Label("ADD A BOOK") ;
		header.setId("header_style") ;

		Label title_lbl = new Label("Title") ;
		title_lbl.setId("instruction_style") ;
		title_lbl.setPrefWidth(100) ;
		TextField titleField = new TextField("") ;
		titleField.setPrefWidth(300) ;
		HBox hbox_1 = new HBox(10) ;
		hbox_1.getChildren().addAll(title_lbl, titleField) ;

		Label author_lbl = new Label("Author") ;
		author_lbl.setId("instruction_style") ;
		author_lbl.setPrefWidth(100) ;
		TextField authorField = new TextField("") ;
		authorField.setPrefWidth(250) ;
		HBox hbox_2 = new HBox(10) ;
		hbox_2.getChildren().addAll(author_lbl, authorField) ;

		Label isbn_lbl = new Label("ISBN") ;
		isbn_lbl.setId("instruction_style") ;
		isbn_lbl.setPrefWidth(100) ;
		TextField isbnField = new TextField("") ;
		isbnField.setPrefWidth(150) ;
		HBox hbox_3 = new HBox(10) ;
		hbox_3.getChildren().addAll(isbn_lbl, isbnField) ;

		Label bookID_lbl = new Label("Book ID") ;
		bookID_lbl.setId("instruction_style") ;
		bookID_lbl.setPrefWidth(100) ;
		TextField bookIDField = new TextField("") ;
		bookIDField.setPrefWidth(70) ;
		HBox hbox_4 = new HBox(10) ;
		hbox_4.getChildren().addAll(bookID_lbl, bookIDField) ;

		Label copies_lbl = new Label("Number Of Copies") ;
		copies_lbl.setId("instruction_style") ;
		copies_lbl.setPrefWidth(150) ;
		TextField copiesField = new TextField("") ;
		copiesField.setPrefWidth(50) ;
		HBox hbox_5 = new HBox(10) ;
		hbox_5.getChildren().addAll(copies_lbl, copiesField) ;

		HBox hbox_6 = new HBox(50) ;
		hbox_6.getChildren().addAll(hbox_4, hbox_5) ;

		Button add_btn = new Button("ADD") ;
		add_btn.setId("add_or_save_btn_style") ;
		add_btn.setOnAction(e ->
		{
			if(titleField.getText().isEmpty() || authorField.getText().isEmpty() || isbnField.getText().isEmpty() || bookIDField.getText().isEmpty() || copiesField.getText().isEmpty())
			{
				msgBox.show("All the fields must be filled!", "ATTENTION!") ;
			}
			else
			{
				if(controller.addNewBook(bookIDField.getText(), titleField.getText(), authorField.getText(), isbnField.getText(), Integer.parseInt(copiesField.getText())) == false)
				{
					msgBox.show("Something went wrong. Failed to add book. Try againg later", "ERROR!") ;
				}
				else
				{
					theTable.getItems().remove(listOfAllBooks) ;	// remove the list of books diplayed on the current table on screen
					listOfAllBooks = controller.getBookList() ;		// get the new list of books 
					theTable.setItems(listOfAllBooks) ;				// set it to th table

					msgBox.show("Book successfully added!", "BOOK ADDED!") ;
					titleField.setText("") ;
					authorField.setText("") ;
					isbnField.setText("") ;
					bookIDField.setText("") ;
					copiesField.setText("") ;
				}
			}
		}) ;

		VBox vbox = new VBox(header) ;
		vbox.setPadding(new Insets(10)) ;
		vbox.setAlignment(Pos.TOP_CENTER) ;

		VBox vbox2 = new VBox(add_btn) ;
		vbox2.setPadding(new Insets(10)) ;
		vbox2.setAlignment(Pos.BOTTOM_CENTER) ;

		VBox mainVBox = new VBox(10) ;
		mainVBox.setPadding(new Insets(10)) ;
		mainVBox.setAlignment(Pos.CENTER) ;
		mainVBox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE) ;
		mainVBox.getChildren().addAll(vbox, hbox_1, hbox_2, hbox_3, hbox_6, vbox2) ;

		Rectangle rectangle = new Rectangle() ;
		rectangle.setWidth(SCENE_WIDTH-210) ;
		rectangle.setHeight(SCENE_HEIGHT-170) ;
		rectangle.setFill(Color.BLACK) ;
		rectangle.setOpacity(0.5) ;

		StackPane stackPane = new StackPane() ;
		stackPane.getChildren().addAll(rectangle, mainVBox) ;

		borderPane.setCenter(stackPane) ;
	}


	private void lookAtReservationsMade(BorderPane borderPane)
	{
		Label header = new Label("RESERVED BOOKS") ;
		header.setId("header_style") ;

		TableView<Book> table = getReservedBooks_asTable(controller.getAllReservedBooks(), true) ;
		table.setMaxSize(Region.USE_PREF_SIZE, 220) ;
		table.setOnMouseClicked(event ->
		{
			if(event.getClickCount() == 2)
			{
				Book bookSelected = table.getSelectionModel().getSelectedItem() ;

				if(confirmBox.show((" Issue the book " + bookSelected.getTitle() + " by " + bookSelected.getAuthor() + " to the holder of the ID number " + bookSelected.getIssuedToID() + "? "), "CONFIRM", "Yes", "No") == true)
				{
					if(controller.issueBook(bookSelected) == false)
					{
						msgBox.show("Failed to issue the book. Try again later", "ERROR!") ;
					}
					else
					{
						msgBox.show("The book was successfully issued!", "SUCCESS!") ;
						table.getItems().remove(bookSelected) ;
					}
				}
			}
		}) ;

		VBox vbox = new VBox(header) ;
		vbox.setPadding(new Insets(10, 0, 20, 0)) ;		// top, right, bottom, left
		vbox.setAlignment(Pos.TOP_CENTER) ;

		VBox mainVBox = new VBox(10) ;
		mainVBox.setPadding(new Insets(10)) ;
		mainVBox.setAlignment(Pos.CENTER) ;
		mainVBox.getChildren().addAll(vbox, table) ;

		Rectangle rectangle = new Rectangle() ;
		rectangle.setWidth(SCENE_WIDTH-170) ;
		rectangle.setHeight(SCENE_HEIGHT-50) ;
		rectangle.setFill(Color.BLACK) ;
		rectangle.setOpacity(0.5) ;

		StackPane stackPane = new StackPane() ;
		stackPane.getChildren().addAll(rectangle, mainVBox) ;

		borderPane.setCenter(stackPane) ;
	}


	private void lookAtReturnRequests(BorderPane borderPane)
	{
		Label header = new Label("RETURN REQUESTS") ;
		header.setId("header_style") ;

		TableView<Book> table = getReturnRequests_table(controller.getAllBooksToBeReturned(), true) ;
		table.setMaxSize(Region.USE_PREF_SIZE, 220) ;
		table.setOnMouseClicked(event ->
		{
			if(event.getClickCount() == 2)
			{
				Book bookSelected = table.getSelectionModel().getSelectedItem() ;

				if(confirmBox.show((" Confirm the return of the book " + bookSelected.getTitle() + " by " + bookSelected.getAuthor() + " from the holder of the ID number " + bookSelected.getIssuedToID() + "? "), "CONFIRM", "Yes", "No") == true)
				{
					if(controller.returnBook(bookSelected) == false)
					{
						msgBox.show("Failed to confirm the book's return. Try again later", "ERROR!") ;
					}
					else
					{
						table.getItems().remove(bookSelected) ;
					}
				}
			}
		}) ;

		VBox vbox = new VBox(header) ;
		vbox.setPadding(new Insets(10, 0, 20, 0)) ;		// top, right, bottom, left
		vbox.setAlignment(Pos.TOP_CENTER) ;

		VBox mainVBox = new VBox(10) ;
		mainVBox.setPadding(new Insets(10)) ;
		mainVBox.setAlignment(Pos.CENTER) ;
		mainVBox.getChildren().addAll(vbox, table) ;

		Rectangle rectangle = new Rectangle() ;
		rectangle.setWidth(SCENE_WIDTH-170) ;
		rectangle.setHeight(SCENE_HEIGHT-50) ;
		rectangle.setFill(Color.BLACK) ;
		rectangle.setOpacity(0.5) ;

		StackPane stackPane = new StackPane() ;
		stackPane.getChildren().addAll(rectangle, mainVBox) ;

		borderPane.setCenter(stackPane) ;
	}

	private void lookAtAllIssuedBooks(BorderPane borderPane)
	{
		Label header = new Label("ISSUED BOOKS") ;
		header.setId("header_style") ;

		TableView<Book> table = getIssuedBooks_asTable(controller.getAllIssuedBooks(), true) ;
		table.setMaxSize(Region.USE_PREF_SIZE, 220) ;

		VBox vbox = new VBox(header) ;
		vbox.setPadding(new Insets(10, 0, 20, 0)) ;		// top, right, bottom, left
		vbox.setAlignment(Pos.TOP_CENTER) ;

		VBox mainVBox = new VBox(10) ;
		mainVBox.setPadding(new Insets(10)) ;
		mainVBox.setAlignment(Pos.CENTER) ;
		mainVBox.getChildren().addAll(vbox, table) ;

		Rectangle rectangle = new Rectangle() ;
		rectangle.setWidth(SCENE_WIDTH-170) ;
		rectangle.setHeight(SCENE_HEIGHT-50) ;
		rectangle.setFill(Color.BLACK) ;
		rectangle.setOpacity(0.5) ;

		StackPane stackPane = new StackPane() ;
		stackPane.getChildren().addAll(rectangle, mainVBox) ;

		borderPane.setCenter(stackPane) ;
	}


	private void memberPage()
	{
		Label acctHeader = new Label("MEMBER") ;
		acctHeader.setId("acctHeader_style") ;

		String str = "Welcome, " + theUser.getFirstName() + "!" ;

		Label welcomeLabel = new Label(str) ;
		welcomeLabel.setId("welcomeLabel_style") ;

		Label lbl_1 = new Label("Your MemberID : " + theUser.getId()) ;
		lbl_1.setId("instruction_style") ;

		VBox search_vbox = searchBookLayout(true, 200, "MEMBER") ;	// the number passed is the max height of the TableView
		search_vbox.setAlignment(Pos.CENTER) ;

		Button myshelf_btn = new Button("My Shelf") ;
		myshelf_btn.setId("optionBtn_style") ;
		myshelf_btn.setPrefWidth(120) ;
		myshelf_btn.setOnAction(event -> lookAtMyShelf()) ;

		Button editProfileBtn = new Button("Edit Profile") ;
		editProfileBtn.setId("optionBtn_style") ;
		editProfileBtn.setPrefWidth(120) ;
		editProfileBtn.setOnAction(event -> editMemberProfile()) ;

		HBox hbox = new HBox(10) ;
		hbox.getChildren().addAll(myshelf_btn, editProfileBtn) ;
		hbox.setPadding(new Insets(5, 0, 20, 10)) ;
		hbox.setAlignment(Pos.CENTER) ;

		Button signOut_btn = new Button("SIGN OUT") ;
		signOut_btn.setId("signOutBtn_style") ;
		signOut_btn.setPrefHeight(20) ;
		signOut_btn.setOnAction(event ->
		{
			if(confirmBox.show(" Sign out of account? ", "SIGN OUT", "Yes", "No") == true)
				window.setScene(startScene) ;
		}) ;

		VBox vbox1 =new VBox(15) ;
		vbox1.getChildren().addAll(acctHeader, welcomeLabel) ;
		vbox1.setAlignment(Pos.TOP_CENTER) ;
		vbox1.setPadding(new Insets(25, 0, 0, 0)) ;

		VBox vbox2 = new VBox(signOut_btn) ;
		vbox2.setPadding(new Insets(20, 0, 0, 0)) ;	// top, right, bottom, left
		vbox2.setAlignment(Pos.CENTER) ;

		VBox mainVBox = new VBox(10) ;
		mainVBox.getChildren().addAll(lbl_1, hbox, search_vbox, vbox2) ;
		mainVBox.setAlignment(Pos.CENTER) ;

		BorderPane b_pane = new BorderPane() ;
		b_pane.setCenter(mainVBox) ;
		b_pane.setTop(vbox1) ;

		Rectangle rect = new Rectangle() ;
		rect.setWidth(SCENE_WIDTH-70) ;
		rect.setHeight(SCENE_HEIGHT-30) ;
		rect.setFill(Color.WHITE) ;
		rect.setOpacity(0.5) ;

		StackPane stackPane = new StackPane() ;
		stackPane.getChildren().addAll(memberImage, rect, b_pane) ;

		memberPageScene = new Scene(stackPane, SCENE_WIDTH, SCENE_HEIGHT) ;
		memberPageScene.getStylesheets().add("MemberPageStyle.css") ;

		window.setScene(memberPageScene) ;
	}


	// displays the books the user has borrowed, requested and pending returns
	private void lookAtMyShelf()
	{
		Button backBtn = new Button("BACK") ;
		backBtn.setOnAction(event -> memberPage()) ;

		VBox backBtn_vbox = new VBox(backBtn) ;
		backBtn_vbox.setPadding(new Insets(10, 0, 15, -10)) ;	// top, right, bottom, left
		backBtn_vbox.setAlignment(Pos.TOP_LEFT) ;
		backBtn.setId("backBtn_style") ;

		Label header = new Label("") ;
		header.setId("header_style") ;

		VBox vbox_header = new VBox(header) ;
		vbox_header.setAlignment(Pos.TOP_CENTER)  ;
		vbox_header.setPadding(new Insets(5, 0, 10, 0)) ;	// top, right, bottom, left

		TableView<Book> borrowedBooks_table = getIssuedBooks_asTable(controller.getIssuedBooks(theUser.getId()), false) ;
		borrowedBooks_table.setMaxSize(Region.USE_PREF_SIZE, 220) ;
		TableView<Book> reservedBooks_table = getReservedBooks_asTable(controller.getReservedBooks(theUser.getId()), false) ;
		reservedBooks_table.setMaxSize(Region.USE_PREF_SIZE, 220) ;		
		TableView<Book> returnsSentOn_table = getReturnRequests_table(controller.getBooksToBeReturned(theUser.getId()), false) ;
		returnsSentOn_table.setMaxSize(Region.USE_PREF_SIZE, 220) ;

		borrowedBooks_table.setOnMouseClicked(event ->
		{
			if(event.getClickCount() == 2)
			{
				Book bookSelected = borrowedBooks_table.getSelectionModel().getSelectedItem() ;

				if(confirmBox.show((" Return " + bookSelected.getTitle() + " by " + bookSelected.getAuthor() + "? "), "CONFIRM RETURN", "Yes", "No") == true)
				{
					if(controller.requestToReturnBook(bookSelected.getRefNum()) == false)					
						msgBox.show("Failed to return the book. Try again later", "ERROR!") ;

					else
						returnsSentOn_table.getItems().add(bookSelected) ;
				}
			}
		}) ;

		TreeItem<String> root, borrowedBooks_item, reservedBooks_item, returnsSentOn_item ;

		root = new TreeItem<>() ;
		root.setExpanded(true) ;

		borrowedBooks_item = makeBranch("Books Borrowed", root) ;
		reservedBooks_item = makeBranch("Reserved Books", root) ;
		returnsSentOn_item = makeBranch("Pending Returns", root) ;
		
		// VBox that holds the table thats displayed in the center of the BorderPane
		VBox vbox = new VBox() ;
		vbox.setPadding(new Insets(10)) ;
		vbox.setAlignment(Pos.CENTER) ;

		theTree = new TreeView<>(root) ;
		theTree.setShowRoot(false) ;
		theTree.getSelectionModel().selectedItemProperty()
			.addListener((observable, oldValue, newValue) ->
			{
				if(newValue != null)
				{
					if(newValue.getParent() == root)
					{
						vbox.getChildren().clear() ;

						if(newValue.getValue().equals("Books Borrowed"))
						{
							vbox.getChildren().add(borrowedBooks_table) ;
							header.setText("Books Borrowed") ;
							currentTableInView = borrowedBooks_table ;
						}
						else if(newValue.getValue().equals("Reserved Books"))
						{
							vbox.getChildren().add(reservedBooks_table) ;
							header.setText("Books Reserved") ;
							currentTableInView = reservedBooks_table ;
						}
						else if(newValue.getValue().equals("Pending Returns"))
						{
							vbox.getChildren().add(returnsSentOn_table) ;
							header.setText("Pending Returns") ;
							currentTableInView = returnsSentOn_table ;
						}
					}
				}
			});
        theTree.setPrefWidth(150) ;
        theTree.setPrefHeight(SCENE_HEIGHT) ;
        theTree.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE) ;

        MultipleSelectionModel multipleSelection_model = theTree.getSelectionModel() ;
        int row = theTree.getRow(borrowedBooks_item) ;
        multipleSelection_model.select(row) ;

		currentTableInView = borrowedBooks_table ;

		VBox mainVBox = new VBox(10) ;
		mainVBox.getChildren().addAll(backBtn_vbox, vbox_header, vbox) ;
        mainVBox.setMaxSize(SCENE_WIDTH-200, SCENE_HEIGHT) ;
        
        header.setText("Books Borrowed") ;

		Rectangle rectangle = new Rectangle() ;
		rectangle.setWidth(SCENE_WIDTH-185) ;
		rectangle.setHeight(SCENE_HEIGHT-90) ;
		rectangle.setFill(Color.WHITE) ;
		rectangle.setOpacity(0.5) ;

		StackPane stackPane = new StackPane() ;
		stackPane.getChildren().addAll(rectangle, mainVBox) ;

        BorderPane b_pane = new BorderPane() ;
		b_pane.setLeft(theTree) ;
		b_pane.setCenter(stackPane) ;

		StackPane stack_pane = new StackPane() ;
		stack_pane.getChildren().addAll(memberImage, b_pane) ;

		Scene myshelf_scene = new Scene(stack_pane, SCENE_WIDTH, SCENE_HEIGHT) ;
		myshelf_scene.getStylesheets().add("MemberPageStyle.css") ;

		window.setScene(myshelf_scene) ;
	}


	private void editMemberProfile()
	{
		VBox editProfile_vbox = editProfile("MEMBER") ;
		editProfile_vbox.setPadding(new Insets(10)) ;
		editProfile_vbox.setAlignment(Pos.CENTER) ;

		Button backBtn = new Button("BACK") ;
		backBtn.setId("backBtn_style") ;
		backBtn.setOnAction(e -> memberPage()) ;

		VBox vbox1 = new VBox(backBtn) ;
		vbox1.setPadding(new Insets(5)) ;
		vbox1.setAlignment(Pos.TOP_LEFT) ;
		vbox1.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE) ;

		BorderPane b_pane = new BorderPane() ;
		b_pane.setTop(vbox1) ;
		b_pane.setCenter(editProfile_vbox) ;

		Rectangle rect = new Rectangle() ;
		rect.setWidth(360) ;
		rect.setHeight(460) ;
		rect.setFill(Color.WHITE) ;
		rect.setOpacity(0.5) ;

		StackPane stack_pane = new StackPane() ;
		stack_pane.getChildren().addAll(memberImage, rect, b_pane) ;

		Scene tempScene = new Scene(stack_pane, SCENE_WIDTH, SCENE_HEIGHT) ;
		tempScene.getStylesheets().add("MemberPageStyle.css") ;

		window.setScene(tempScene) ;
	}


	// method that displays all the books and allows the user to search
	private VBox searchBookLayout(boolean editable, double tableHeight, String userType)
	{
		VBox mainVBox = new VBox(10) ;

		HBox hbox = new HBox(10) ;
		hbox.setPadding(new Insets(5, 10, 5, 10)) ;	// top, right, bottom, left

		ObservableList<Book> allTheBooks = controller.getBookList() ;

		theTable = getAllBooksAsTable(allTheBooks, false) ;
		theTable.setMaxSize(Region.USE_PREF_SIZE, tableHeight) ;

		if(editable)
		{
			theTable.setOnMouseClicked(event ->
			{
				if(event.getClickCount() == 2)
				{
					Book selectedBook = theTable.getSelectionModel().getSelectedItem() ;

					// if it's a member allow him/her to reserve a book when double-clicked on a tree item
					if(userType.equals("MEMBER"))
					{
						if(theUser.getNumOfBorrowedBooks() < 5)
						{
							if(theUser.hasBorrowed(selectedBook) == false)
							{
								if(confirmBox.show((" Do you wish to reserve " + selectedBook.getTitle() + " by " + selectedBook.getAuthor() + "? "), "ATTENTION!", "Yes", "No"))
								{
									if(selectedBook.getCopies() > 0)
									{
										controller.reserveBook(theUser.getId(), selectedBook) ;
										memberPage() ;
									}
								}
								else
								{								
									theTable.getItems().remove(allTheBooks) ;
									theTable.setItems(allTheBooks) ;
								}
							}
							else
							{
								msgBox.show(" You have already borrowed this book! ", "OK") ;
							}
						}					
						else
						{
							msgBox.show(" You cannot borrow more than 5 books. Return a book to borrow another ", "ATTENTION!") ;
						}
					}
					else if(userType.equals("ADMIN")) // if it's an admin allow him/her to edit a book's details
					{
						editBookBox.show(selectedBook, controller, theTable) ;
					}
				}
			}) ;
		}

		ComboBox<String> cbox = new ComboBox<String>();
		cbox.getItems().addAll("by BookID", "by Title", "by Author", "by ISBN") ;
		cbox.setEditable(false) ;
		cbox.setVisibleRowCount(3) ;	// The ComboBox shows only 4 items and adds a scroll is added automatically
		cbox.setValue("by Book ID") ;

		TextField searchField = new TextField() ;
		searchField.setPrefWidth(320) ;
		searchField.setPromptText("look for a book...") ;
		searchField.setOnKeyPressed(event ->
		{
			if(searchField.getText().trim().isEmpty())
			{
				theTable.getItems().removeAll() ;
				theTable.setItems(allTheBooks) ;
			}
		}) ;

		Button searchBtn = new Button("SEARCH") ;
		searchBtn.setId("searchBtn_style") ;
		searchBtn.setPrefWidth(75) ;
		searchBtn.setOnAction(e ->
		{
			String order_by ;
			Label lbl_1 = new Label("") ;

			switch(cbox.getValue()) 
			{
				case "by Title" : order_by = "Title" ;
								  break ;
				case "by Author" : order_by = "Author" ;
							   	   break ;
				case "by ISBN" : order_by = "ISBN" ;
								 break ;
				default : order_by = "BookID" ;
						  break ;
			}

			if(!searchField.getText().equals(""))
			{
				ObservableList<Book> searchResult_list = controller.getBookList(searchField.getText(), order_by) ;

				if(searchResult_list.isEmpty() == false)
				{
					theTable.getItems().remove(allTheBooks) ;
					theTable.setItems(searchResult_list) ;
				}
				else
				{
					msgBox.show("Book not found!!", "") ;

					theTable.getItems().removeAll() ;
					theTable.setItems(allTheBooks) ;

					return ;
				}
			}
		}) ;

		hbox.getChildren().addAll(cbox, searchField, searchBtn) ;
		hbox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE) ;

		mainVBox.getChildren().addAll(hbox, theTable) ;

		return mainVBox ;
	}


	private VBox editProfile(String type)
	{
		String table ;

		if(type.equals("MEMBER"))
			table = "tblMembers" ;
		else 
			table = "tblAdmins" ;

		Label header, lbl_1, lbl_2, lbl_3, lbl_4, lbl_5, lbl_6, lbl_7, lbl_8 ;
		HBox hbox_1, hbox_2, hbox_3, hbox_4, hbox_5, hbox_6, hbox_7, hbox_8 ;
		TextField firstName_field, lastName_field, username_field, email_field, phone_field, dob_field, address_field ;

		header = new Label("EDIT YOUR PROFILE") ;
		header.setId("header_style") ;
		
		VBox vbox = new VBox(header) ;
		vbox.setPadding(new Insets(0, 0, 20, 0)) ;
		vbox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE) ;
		vbox.setAlignment(Pos.TOP_CENTER) ;


		lbl_1 = new Label("First Name") ;
		lbl_1.setId("instruction_style") ;
		lbl_1.setPrefWidth(150) ;
		firstName_field = new TextField(theUser.getFirstName()) ;
		firstName_field.setPrefWidth(170) ;

		hbox_1 = new HBox(10) ;
		hbox_1.getChildren().addAll(lbl_1, firstName_field) ;


		lbl_2 = new Label("Last Name") ;
		lbl_2.setId("instruction_style") ;
		lbl_2.setPrefWidth(150) ;
		lastName_field = new TextField(theUser.getLastName()) ;
		lastName_field.setPrefWidth(170) ;
		
		hbox_2 = new HBox(10) ;
		hbox_2.getChildren().addAll(lbl_2, lastName_field) ;


		lbl_3 = new Label("Username") ;
		lbl_3.setId("instruction_style") ;
		lbl_3.setPrefWidth(150) ;
		username_field = new TextField(theUser.getUsername()) ;
		username_field.setPrefWidth(170) ;
		
		hbox_3 = new HBox(10) ;
		hbox_3.getChildren().addAll(lbl_3, username_field) ;


		lbl_4 = new Label("Password") ;
		lbl_4.setId("instruction_style") ;
		lbl_4.setPrefWidth(150) ;
		PasswordField password_field = new PasswordField() ;
		password_field.setPrefWidth(170) ;
		password_field.setText(theUser.getPassword()) ;
		
		hbox_4 = new HBox(10) ;
		hbox_4.getChildren().addAll(lbl_4, password_field) ;


		lbl_5 = new Label("Email Address") ;
		lbl_5.setId("instruction_style") ;
		lbl_5.setPrefWidth(150) ;
		email_field = new TextField() ;
		email_field.setPrefWidth(170) ;
		email_field.setText(theUser.getEmail()) ;

		hbox_5 = new HBox(10) ;
		hbox_5.getChildren().addAll(lbl_5, email_field) ;


		lbl_6 = new Label("Phone Number") ;
		lbl_6.setId("instruction_style") ;
		lbl_6.setPrefWidth(150) ;
		phone_field = new TextField() ;
		phone_field.setPrefWidth(170) ;
		phone_field.setText(theUser.getPhone()) ;

		hbox_6 = new HBox(10) ;
		hbox_6.getChildren().addAll(lbl_6, phone_field) ;

		VBox vbox2 = new VBox(10) ;
		vbox2.setPadding(new Insets(10)) ;
		vbox2.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE) ;
		vbox2.setAlignment(Pos.CENTER) ;

		dob_field = new TextField() ;
		dob_field.setPrefWidth(170) ;
		address_field = new TextField() ;
		address_field.setPrefWidth(170) ;

		if(type.equals("MEMBER"))
		{
			lbl_7 = new Label("Date Of Birth") ;
			lbl_7.setId("instruction_style") ;
			lbl_7.setPrefWidth(150) ;			
			dob_field.setText(theUser.getDateOfBirth()) ;

			hbox_7 = new HBox(10) ;
			hbox_7.getChildren().addAll(lbl_7, dob_field) ;

			lbl_8 = new Label("Address") ;
			lbl_8.setId("instruction_style") ;
			lbl_8.setPrefWidth(150) ;			
			address_field.setText(theUser.getAddress()) ;

			hbox_8 = new HBox(10) ;
			hbox_8.getChildren().addAll(lbl_8, address_field) ;

			vbox2.getChildren().addAll(hbox_1, hbox_2, hbox_7, hbox_3, hbox_4, hbox_5, hbox_6, hbox_8) ;
		}
		else
		{
			vbox2.getChildren().addAll(hbox_1, hbox_2, hbox_3, hbox_4, hbox_5, hbox_6) ;
		}


		Button saveBtn = new Button("Save Changes") ;
		saveBtn.setId("add_or_save_btn_style") ;
		saveBtn.setOnAction(event ->
		{
			// temporarily create a Person object using the details in the TextFields
			Person tempPerson = new Person(theUser.getId(), firstName_field.getText(), lastName_field.getText(), username_field.getText(), password_field.getText()) ;			
			tempPerson.setPhone(phone_field.getText()) ;
			tempPerson.setEmail(email_field.getText()) ;
			if(type.equals("MEMBER"))
			{
				tempPerson.setDateOfBirth(dob_field.getText()) ;
				tempPerson.setAddress(address_field.getText()) ;
			}
			else
			{
				tempPerson.setNic(theUser.getNic()) ;
			}

			// first check if the username in the TextField does not exist in the table under any other account
			if(controller.doesUsernameExist(theUser.getId(), username_field.getText(), table) == false)
			{
				// if the database server successfully updated the table
				if(controller.updateProfile(table, tempPerson) == true)
				{
					theUser = tempPerson ;
					msgBox.show(" Profile edited! ", "SUCCESS") ;					
				}
				else
				{
					msgBox.show(" Oops! Something went wrong. Failed to save your edits ", "ERROR") ;
				}
			}
			else
			{
				msgBox.show(" Username already exists! ", "ERROR") ;
			}
		}) ;

		VBox vbox3 = new VBox(saveBtn) ;
		vbox3.setPadding(new Insets(20, 0, 30, 0)) ;	// top, right, bottom, left
		vbox3.setAlignment(Pos.BOTTOM_CENTER) ;

		VBox mainVBox = new VBox(10) ;
		mainVBox.setPadding(new Insets(10)) ;
		mainVBox.getChildren().addAll(vbox, vbox2, vbox3) ;

		return mainVBox ;
	}


    private TreeItem<String> makeBranch(String title, TreeItem<String> parent) 
    {
        TreeItem<String> item = new TreeItem<>(title) ;
        item.setExpanded(false) ;

        parent.getChildren().add(item) ;

        return item ;
    }	


    // accepts a ObservableList and a boolean value - if set to true, the user can make changes to the cells. Returns a TableView with all the books' details
	private TableView<Book> getAllBooksAsTable(ObservableList<Book> list, boolean editableCells)
	{
		TableView<Book> table = new TableView<Book>() ;	 

		TableColumn<Book, String> bookID_column = new TableColumn<Book, String>("Book ID") ;
		bookID_column.setPrefWidth(60) ;
		bookID_column.setCellValueFactory(new PropertyValueFactory<Book, String>("bookID")) ;
		bookID_column.setSortable(false) ;			

		TableColumn<Book, String> title_column = new TableColumn<Book, String>("Title") ;
		title_column.setPrefWidth(350) ;
		title_column.setCellValueFactory(new PropertyValueFactory<Book, String>("title")) ;
		title_column.setSortable(false) ;			

		TableColumn<Book, String> author_column = new TableColumn<Book, String>("Author") ;	
		author_column.setPrefWidth(205) ;
		author_column.setCellValueFactory(new PropertyValueFactory<Book, String>("author")) ;
		author_column.setSortable(false) ;			

		TableColumn<Book, String> copies_column = new TableColumn<Book, String>("Copies") ;
		copies_column.setPrefWidth(62) ;
		copies_column.setCellValueFactory(new PropertyValueFactory<Book, String>("copiesAsString")) ;
		copies_column.setSortable(false) ;			

		TableColumn<Book, String> isbn_column = new TableColumn<Book, String>("ISBN") ;
		isbn_column.setPrefWidth(140) ;
		isbn_column.setCellValueFactory(new PropertyValueFactory<Book, String>("isbn")) ;
		isbn_column.setSortable(false) ;


		table.setItems(list) ;		// pass the ObservableList
		table.getColumns().addAll(bookID_column, title_column, author_column, copies_column, isbn_column) ;
		table.setFixedCellSize(25) ;	
		table.prefHeightProperty().bind(Bindings.size(table.getItems()).multiply(table.getFixedCellSize()).add(30)) ;
		table.setMinHeight(100) ;

		return table ;
	}


	// takes an ObservableList and a boolean value. Returns a TableView containing the details' of the book/s a user has borrowed
	private TableView<Book> getIssuedBooks_asTable(ObservableList<Book> list, boolean all_issuedBooks)
	{
		TableView<Book> table = new TableView<Book>() ;

		// create the columns
		TableColumn<Book, String> refNum_column, bookID_column, title_column, issued_date_col, due_date_column ;
		TableColumn<Book, String> personIssuedTo_col, reserved_on_col, returned_on_col ;

		refNum_column = new TableColumn<Book, String>("RefNum") ;	// the header to the column is "RefNum". 
		refNum_column.setCellValueFactory(new PropertyValueFactory<Book, String>("refNum")) ; // "refNum" is the name of the variable in the Book class
		refNum_column.setSortable(false) ;			// doesn't allow the user to sort the table

		bookID_column = new TableColumn<Book, String>("Book ID") ;
		bookID_column.setCellValueFactory(new PropertyValueFactory<Book, String>("bookID")) ;
		bookID_column.setSortable(false) ;			// doesn't allow the user to sort the table

		title_column = new TableColumn<Book, String>("Title") ;
		title_column.setCellValueFactory(new PropertyValueFactory<Book, String>("title")) ;
		title_column.setSortable(false) ;			// doesn't allow the user to sort the table

		issued_date_col = new TableColumn<Book, String>("Issued On") ;
		issued_date_col.setCellValueFactory(new PropertyValueFactory<Book, String>("issuedDate")) ;
		issued_date_col.setSortable(false) ;

		due_date_column = new TableColumn<Book, String>("Due Date") ;
		due_date_column.setCellValueFactory(new PropertyValueFactory<Book, String>("dueDate")) ;
		due_date_column.setSortable(false) ;

		personIssuedTo_col = new TableColumn<Book, String>("ID Of Person") ;
		reserved_on_col = new TableColumn<Book, String>("Reserved On") ;
		returned_on_col = new TableColumn<Book, String>("Returned On") ;

		if(all_issuedBooks == true)		// inserts the ID of the person who borrowed the book, when it was issued and returned
		{			
			personIssuedTo_col.setCellValueFactory(new PropertyValueFactory<Book, String>("issuedToID")) ;
			personIssuedTo_col.setSortable(false) ;			// doesn't allow the user to sort the table
			
			reserved_on_col.setCellValueFactory(new PropertyValueFactory<Book, String>("reservedDate")) ;
			reserved_on_col.setSortable(false) ;
			
			returned_on_col.setCellValueFactory(new PropertyValueFactory<Book, String>("returnedOn")) ;
			returned_on_col.setSortable(false) ;
		}

		table.setItems(list) ;		// pass the ObservableList
		table.setFixedCellSize(25) ;			
		table.prefHeightProperty().bind(Bindings.size(table.getItems()).multiply(table.getFixedCellSize()).add(30)) ;
		table.setMinHeight(100) ;

		if(all_issuedBooks)
		{
			table.getColumns().addAll(refNum_column, personIssuedTo_col, bookID_column, title_column, reserved_on_col, issued_date_col, returned_on_col) ;

			refNum_column.setPrefWidth(65) ;
			bookID_column.setPrefWidth(65) ;
			title_column.setPrefWidth(320) ;
			issued_date_col.setPrefWidth(100) ;
			personIssuedTo_col.setPrefWidth(92) ;
			returned_on_col.setPrefWidth(100) ;
			reserved_on_col.setPrefWidth(100) ;
		}
		else
		{
			table.getColumns().addAll(refNum_column, bookID_column, title_column, issued_date_col, due_date_column) ;

			refNum_column.setPrefWidth(65) ;
			bookID_column.setPrefWidth(65) ;
			title_column.setPrefWidth(320) ;
			issued_date_col.setPrefWidth(100) ;
			due_date_column.setPrefWidth(100) ;
		}

		return table ;
	}


	// creates and returns a TableView of the reserved books
	private TableView<Book> getReservedBooks_asTable(ObservableList<Book> list, boolean all_reservedBooks)
	{
		TableView<Book> table = new TableView<Book>() ;

		TableColumn<Book, String> refNum_column, bookID_column, title_column, reservedDate_column  ;
		TableColumn<Book, String> reservedBy_column ;

		refNum_column = new TableColumn<Book, String>("RefNum") ;
		refNum_column.setPrefWidth(70) ;
		refNum_column.setCellValueFactory(new PropertyValueFactory<Book, String>("refNum")) ;
		refNum_column.setSortable(false) ;			// doesn't allow the user to sort the table

		bookID_column = new TableColumn<Book, String>("Book ID") ;
		bookID_column.setPrefWidth(70) ;
		bookID_column.setCellValueFactory(new PropertyValueFactory<Book, String>("bookID")) ;
		bookID_column.setSortable(false) ;			// doesn't allow the user to sort the table

		title_column = new TableColumn<Book, String>("Title") ;
		title_column.setPrefWidth(350) ;
		title_column.setCellValueFactory(new PropertyValueFactory<Book, String>("title")) ;
		title_column.setSortable(false) ;			// doesn't allow the user to sort the table

		reservedDate_column = new TableColumn<Book, String>("Reserved On") ;
		reservedDate_column.setPrefWidth(105) ;
		reservedDate_column.setCellValueFactory(new PropertyValueFactory<Book, String>("reservedDate")) ;
		reservedDate_column.setSortable(false) ;			// doesn't allow the user to sort the table

		reservedBy_column = new TableColumn<Book, String>("ID Of Person") ;
		if(all_reservedBooks)
		{
			reservedBy_column.setPrefWidth(100) ;
			reservedBy_column.setCellValueFactory(new PropertyValueFactory<Book, String>("issuedToID")) ;
			reservedBy_column.setSortable(false) ;			// doesn't allow the user to sort the table
		}

		table.setItems(list) ;		// pass the ObservableList
		table.setFixedCellSize(25) ;			
		table.prefHeightProperty().bind(Bindings.size(table.getItems()).multiply(table.getFixedCellSize()).add(30)) ;
		table.setMinHeight(100) ;		

		if(all_reservedBooks)
			table.getColumns().addAll(refNum_column, reservedBy_column, bookID_column, title_column, reservedDate_column) ;
		else
			table.getColumns().addAll(refNum_column, bookID_column, title_column, reservedDate_column) ;

		return table ;
	}


	//  returns a TableView with data of the books a user/s have requested to return
	private TableView<Book> getReturnRequests_table(ObservableList<Book> list, boolean all_returnRequests)
	{
		TableView<Book> table = new TableView<Book>() ;

		// create the columns
		TableColumn<Book, String> refNum_column, bookID_column, title_column, issued_date_col, returnRequest_sent_date_col ;
		TableColumn<Book, String> personIssuedTo_col ;

		refNum_column = new TableColumn<Book, String>("RefNum") ;	// the header to the column is "RefNum". 
		refNum_column.setPrefWidth(70) ;
		refNum_column.setCellValueFactory(new PropertyValueFactory<Book, String>("refNum")) ; // "refNum" is the name of the variable in the Book class
		refNum_column.setSortable(false) ;			// doesn't allow the user to sort the table

		bookID_column = new TableColumn<Book, String>("Book ID") ;
		bookID_column.setPrefWidth(65) ;
		bookID_column.setCellValueFactory(new PropertyValueFactory<Book, String>("bookID")) ;
		bookID_column.setSortable(false) ;			// doesn't allow the user to sort the table

		title_column = new TableColumn<Book, String>("Title") ;
		title_column.setPrefWidth(350) ;
		title_column.setCellValueFactory(new PropertyValueFactory<Book, String>("title")) ;
		title_column.setSortable(false) ;			// doesn't allow the user to sort the table

		issued_date_col = new TableColumn<Book, String>("Issued On") ;
		issued_date_col.setPrefWidth(105) ;
		issued_date_col.setCellValueFactory(new PropertyValueFactory<Book, String>("issuedDate")) ;
		issued_date_col.setSortable(false) ;

		returnRequest_sent_date_col = new TableColumn<Book, String>("Return Sent On") ;
		returnRequest_sent_date_col.setPrefWidth(110) ;
		returnRequest_sent_date_col.setCellValueFactory(new PropertyValueFactory<Book, String>("returnSentOn")) ;
		returnRequest_sent_date_col.setSortable(false) ;

		personIssuedTo_col = new TableColumn<Book, String>("ID Of Person") ;

		if(all_returnRequests == true)
		{			
			personIssuedTo_col.setPrefWidth(100) ;
			personIssuedTo_col.setCellValueFactory(new PropertyValueFactory<Book, String>("issuedToID")) ;
			personIssuedTo_col.setSortable(false) ;			// doesn't allow the user to sort the table
		}

		table.setItems(list) ;		// pass the ObservableList
		table.setFixedCellSize(25) ;			
		table.prefHeightProperty().bind(Bindings.size(table.getItems()).multiply(table.getFixedCellSize()).add(30)) ;
		table.setMinHeight(100) ;

		if(all_returnRequests)
			table.getColumns().addAll(refNum_column, personIssuedTo_col, bookID_column, title_column, returnRequest_sent_date_col, issued_date_col) ;
		else
			table.getColumns().addAll(refNum_column, bookID_column, title_column, returnRequest_sent_date_col, issued_date_col) ;

		return table ;
	}

	private void loadImgs()
	{
		openingImage = new ImageView(new Image("file:opening.jpg")) ;
		openingImage.setFitWidth(SCENE_WIDTH) ;
		openingImage.setFitHeight(SCENE_HEIGHT) ;
		openingImage.setCache(true) ;

		searchImage = new ImageView(new Image("file:searching.jpg")) ;
		searchImage.setFitWidth(SCENE_WIDTH) ;
		searchImage.setFitHeight(SCENE_HEIGHT) ;
		searchImage.setCache(true) ;

		adminImage = new ImageView(new Image("file:admin.jpg")) ;
		adminImage.setFitWidth(SCENE_WIDTH) ;
		adminImage.setFitHeight(SCENE_HEIGHT) ;
		adminImage.setCache(true) ;

		memberImage = new ImageView(new Image("file:member2.jpeg")) ;
		memberImage.setFitWidth(SCENE_WIDTH) ;
		memberImage.setFitHeight(SCENE_HEIGHT) ;
		memberImage.setCache(true) ;
	}
}
