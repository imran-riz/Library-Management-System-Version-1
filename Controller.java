import java.sql.* ;
import java.io.* ;
import java.util.* ;
import java.time.LocalDate ;
import java.text.SimpleDateFormat ;
import javafx.collections.* ;

// java -cp ".;mysql-connector-java-8.0.23.jar" Controller

public class Controller
{
	private Connection conn ;
	private Statement stmnt ;


	public Controller() throws Exception
	{
		String dbName = "dbLibrary" ;		// the name of the existing database being used
		String url = "jdbc:mysql://localhost:3306/" ;		// where localhost is the name of the server hosting the database, and 3306 is the port number
		String password = getPassword() ;

		try
		{
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance() ;	// register the driver class dynamically

			// establish a database connection
			conn = DriverManager.getConnection(url.concat(dbName), "root", password) ;	// pass the url with the database name, username and password
		}
		catch(Exception e)
		{
			System.out.println("Exceptions In Controller constructor -> \n" + e + "\n") ;
		}
	}


	// checks if the username and password are in the respective database table. If its found, a Person object is returned
	public Person signInToAcct(String type, String username, String password)
	{
		Person person = null ;

		String table = "tblAdmins" ;
		String column = "AdminID" ;

		if(username.equals("NULL") || password.equals("NULL")) return null ;

		try
		{
			if(type.toUpperCase().equals("MEMBER"))
			{
				table = "tblMembers" ;
				column = "MemberID" ;
			}

			String query = "SELECT * FROM " + table + " WHERE Username = '" + username + "' ;" ;			

			Statement stmnt = conn.createStatement() ;

			ResultSet resultSet = stmnt.executeQuery(query) ;

			if(resultSet.next())	// if the received username is in the table then...
			{
				String storedPswrd = resultSet.getString("Password") ;	//...get the respective password stored				
				String storedUsername = resultSet.getString("Username") ;

				if(storedPswrd.equals(password) && storedUsername.equals(username) && !storedPswrd.equals("NULL") && !storedUsername.equals("NULL"))
				{
					String id = resultSet.getString(column) ;	// get the user's id

					person = new Person(id, resultSet.getString("FirstName"), resultSet.getString("LastName"), username, password) ;
					person.setEmail(resultSet.getString("Email")) ;
					person.setPhone(resultSet.getString("Phone")) ;
					
					if(table.equals("tblMembers"))
					{
						person.setDateOfBirth(resultSet.getString("DOB")) ;
						person.setAddress(resultSet.getString("Address")) ;
					}
					else
					{
						person.setNic(resultSet.getString("NIC_number")) ;
					}

					person.setAllBorrowedBooks(getIssuedBooks(id)) ;
					person.setNumOfBorrowedBooks(getNumOfIssuedBooksOnly(id)) ;
				}
			}

			stmnt.close() ;
			resultSet.close() ;			
		}
		catch(Exception e)
		{
			System.out.println("\nExceptions in Controller.loginToAcct() -> \n" + e) ;
		}

		return person ;
	}


	// adds a new record to tblBooks
	public Boolean addNewBook(String id, String title, String author, String isbn, int numOfCopies)
	{
		String operation = "INSERT INTO tblBooks SET BookID = '" + id + "', Title = \"" + title.toUpperCase() + "\", Author = \"" + author.toUpperCase() + "\", ISBN = '" + isbn + "', Copies = " + numOfCopies + ";" ;
		try
		{
			Statement stmnt = conn.createStatement() ;
			stmnt.execute(operation) ;
			stmnt.close() ;
			return true ;
		}
		catch(Exception e)
		{
			System.out.println("\nExceptions in Controller.addNewBook() -> \n" + e) ;
		}

		return false ;
	}


	// update a book's details
	public Boolean updateBook(String book_id, Book updated_book)
	{
		String operation = "UPDATE tblBooks SET Title = \"" + updated_book.getTitle() + "\", Author = \"" + updated_book.getAuthor() + "\", ISBN = '" + updated_book.getIsbn() + "', Copies = " + updated_book.getCopies() + " WHERE BookID = '" + book_id + "' ;" ;
		String operation_2 = "UPDATE tblBorrowedBooks SET Title = \"" + updated_book.getTitle() + "\" WHERE BookID = '" + book_id + "' ;" ;

		try
		{
			Statement stmnt = conn.createStatement() ;			
			stmnt.execute(operation) ;
			stmnt.execute(operation_2) ;

			return true ;
		}
		catch(Exception e)
		{
			System.out.println("\nExceptions in Controller.updateBook() -> \n" + e) ;
		}

		return false ;
	}


