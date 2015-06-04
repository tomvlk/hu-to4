package nl.atd.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

import com.sun.corba.se.spi.orbutil.fsm.Guard.Result;

import nl.atd.helper.DatabaseHelper;
import nl.atd.model.Auto;
import nl.atd.model.Klant;
import nl.atd.model.Klus;
import nl.atd.model.Monteur;
import nl.atd.service.ServiceProvider;

public class KlusDAO {

	/**
	 * Get autos, convert to list met klussen
	 * 
	 * @param query
	 * @return array met klussen
	 */
	private ArrayList<Klus> getKlussen(String query) {
		ArrayList<Klus> klussen = new ArrayList<Klus>();

		try {
			Connection connection = DatabaseHelper.getDatabaseConnection();
			Statement statement = connection.createStatement();

			ResultSet set = statement.executeQuery(query);

			while (set.next()) {

				Klant klant = null;
				Auto auto = null;
				Monteur monteur = null;

				if (set.getString("klant") != null) {
					klant = ServiceProvider.getKlantService()
							.getKlantByGebruikersnaam(set.getString("klant"));
				}

				if (set.getInt("auto") > 0) {
					auto = ServiceProvider.getAutoService().getAuto(
							set.getInt("auto"));
				}

				if (set.getString("monteur") != null) {
					monteur = ServiceProvider.getMonteurService()
							.getMonteurByGebruikersnaam(
									set.getString("monteur"));
				}

				Klus klus = new Klus(klant, auto);
				klus.setMonteur(monteur);

				// Calendar
				Calendar datum = Calendar.getInstance();
				try {
					datum.setTimeInMillis(set.getTimestamp("datum").getTime());
				} catch (SQLException se) {
					datum = null;
				}

				klus.setOnderdelen(ServiceProvider.getOnderdeelService()
						.getAlleOnderdelenVanKlus(set.getInt("idklus")));

				klus.setCalendar(datum);
				klus.setKlaar(set.getBoolean("klaar"));
				klus.setOmschrijving(set.getString("omschrijving"));
				klus.setType(set.getString("type"));
				klus.setUren(set.getInt("uren"));

				klussen.add(klus);
			}

			connection.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return klussen;
	}

	/**
	 * Get alle klussen inclusief klant, auto e.d.
	 * 
	 * @return array met klussen
	 */
	public ArrayList<Klus> getAlleKlussen() {
		return this.getKlussen("SELECT * FROM klus");
	}

	// , UNIX_TIMESTAMP(klus.datum) as datumtimestamp

	public Klus getKlusOpId(int id) {
		ArrayList<Klus> klussen = this
				.getKlussen("SELECT * FROM klus WHERE idklus = " + id);
		return klussen.size() >= 1 ? klussen.get(0) : null;
	}

	// , UNIX_TIMESTAMP(klus.datum) as datumtimestamp

	public boolean addKlus(Klus klus, Auto auto, Monteur monteur, Klant klant) {
		try {
			Connection connection = DatabaseHelper.getDatabaseConnection();
			PreparedStatement st = connection
					.prepareCall("INSERT INTO klus (type, klaar, datum, omschrijving, monteur, klant, auto, uren) VALUES(?, 0, ?, ?, ?, ?, ?, ?);");

			st.setString(1, klus.getType());
			st.setTimestamp(2, new Timestamp(klus.getCalendar()
					.getTimeInMillis()));
			st.setString(3, klus.getOmschrijving());
			if (monteur == null) {
				st.setObject(4, null);
			} else {
				st.setString(4, monteur.getGebruikersnaam());
			}
			st.setString(5, klant.getGebruikersnaam());
			st.setInt(
					6,
					ServiceProvider.getAutoService().getAutoIdOpKenteken(
							auto.getKenteken()));
			st.setInt(7, klus.getUren());

			st.execute();

			connection.close();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private int getKlusIdOpQuery(String query) {
		int nr = 0;

		try {
			Connection connection = DatabaseHelper.getDatabaseConnection();
			Statement statement = connection.createStatement();

			ResultSet set = statement.executeQuery(query);

			while (set.next()) {
				nr = set.getInt("idklus");
			}

			connection.close();

		} catch (Exception e) {
		}

		return nr;
	}

	public int getKlusId(Calendar datum, Klant klant, Auto auto) {
		return this
				.getKlusIdOpQuery("SELECT idklus FROM klus WHERE datum = FROM_UNIXTIME("
						+ (datum.getTimeInMillis() / 1000)
						+ ")"
						+ " AND klant LIKE '"
						+ klant.getGebruikersnaam()
						+ "'"
						+ " AND auto = "
						+ ServiceProvider.getAutoService().getAutoIdOpKenteken(
								auto.getKenteken()));

	}

	public ArrayList<Klus> getAlleKlussenTussen(Calendar start, Calendar end) {
		return this
				.getKlussen("SELECT *, UNIX_TIMESTAMP(klus.datum) as datumtimestamp FROM klus "
						+ "WHERE datum BETWEEN FROM_UNIXTIME("
						+ (start.getTimeInMillis() / 1000)
						+ ") AND FROM_UNIXTIME("
						+ (end.getTimeInMillis() / 1000) + ");");
	}

	public boolean delete(Klus klus) {
		boolean b = false;
		int klusID = getKlusId(klus.getCalendar(), klus.getKlant(),
				klus.getAuto());

		if (klusID != 0) {
			String query = "DELETE FROM `atd`.`klus` WHERE `klus`.`idklus` = "
					+ klusID;

			try {
				Connection connection = DatabaseHelper.getDatabaseConnection();
				Statement statement = connection.createStatement();

				if (statement.executeUpdate(query) == 1) {
					b = true;
				}
				connection.close();
			} catch (Exception e) {
			}
		}
		return b;
	}

	public void deleteAlles() {
		try {
			Connection connection = DatabaseHelper.getDatabaseConnection();
			Statement statement = connection.createStatement();
			statement.executeUpdate("SET FOREIGN_KEY_CHECKS=0; "
					+ "TRUNCATE klus; " + "SET FOREIGN_KEY_CHECKS=1;");

			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
