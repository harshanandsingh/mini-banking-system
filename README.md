# mini-banking-system

1. Project Title & Introduction - 
Mini Banking System – A simple console-based banking application built with Java and MySQL. It supports user registration, login, deposits, withdrawals, transfers, transaction history filtering, password change, and soft account deletion.

2. Features -

    User Registration & Login (with BCrypt password hashing)
    
    Deposit & Withdraw money
    
    Transfer between accounts
    
    View Transaction History (with filters for date & type)
    
    Change Password (secure check with BCrypt)
    
    Soft Delete Account (user + account marked as deleted)
    
    Session management for logged-in users

3. Project Flow Explanation -

a. Registration Flow:
  
      User enters name, email, password.
      System → calls UserDAO.registerUser(newUser).
      UserDAO → establishes DB connection, starts transaction.
      Insert into users table.
      On success, create account with balance = 0.
      Commit if successful, rollback if failure.
      Return status to user.
      Outcome: Account created, user registered.
      
b. Login Flow:

      Start – Input Credentials
        The user enters their email and password.
      Authenticate User
        The system searches the database for a matching email that is not marked as deleted.
      Database Response
        If a user record is found, the system retrieves the stored (hashed) password.
        The entered password is compared with the stored password securely.
      Password Verification
        If the password matches → A user session is created, and the user is logged in.
        if the password does not match or the user is not found → The login fails.
      System Decision
        On success → A message “Login successful” is shown, and the user menu/options are displayed.
        On failure → A message “Invalid email or password” is shown