	// adds a new row to tblBorrowedBooks when a user reserves a book
	public Boolean reserveBook(String idOfPerson, Book book)
	{
		String bk_id = book.getBookID() ;
		String bk_title = book.getTitle() ;
		String reserved_date = LocalDate.now().toString() ;
		String ref_num = generateRefNum() ;

		String operation_1 = "INSERT INTO tblBorrowedBooks SET RefNum = '" + ref_num + "', BookID = '" + bk_id + "', PersonIssuedTo_ID = '" + idOfPerson + "', Title = \"" + bk_title + "\", ReservedOn = '" + reserved_date + "' ;" ;
		String operation_2 = "UPDATE tblBooks SET Copies = Copies - 1 WHERE BookID = '" + bk_id + "'"  ;	// reduce the available copies by 1

		try
		{
			Statement stmnt = conn.createStatement() ;
			stmnt.execute(operation_1) ;
			stmnt.execute(operation_2) ;

			return true ;
		}
		catch(Exception e)
		{
			System.out.println("\nExceptions in Controller.reserveBook() -> \n" + e) ;
		}

		return false ;
	}


	// issues a particular book to a person
	public Boolean issueBook(Book book)
	{
		String issued_date = LocalDate.now().toString() ;
		String due_date = LocalDate.now().plusMonths(1).toString() ;
		String issuedTo_id = book.getIssuedToID() ;

		String operation_1 = "UPDATE tblBorrowedBooks SET IssuedOn = '" + issued_date + "', DueDate = '" + due_date + "' WHERE RefNum = '" + book.getRefNum() + "';" ;
		String operation_2 = "UPDATE tblMembers SET BooksBorrowed = BooksBorrowed + 1 WHERE MemberID = '" + issuedTo_id + "' ;";

		try
		{
			Statement stmnt = conn.createStatement() ;
			stmnt.execute(operation_1) ;
			stmnt.execute(operation_2) ;

			stmnt.close() ;

			return true ;
		}
		catch(Exception e)
		{
			System.out.println("\nExceptions in Controller.issueBook() -> \n" + e) ;
		}

		return false ;
	}


	// called when a user requests to return a book
	public Boolean requestToReturnBook(String ref_num)
	{
		String return_sent_on = LocalDate.now().toString() ;

		String operation_1 = "UPDATE tblBorrowedBooks SET ReturnSentOn = '" + return_sent_on + "' WHERE RefNum = '" + ref_num + "';" ;

		try
		{
			Statement stmnt = conn.createStatement() ;
			stmnt.execute(operation_1) ;
			stmnt.close() ;

			return true ;
		}
		catch(Exception e)
		{
			System.out.println("\nExceptions in Controller.requestToReturnBook() -> \n" + e) ;
		}

		return false ;
	}


	// returns a book to tblBooks
	public Boolean returnBook(Book book)
	{
		String refNum = book.getRefNum() ;
		String issuedTo_id = book.getIssuedToID() ;
		String bookId = book.getBookID() ;
		String returned_date = LocalDate.now().toString() ;

		String operation_1 = "UPDATE tblBooks SET Copies = Copies + 1 WHERE BookID = '" + bookId + "';" ;
		String operation_2 = "UPDATE tblMembers SET BooksBorrowed = BooksBorrowed - 1 WHERE MemberID = '" + issuedTo_id  + "';" ;
		String operation_3 = "UPDATE tblBorrowedBooks SET ReturnedOn = '" + returned_date + "' WHERE RefNum = '" + refNum + "';" ;

		try
		{
			Statement stmnt = conn.createStatement() ;
			stmnt.execute(operation_1) ;
			stmnt.execute(operation_2) ;
			stmnt.execute(operation_3) ;

			stmnt.close() ;

			return true ;
		}
		catch(Exception e)
		{
			System.out.println("\nExceptions in Controller.returnBook() -> \n" + e) ;
		}

		return false ;
	}


