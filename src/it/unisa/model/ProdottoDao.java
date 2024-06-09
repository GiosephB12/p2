package it.unisa.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class ProdottoDao implements ProdottoDaoInterfaccia{

	private static DataSource ds;

	static {
		try {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");

			ds = (DataSource) envCtx.lookup("jdbc/storage");

		} catch (NamingException e) {
			System.out.println("Error:" + e.getMessage());
		}
	}
	
	private static final String TABLE_NAME = "prodotto";

	public synchronized void doSave(ProdottoBean product) throws SQLException {

	    try (Connection connection = ds.getConnection();
	         PreparedStatement preparedStatement = connection.prepareStatement(
	                 "INSERT INTO " + ProdottoDao.TABLE_NAME
	                         + " (NOME, PIATTAFORMA, DESCRIZIONE, PREZZO, QUANTITA, GENERE, DATA_USCITA, IN_VENDITA, IVA, IMMAGINE, DESCRIZIONE_DETTAGLIATA) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

	        preparedStatement.setString(1, product.getNome());
	        preparedStatement.setString(2, product.getPiattaforma());
	        preparedStatement.setString(3, product.getDescrizione());
	        preparedStatement.setDouble(4, product.getPrezzo());
	        preparedStatement.setInt(5, product.getQuantita());
	        preparedStatement.setString(6, product.getGenere());
	        preparedStatement.setString(7, product.getDataUscita());
	        preparedStatement.setBoolean(8, product.isInVendita());
	        preparedStatement.setString(9, product.getIva());
	        preparedStatement.setString(10, product.getImmagine());
	        preparedStatement.setString(11, product.getDescrizioneDettagliata());

	        preparedStatement.executeUpdate();
	    }
	}

	public synchronized ProdottoBean doRetrieveByKey(int idProdotto) throws SQLException {
	    ProdottoBean bean = null;

	    String selectSQL = "SELECT * FROM " + ProdottoDao.TABLE_NAME + " WHERE ID_PRODOTTO = ?";

	    try (Connection connection = ds.getConnection();
	         PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {

	        preparedStatement.setInt(1, idProdotto);

	        try (ResultSet rs = preparedStatement.executeQuery()) {
	            if (rs.next()) {
	                bean = new ProdottoBean();
	                bean.setIdProdotto(rs.getInt("ID_PRODOTTO"));
	                bean.setNome(rs.getString("NOME"));
	                bean.setDescrizione(rs.getString("DESCRIZIONE"));
	                bean.setPrezzo(rs.getDouble("PREZZO"));
	                bean.setQuantita(rs.getInt("QUANTITA"));
	                bean.setPiattaforma(rs.getString("PIATTAFORMA"));
	                bean.setIva(rs.getString("IVA"));
	                bean.setDataUscita(rs.getString("DATA_USCITA"));
	                bean.setInVendita(rs.getBoolean("IN_VENDITA"));
	                bean.setImmagine(rs.getString("IMMAGINE"));
	                bean.setGenere(rs.getString("GENERE"));
	                bean.setDescrizioneDettagliata(rs.getString("DESCRIZIONE_DETTAGLIATA"));
	            }
	        }
	    }

	    return bean;
	}


	public synchronized boolean doDelete(int idProdotto) throws SQLException {
	    int result = 0;

	    String deleteSQL = "DELETE FROM " + ProdottoDao.TABLE_NAME + " WHERE ID_PRODOTTO = ?";

	    try (Connection connection = ds.getConnection();
	         PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {

	        preparedStatement.setInt(1, idProdotto);

	        result = preparedStatement.executeUpdate();
	    }

	    return (result != 0);
	}


	public synchronized ArrayList<ProdottoBean> doRetrieveAll(String order) throws SQLException {
	    ArrayList<ProdottoBean> products = new ArrayList<>();

	    String selectSQL = "SELECT * FROM " + ProdottoDao.TABLE_NAME;

	    if (order != null && !order.equals("")) {
	        // Controllo aggiunto per evitare SQL injection
	        if (order.matches("[a-zA-Z_]+")) {
	            selectSQL += " ORDER BY " + order;
	        } else {
	            // In caso di ordine non valido, si può gestire in modo diverso, ad esempio ignorare o impostare un ordine predefinito
	            // Qui ho scelto di non aggiungere l'ordine per evitare possibili attacchi di SQL injection
	        }
	    }

	    try (Connection connection = ds.getConnection();
	         PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {

	        ResultSet rs = preparedStatement.executeQuery();

	        while (rs.next()) {
	            ProdottoBean bean = new ProdottoBean();

	            bean.setIdProdotto(rs.getInt("ID_PRODOTTO"));
	            bean.setNome(rs.getString("NOME"));
	            bean.setDescrizione(rs.getString("DESCRIZIONE"));
	            bean.setPrezzo(rs.getDouble("PREZZO"));
	            bean.setQuantita(rs.getInt("QUANTITA"));
	            bean.setPiattaforma(rs.getString("PIATTAFORMA"));
	            bean.setIva(rs.getString("IVA"));
	            bean.setDataUscita(rs.getString("DATA_USCITA"));
	            bean.setInVendita(rs.getBoolean("IN_VENDITA"));
	            bean.setImmagine(rs.getString("IMMAGINE"));
	            bean.setGenere(rs.getString("GENERE"));
	            bean.setDescrizioneDettagliata(rs.getString("DESCRIZIONE_DETTAGLIATA"));

	            products.add(bean);
	        }
	    }

	    return products;
	}

    
	public synchronized void doUpdateQnt(int id, int qnt) throws SQLException {
	    String updateSQL = "UPDATE " + ProdottoDao.TABLE_NAME
	            + " SET QUANTITA = ? "
	            + " WHERE ID_PRODOTTO = ? ";

	    try (Connection connection = ds.getConnection();
	         PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {

	        preparedStatement.setInt(1, qnt);
	        preparedStatement.setInt(2, id);

	        preparedStatement.executeUpdate();
	    }
	}
	
	public synchronized void doUpdate(ProdottoBean product) throws SQLException {
	    String updateSQL = "UPDATE " + ProdottoDao.TABLE_NAME
	            + " SET NOME = ? , QUANTITA = ? , PIATTAFORMA = ?, DESCRIZIONE = ?, PREZZO = ?, GENERE = ?, DATA_USCITA = ?, IN_VENDITA = ?, IVA = ?, IMMAGINE = ?, DESCRIZIONE_DETTAGLIATA = ?"
	            + " WHERE ID_PRODOTTO = ? ";

	    try (Connection connection = ds.getConnection();
	         PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {

	        preparedStatement.setString(1, product.getNome());
	        preparedStatement.setInt(2, product.getQuantita());
	        preparedStatement.setString(3, product.getPiattaforma());
	        preparedStatement.setString(4, product.getDescrizione());
	        preparedStatement.setDouble(5, product.getPrezzo());
	        preparedStatement.setString(6,product.getGenere());
	        preparedStatement.setString(7, product.getDataUscita());
	        preparedStatement.setBoolean(8, product.isInVendita());
	        preparedStatement.setString(9, product.getIva());
	        preparedStatement.setString(10, product.getImmagine());
	        preparedStatement.setString(11, product.getDescrizioneDettagliata());
	        preparedStatement.setInt(12, product.getIdProdotto());

	        preparedStatement.executeUpdate();
	    }
	}

	
	public synchronized ArrayList<ProdottoBean> doRetrieveByPiattaforma(String piattaforma) throws SQLException {
	    ArrayList<ProdottoBean> prodotti = new ArrayList<ProdottoBean>();
	    String selectSQL = "SELECT * FROM " + ProdottoDao.TABLE_NAME + " WHERE PIATTAFORMA = ?";

	    try (Connection connection = ds.getConnection();
	         PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {

	        preparedStatement.setString(1, piattaforma);
	        try (ResultSet rs = preparedStatement.executeQuery()) {
	            while (rs.next()) {
	                ProdottoBean bean = new ProdottoBean();
	                bean.setIdProdotto(rs.getInt("ID_PRODOTTO"));
	                bean.setNome(rs.getString("NOME"));
	                bean.setDescrizione(rs.getString("DESCRIZIONE"));
	                bean.setPrezzo(rs.getDouble("PREZZO"));
	                bean.setQuantita(rs.getInt("QUANTITA"));
	                bean.setPiattaforma(rs.getString("PIATTAFORMA"));
	                bean.setIva(rs.getString("IVA"));
	                bean.setDataUscita(rs.getString("DATA_USCITA"));
	                bean.setInVendita(rs.getBoolean("IN_VENDITA"));
	                bean.setImmagine(rs.getString("IMMAGINE"));
	                bean.setGenere(rs.getString("GENERE"));
	                bean.setDescrizioneDettagliata(rs.getString("DESCRIZIONE_DETTAGLIATA"));

	                prodotti.add(bean);
	            }
	        }
	    }
	    return prodotti;
	}	

}