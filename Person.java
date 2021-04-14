import javafx.collections.* ;

public class Person
{
	private String id, firstName, lastName, username, password ;
	private String emailAddress, phone, nic ;
	private String dateOfBirth, address ;
	private int numOfBorrowedBooks ;

	private ObservableList<Book> borrowedBooks_list = FXCollections.observableArrayList() ; // stores the Books the person has borrowed


	public Person(String id, String f_name, String l_name, String username, String pswrd)
	{
		this.id = id ;
		this.firstName = f_name ;
		this.lastName = l_name ;
		this.username = username ;
		this.password = pswrd ;
	}


	public String getId()
	{
		return this.id ;
	}

	public String getFirstName()
	{
		return this.firstName ;
	}

	public String getLastName()
	{
		return this.lastName ;
	}

	public String getUsername()
	{
		return this.username ;
	}

	public String getPassword()
	{
		return this.password ;
	}

	public String getEmail()
	{
		return this.emailAddress ;
	}

	public String getPhone()
	{
		return this.phone ;
	}

	public String getDateOfBirth()
	{
		return this.dateOfBirth ;
	}

	public Integer getNumOfBorrowedBooks()
	{
		return this.numOfBorrowedBooks ;
	}

	public String getAddress()
	{
		return this.address ;
	}

	public ObservableList<Book> getAllBorrowedBooks()
	{
		return this.borrowedBooks_list ;
	}

	public String getNic()
	{
		return this.nic ;
	}

	public Boolean hasBorrowed(Book book)	// checks if the user has already borrowed a specific book
	{
		for(int index = 0 ; index < borrowedBooks_list.size() ; index++)
		{
			if(borrowedBooks_list.get(index).getBookID().equals(book.getBookID()))
				return true ;
		}

		return false ;
	}


	public void setId(String id)
	{
		this.id = id ;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName ;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName ;
	}

	public void setUsername(String username)
	{
		this.username = username ;
	}

	public void setPassword(String password)
	{
		this.password = password ;
	}

	public void setEmail(String email)
	{
		this.emailAddress = email ;
	}

	public void setPhone(String phone)
	{
		this.phone = phone ;
	}

	public void setDateOfBirth(String dateOfBirth)
	{
		this.dateOfBirth = dateOfBirth ;
	}

	public void setNumOfBorrowedBooks(int numOfBorrowedBooks)
	{
		this.numOfBorrowedBooks = numOfBorrowedBooks ;
	}

	public void setAddress(String a)
	{
		this.address = a ;
	}


	public void setAllBorrowedBooks(ObservableList<Book> list)
	{
		this.borrowedBooks_list = list ;
	}

	public void setNic(String n)
	{
		this.nic = n ;
	}

	public void addToBorrowedBooks(Book book)
	{
		this.borrowedBooks_list.add(book) ;
	}
}