	public Boolean updateProfile(String tableName, Person person)
	{
		String firstName = person.getFirstName() ;
		String lastName = person.getLastName() ;
		String username = person.getUsername() ;
		String password = person.getPassword() ;
		String email = person.getEmail() ;
		String phoneNum = person.getPhone() ;

		
		String operation = ("UPDATE " + tableName + " SET FirstName = \"" + firstName + "\", LastName = \"" + lastName + "\", Username = \"" + username + "\", Password = \"" + password + "\", Email = \"" + email + "\", Phone = \"" + phoneNum + "\"")  ;

		if(tableName.equals("tblMembers"))
			operation = operation.concat(", Address = \"" + person.getAddress() + "\", DOB = '" + person.getDateOfBirth() + "' WHERE MemberID = '" + person.getId() + "' ;") ;
		else
			operation = operation.concat(" WHERE AdminID = '" + person.getId() + "' ;") ;

		try
		{
			Statement stmnt = conn.createStatement() ;
			stmnt.execute(operation) ;

			return true ;			
		}
		catch(Exception e)
		{
			System.out.println("\nExceptions in Controller.updateProfile() -> \n" + e) ;
		}

		return false ;
	}


	public Boolean doesUsernameExist(String id, String username, String tableName)
	{
		String field = "AdminID" ;

		if(tableName.equals("tblMembers")) field = "MemberID" ;

		String query = "SELECT * FROM " + tableName + " WHERE Username = \"" + username + "\" AND " + field + " <> '" + id + "' ;" ;

		try
		{
			Statement stmnt = conn.createStatement() ;

			ResultSet resultSet = stmnt.executeQuery(query) ;

			return resultSet.next() ;
		}
		catch(Exception e)
		{
			System.out.println("\nExceptions in Controller.doesUsernameExist() -> \n" + e) ;
		}

		return false ;
	}


	public Integer getNumOfIssuedBooksOnly(String id)
	{
		String query = "SELECT COUNT(PersonIssuedTo_ID) AS NumOfIssuedBooks FROM tblBorrowedBooks WHERE (IssuedOn IS NOT NULL AND PersonIssuedTo_ID = '" + id + "');" ;

		try
		{
			Statement stmnt = conn.createStatement() ;
			ResultSet resultSet = stmnt.executeQuery(query) ;

			if(resultSet.next() == true) return resultSet.getInt("NumOfIssuedBooks") ;
		}
		catch(Exception e)
		{
			System.out.println("\nExceptions in Controller.getNumOfIssuedBooksOnly() -> \n" + e) ;
		}

		return 0 ;
	}


	// get the details of the books a user has borrowed and still not returned as an ObservableList of Book type
	public ObservableList<Book> getIssuedBooks(String id)
	{
		String refNum, book_id, book_title, author, isbn, issued_date, due_date ;

		ObservableList<Book> list = FXCollections.observableArrayList() ;

		String query = "SELECT * FROM tblBorrowedBooks WHERE (PersonIssuedTo_ID = '" + id + "' AND IssuedOn IS NOT NULL AND ReturnedOn IS NULL) ORDER BY IssuedOn;" ;

		try
		{
			Book book ;
			Statement stmnt = conn.createStatement() ;

			ResultSet resultSet = stmnt.executeQuery(query) ;

			while(resultSet.next() == true)
			{
				refNum = resultSet.getString("RefNum") ;
				book_id = resultSet.getString("BookID") ;
				book_title = resultSet.getString("Title") ;
				issued_date = resultSet.getString("IssuedOn") ;
				due_date = resultSet.getString("DueDate") ;

				author = getFromBooks("Author", book_id) ;
				isbn = getFromBooks("ISBN", book_id) ;

				book = new Book(book_id, book_title, author, isbn) ;
				book.setRefNum(refNum) ;
				book.setIssuedDate(issued_date) ;
				book.setDueDate(due_date) ;

				list.add(book) ;
			}
		}
		catch(Exception e)
		{
			System.out.println("\nExceptions in Controller.getBorrowedBooks() -> \n" + e) ;
		}

		return list ;
	}


