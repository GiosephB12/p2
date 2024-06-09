package it.unisa.model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class UserDao implements UserDaoInterfaccia {

	private static DataSource ds;

	static {
		try {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");

			ds = (DataSource) envCtx.lookup("jdbc/storage");

		} 
		catch (NamingException e) {
			System.out.println("Error:" + e.getMessage());
		}
	}
	
	private static final String TABLE_NAME = "cliente";
	
	
	public synchronized void doSave(UserBean user) throws SQLException {
	    String insertSQL = "INSERT INTO " + UserDao.TABLE_NAME 
	                    + " (NOME, COGNOME, USERNAME, PWD, EMAIL, DATA_NASCITA, CARTA_CREDITO, INDIRIZZO, CAP, AMMINISTRATORE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";

	    try (Connection connection = ds.getConnection();
	         PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {

	        preparedStatement.setString(1, user.getNome());
	        preparedStatement.setString(2, user.getCognome());
	        preparedStatement.setString(3, user.getUsername());
	        preparedStatement.setString(4, user.getPassword());
	        preparedStatement.setString(5, user.getEmail());
	        preparedStatement.setDate(6, new java.sql.Date(user.getDataDiNascita().getTime()));
	        preparedStatement.setString(7, user.getCartaDiCredito());
	        preparedStatement.setString(8, user.getIndirizzo());
	        preparedStatement.setString(9, user.getCap());
	        preparedStatement.setBoolean(10, user.isAmministratore());

	        preparedStatement.executeUpdate();
	    }
	}


	public synchronized UserBean doRetrieve(String username, String password) throws SQLException {
	    UserBean user = new UserBean();
	    String searchQuery = "SELECT * FROM " + UserDao.TABLE_NAME + " WHERE username = ? AND pwd = ?";
	    
	    try (Connection connection = ds.getConnection();
	         PreparedStatement preparedStatement = connection.prepareStatement(searchQuery)) {
	        
	        preparedStatement.setString(1, username);
	        preparedStatement.setString(2, password);
	        
	        try (ResultSet rs = preparedStatement.executeQuery()) {
	            if (rs.next()) {
	                user.setUsername(rs.getString("username"));
	                user.setPassword(rs.getString("pwd"));
	                user.setEmail(rs.getString("email"));
	                user.setNome(rs.getString("nome"));
	                user.setCognome(rs.getString("cognome"));
	                user.setDataDiNascita(rs.getDate("data_nascita"));
	                user.setCartaDiCredito(rs.getString("carta_credito"));
	                user.setIndirizzo(rs.getString("indirizzo"));
	                user.setCap(rs.getString("cap"));
	                user.setAmministratore(rs.getBoolean("amministratore"));
	                user.setValid(true);
	            } else {
	                user.setValid(false);
	            }
	        }
	    } catch (SQLException ex) {
	        System.out.println("Log In failed: An Exception has occurred! " + ex);
	    }

	    return user;
	}
	

	public synchronized ArrayList<UserBean> doRetrieveAll(String order) throws SQLException {
	    ArrayList<UserBean> users = new ArrayList<>();
	    String selectSQL = "SELECT * FROM " + UserDao.TABLE_NAME;

	    if (order != null && !order.equals("")) {
	        selectSQL += " ORDER BY " + order;
	    }

	    try (Connection connection = ds.getConnection();
	         PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
	         ResultSet rs = preparedStatement.executeQuery()) {

	        while (rs.next()) {
	            UserBean user = new UserBean();
	            user.setUsername(rs.getString("username"));
	            user.setPassword(rs.getString("pwd"));
	            user.setEmail(rs.getString("email"));
	            user.setNome(rs.getString("nome"));
	            user.setCognome(rs.getString("cognome"));
	            user.setDataDiNascita(rs.getDate("data_nascita"));
	            user.setCartaDiCredito(rs.getString("carta_credito"));
	            user.setIndirizzo(rs.getString("indirizzo"));
	            user.setCap(rs.getString("cap"));
	            user.setAmministratore(rs.getBoolean("amministratore"));
	            user.setValid(true);
	            users.add(user);
	        }
	    } catch (SQLException ex) {
	        System.out.println("An error occurred while retrieving users: " + ex.getMessage());
	        throw ex; // Rilancia l'eccezione per gestirla al livello superiore
	    }

	    return users;
	}


	public synchronized void doUpdateSpedizione(String email, String indirizzo, String cap) throws SQLException {
	    String updateSQL = "UPDATE " + UserDao.TABLE_NAME
	            + " SET INDIRIZZO = ?, CAP = ?"
	            + " WHERE EMAIL = ? ";

	    try (Connection connection = ds.getConnection();
	         PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
	        
	        preparedStatement.setString(1, indirizzo);
	        preparedStatement.setString(2, cap);
	        preparedStatement.setString(3, email);
	        preparedStatement.executeUpdate();
	    } catch (SQLException ex) {
	        System.out.println("An error occurred while updating shipping information: " + ex.getMessage());
	        throw ex; // Rilancia l'eccezione per gestirla al livello superiore
	    }
	}

	public synchronized void doUpdatePagamento(String email, String carta) throws SQLException {
	    String updateSQL = "UPDATE " + UserDao.TABLE_NAME
	            + " SET CARTA_CREDITO = ?"
	            + " WHERE EMAIL = ? ";
	    
	    try (Connection connection = ds.getConnection();
	         PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
	        
	        preparedStatement.setString(1, carta);
	        preparedStatement.setString(2, email);
	        preparedStatement.executeUpdate();
	    } catch (SQLException ex) {
	        System.out.println("An error occurred while updating payment information: " + ex.getMessage());
	        throw ex; // Rilancia l'eccezione per gestirla al livello superiore
	    }
	}
 }
