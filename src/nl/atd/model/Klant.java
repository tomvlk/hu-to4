package nl.atd.model;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Klant object
 * 
 * @author ATD Developers
 *
 */
public class Klant extends Persoon {
	private String email;
	private Calendar laatsteBezoek;

	private ArrayList<Auto> autos;

	public Klant(String nm) {
		super(nm);

		this.email = "";
		this.laatsteBezoek = Calendar.getInstance();
		this.autos = new ArrayList<Auto>();
	}

	/**
	 * Get alle auto's in arraylist
	 * 
	 * @return ArrayList met auto's
	 */
	public ArrayList<Auto> getAutos() {
		return this.autos;
	}

	/**
	 * Voeg auto toe aan auto's
	 * 
	 * @param a
	 *            Auto om toe te voegen
	 */
	public void voegAutoToe(Auto a) {
		this.autos.add(a);
	}

	/**
	 * Get e-mail adres van klant
	 * 
	 * @return E-mail adres
	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * Get laatste bezoek
	 * 
	 * @return laatste bezoek klant
	 */
	public Calendar getLaatsteBezoek() {
		return this.laatsteBezoek;
	}

	/**
	 * Set laatste bezoek van klant
	 * 
	 * @param lb
	 *            Laatste bezoek
	 */
	public void setLaatsteBezoek(Calendar lb) {
		this.laatsteBezoek = lb;
	}

	/**
	 * Set email
	 * 
	 * @param email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Override van equals 
	 * 
	 * @param ander 
	 */
	public boolean equals(Object ander) {
		boolean b;

		if (ander instanceof Klant) {
			b = true;
		} else {
			b = false;
		}

		b = b && super.equals(ander) && (this.email.equals(((Klant)ander).getEmail()));
		// Omdat in de tests de database moet worden geleegd bij tearDown()
		//b = b && (this.autos.equals(((Klant)ander).getAutos()));
		
		
		// Calendar vergelijken, DAY.OF.YEAR - YEAR - HOURS : MINUTES
		
	//	b = b && ((this.laatsteBezoek).get(Calendar.DAY_OF_YEAR) == ((Klant)ander).getLaatsteBezoek().get(Calendar.DAY_OF_YEAR));
		b = b && ((this.laatsteBezoek).get(Calendar.YEAR) == ((Klant)ander).getLaatsteBezoek().get(Calendar.YEAR));
		b = b && ((this.laatsteBezoek).get(Calendar.HOUR_OF_DAY) == ((Klant)ander).getLaatsteBezoek().get(Calendar.HOUR_OF_DAY));
		b = b && ((this.laatsteBezoek).get(Calendar.MINUTE) == ((Klant)ander).getLaatsteBezoek().get(Calendar.MINUTE));
		
		return b;
	}

}