	// gets all the books that have been issued
	public ObservableList<Book> getAllIssuedBooks()
	{
		String ref_num, issuedTo_id, book_id, book_title, author, isbn, reserved_date, issued_date, returned_date, due_date, return_sent_on ;

		ObservableList<Book> list = FXCollections.observableArrayList() ;

		String query = "SELECT * FROM tblBorrowedBooks WHERE IssuedOn IS NOT NULL ORDER BY IssuedOn ;" ;
		
		try
		{
			Statement stmnt = conn.createStatement() ;			

			ResultSet resultSet = stmnt.executeQuery(query) ;

			while(resultSet.next() == true)
			{
				ref_num = resultSet.getString("RefNum") ;
				issuedTo_id = resultSet.getString("PersonIssuedTo_ID") ;
				book_id = resultSet.getString("BookID") ;
				reserved_date = resultSet.getString("ReservedOn") ;
				issued_date = resultSet.getString("IssuedOn") ;
				due_date = resultSet.getString("DueDate") ;
				returned_date = resultSet.getString("ReturnedOn") ;
				return_sent_on = resultSet.getString("ReturnSentOn") ;

				author = getFromBooks("Author", book_id) ;
				book_title = getFromBooks("Title", book_id) ;
				isbn = getFromBooks("ISBN", book_id) ;

				if(returned_date == null) returned_date = "-" ;
				if(return_sent_on == null) return_sent_on = "-" ;

				Book book = new Book(book_id, book_title, author, isbn) ;
				book.setRefNum(ref_num) ;
				book.setIssuedToID(issuedTo_id) ;
				book.setReservedDate(reserved_date) ;
				book.setIssuedDate(issued_date) ;
				book.setDueDate(due_date) ;
				book.setReturnedOn(returned_date) ;
				book.setReturnSentOn(return_sent_on) ;

				list.add(book) ;
			}
		}
		catch(Exception e)
		{
			System.out.println("\nExceptions in Controller.getAllIssuedBooks() -> \n" + e) ;
		}

		return list ;
	}


	// get all the books that have been reserved by a particular member
	public ObservableList<Book> getReservedBooks(String memID)
	{
		String refNum, book_id, book_title, author, isbn, reserved_date ;

		ObservableList<Book> list = FXCollections.observableArrayList() ;

		String query = "SELECT * FROM tblBorrowedBooks WHERE PersonIssuedTo_ID = '" + memID + "' AND (ReservedOn IS NOT NULL AND IssuedOn IS NULL) ORDER BY ReservedOn;" ;

		try
		{
			Book book ;
			Statement stmnt = conn.createStatement() ;

			ResultSet resultSet = stmnt.executeQuery(query) ;

			while(resultSet.next() == true)
			{
				refNum = resultSet.getString("RefNum") ;
				book_id = resultSet.getString("BookID") ;
				book_title = resultSet.getString("Title") ;
				reserved_date = resultSet.getString("ReservedOn") ;

				author = getFromBooks("Author", book_id) ;
				isbn = getFromBooks("ISBN", book_id) ;

//				System.out.printf("\nBook details : %s, %s, %s, %s, %s \n", refNum, book_id, book_title, reserved_date);

				book = new Book(book_id, book_title, author, isbn) ;
				book.setRefNum(refNum);
				book.setReservedDate(reserved_date) ;

				list.add(book) ;
			}
		}
		catch(Exception e)
		{
			System.out.println("\nExceptions in Controller.getReservedBooks() -> \n" + e) ;
		}

		return list ;
	}


	// get all the reserved books only and not the ones that have been issued
	public ObservableList<Book> getAllReservedBooks()
	{
		String refNum, book_id, book_title, author, isbn, reserved_date ;

		ObservableList<Book> list = FXCollections.observableArrayList() ;

		String query = "SELECT * FROM tblBorrowedBooks WHERE ReservedOn IS NOT NULL AND IssuedOn IS NULL ORDER BY ReservedOn DESC;" ;
		
		try
		{
			Book book ;
			Statement stmnt = conn.createStatement() ;

			ResultSet resultSet = stmnt.executeQuery(query) ;

			while(resultSet.next() == true)
			{
				refNum = resultSet.getString("RefNum") ;
				book_id = resultSet.getString("BookID") ;
				book_title = resultSet.getString("Title") ;
				reserved_date = resultSet.getString("ReservedOn") ;

				author = getFromBooks("Author", book_id) ;
				isbn = getFromBooks("ISBN", book_id) ;

//				System.out.printf("\nBook details : %s, %s, %s, %s, %s \n", refNum, book_id, book_title, reserved_date, dueDate);

				book = new Book(book_id, book_title, author, isbn) ;
				book.setIssuedToID(resultSet.getString("PersonIssuedTo_ID")) ;		// set the ID of the person who the book was reserved to
				book.setRefNum(refNum);
				book.setReservedDate(reserved_date) ;

				list.add(book) ;
			}
		}
		catch(Exception e)
		{
			System.out.println("\nExceptions in Controller.getAllReservedBooks() ->\n" + e) ;
		}

		return list ;
	}


	// get all the books that a person wishes to return
	public ObservableList<Book> getBooksToBeReturned(String personId)
	{
		String refNum, book_id, issuedTo_id, book_title, author, isbn, issued_date, return_request_date ;

		ObservableList<Book> list = FXCollections.observableArrayList() ;

		String query = "SELECT * FROM tblBorrowedBooks WHERE ReturnSentOn IS NOT NULL AND PersonIssuedTo_ID = '" + personId + "' ORDER BY ReturnSentOn DESC;" ;

		try
		{
			Book book ;
			Statement stmnt = conn.createStatement() ;

			ResultSet resultSet = stmnt.executeQuery(query) ;

			while(resultSet.next() == true)
			{
				refNum = resultSet.getString("RefNum") ;
				book_id = resultSet.getString("BookID") ;
				issuedTo_id = resultSet.getString("PersonIssuedTo_ID") ;
				book_title = resultSet.getString("Title") ;
				issued_date = resultSet.getString("IssuedOn") ;
				return_request_date = resultSet.getString("ReturnSentOn") ;

				author = getFromBooks("Author", book_id) ;
				isbn = getFromBooks("ISBN", book_id) ;

				book = new Book(book_id, book_title, author, isbn) ;
				book.setRefNum(refNum);
				book.setIssuedDate(issued_date) ;
				book.setIssuedToID(issuedTo_id) ;
				book.setReturnSentOn(return_request_date) ;	

//				System.out.printf("\n %s, %s, %s, %s, %s, %s", refNum, book_id, issuedTo_id, book_title, issued_date, return_request_date);

				list.add(book) ;
			}
		}
		catch(Exception e)
		{
			System.out.println("\nExceptions in Controller.getAllReservedBooks() ->\n" + e) ;
		}

		return list ;
	}



	// get all the books that people wish to return
	public ObservableList<Book> getAllBooksToBeReturned()
	{
		String refNum, book_id, book_title, author, isbn, issued_date, return_request_date ;

		ObservableList<Book> list = FXCollections.observableArrayList() ;

		String query = "SELECT * FROM tblBorrowedBooks WHERE ReturnSentOn IS NOT NULL ORDER BY ReturnSentOn DESC;" ;

		try
		{
			Book book ;
			Statement stmnt = conn.createStatement() ;

			ResultSet resultSet = stmnt.executeQuery(query) ;

			while(resultSet.next() == true)
			{
				refNum = resultSet.getString("RefNum") ;
				book_id = resultSet.getString("BookID") ;
				book_title = resultSet.getString("Title") ;
				issued_date = resultSet.getString("IssuedOn") ;
				return_request_date = resultSet.getString("ReturnSentOn") ;

				author = getFromBooks("Author", book_id) ;
				isbn = getFromBooks("ISBN", book_id) ;

				book = new Book(book_id, book_title, author, isbn) ;
				book.setIssuedToID(resultSet.getString("PersonIssuedTo_ID")) ;		// set the ID of the person who wishes to return the book
				book.setRefNum(refNum);
				book.setIssuedDate(issued_date) ;
				book.setReturnSentOn(return_request_date) ;				

				list.add(book) ;
			}
		}
		catch(Exception e)
		{
			System.out.println("\nExceptions in Controller.getAllReservedBooks() ->\n" + e) ;
		}

		return list ;
	}


	// get all the books' details
	public ObservableList<Book> getBookList()
	{
		String bookId, title, author, isbn ;
		int copies ;
		ObservableList<Book> list = FXCollections.observableArrayList() ;

		try
		{
			Book book ;
			String query = "SELECT * FROM tblBooks ORDER BY Title ;" ;

			Statement stmnt = conn.createStatement() ;

			ResultSet resultSet = stmnt.executeQuery(query) ;

			while(resultSet.next() == true)
			{
				// get all the details in the columns in the table
				bookId = resultSet.getString("BookID") ;
				title = resultSet.getString("Title") ;
				author = resultSet.getString("Author") ;
				copies = resultSet.getInt("Copies") ;
				isbn = resultSet.getString("ISBN") ;

				// create a Book object using the retrieved data
				book = new Book(bookId, title, author, isbn) ;
				book.setCopies(copies) ;

				list.add(book) ;
			}
		}
		catch(Exception e)
		{
			System.out.println("\n Exceptions in Controller.getBookList() -> \n" + e) ;
		}

		return list ;
	}


	// get the details of a specific book/s
	public ObservableList<Book> getBookList(String lookingFor, String field)	// receive the Value and the name of the Field in the table
	{
		String bookId, title, author, isbn ;
		int copies ;
		ObservableList<Book> list = FXCollections.observableArrayList() ;

		String query = "SELECT * FROM tblBooks WHERE " + field + " LIKE \"%" + lookingFor.toUpperCase() + "%\" ;" ;
		
		try
		{
			Book book ;
			Statement stmnt = conn.createStatement() ;

			ResultSet resultSet = stmnt.executeQuery(query) ;

			while(resultSet.next() == true)
			{
				// get all the details in the columns in the table
				bookId = resultSet.getString("BookID") ;
				title = resultSet.getString("Title") ;
				author = resultSet.getString("Author") ;
				copies = resultSet.getInt("Copies") ;
				isbn = resultSet.getString("ISBN") ;

				// create a Book object using the retrieved data
				book = new Book(bookId, title, author, isbn) ;
				book.setCopies(copies) ;

				list.add(book) ;
			}
		}
		catch(Exception e)
		{
			System.out.println("\n Exceptions in Controller.getBookList() -> \n" + e) ;
		}

		return list ;
	}


	// get data from a column in tblBooks
	private String getFromBooks(String fieldName, String bk_id)
	{
		String query = "SELECT " + fieldName + " FROM tblBooks WHERE BookID = '" + bk_id + "' ;" ;

		try
		{
			Statement stmnt = conn.createStatement() ;

			ResultSet resultSet = stmnt.executeQuery(query) ;

			if(resultSet.next())	return resultSet.getString(fieldName) ;
		}
		catch(Exception e)
		{
			System.out.println("\nExceptions in Controller.getFromBooks() -> \n" + e) ;
		}

		return null ;
	}


	private String generateRefNum()
	{
		// get all the refNums in the table
		ArrayList<String> refNum_list = new ArrayList<String>() ;
		try
		{
			Statement stmnt = conn.createStatement() ;
			ResultSet resultSet = stmnt.executeQuery("SELECT RefNum FROM tblBorrowedBooks;") ;
			while(resultSet.next())
			{
				refNum_list.add(resultSet.getString("RefNum")) ;
			}

			stmnt.close() ;
			resultSet.close() ;
		}
		catch(Exception e)
		{
			System.out.println("\nExceptions in Controller.generateRefNum() -> \n" + e) ;
		}

		String tempRefNum = "" ;
		do
		{
			int digit1 = (int)(Math.random()*10) ;
			int digit2 = (int)(Math.random()*10) ;
			int digit3 = (int)(Math.random()*10) ;
			
			String ltr = getRandomLtr() ;

			tempRefNum = ltr + (Integer.toString(digit1)) + (Integer.toString(digit2)) + (Integer.toString(digit3)) ;
		}
		while(refNum_list.contains(tempRefNum)) ;

		return tempRefNum ;
	}


	private String getPassword()
	{
		File file ;
		Scanner scan ;

		try
		{
			file = new File(/* pass the path of the text file that's got the password to the MySql server */) ;
			scan = new Scanner(file) ;

			return scan.nextLine() ;
		}
		catch(Exception e)
		{
			System.out.println("Exceptions in Controller.getPassword() -> \n" + e) ;
		}

		return null ;
	}


	private String getRandomLtr()
	{
		int num = (int)(Math.random()*27) ;
		String ltr = "" ;

		switch(num)
		{
			case 1 : ltr = "A" ;
					 break ;
			case 2 : ltr = "B" ;
					 break ;
			case 3 : ltr = "C" ;
					 break ;
			case 4 : ltr = "D" ;
					 break ;
			case 5 : ltr = "E" ;
					 break ;
			case 6 : ltr = "F" ;
					 break ;
			case 7 : ltr = "G" ;
					 break ;
			case 8 : ltr = "H" ;
					 break ;
			case 9 : ltr = "I" ;
					 break ;
			case 10 : ltr = "J" ;
					 break ;
			case 11 : ltr = "K" ;
					 break ;
			case 12 : ltr = "L" ;
					 break ;
			case 13 : ltr = "M" ;
					 break ;
			case 14 : ltr = "N" ;
					 break ;
			case 15 : ltr = "O" ;
					 break ;
			case 16 : ltr = "P" ;
					 break ;
			case 17 : ltr = "Q" ;
					 break ;
			case 18 : ltr = "R" ;
					 break ;
			case 19 : ltr = "S" ;
					 break ;
			case 20 : ltr = "T" ;
					 break ;
			case 21 : ltr = "U" ;
					 break ;
			case 22 : ltr = "V" ;
					 break ;
			case 23 : ltr = "W" ;
					 break ;
			case 24 : ltr = "X" ;
					 break ;
			case 25 : ltr = "Y" ;
					 break ;
			case 26 : ltr = "Z" ;
					 break ;
		}

		return ltr ;
	}
}