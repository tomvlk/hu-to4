package nl.atd.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import nl.atd.model.Parkeerplek;

public class ParkeerplekDAO extends BaseDAO {
	
	/**
	 * Get plekken, prepared statement
	 * @param ps Prepared statement
	 * @return array met plekken
	 */
	private ArrayList<Parkeerplek> getPlekken(PreparedStatement ps) {
		ArrayList<Parkeerplek> plekken = new ArrayList<Parkeerplek>();
		try{
			ResultSet set = ps.executeQuery();
			
			while(set.next()) {
				
			}
			
			ps.closeOnCompletion();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
		return plekken;
	}
	
	/**
	 * Get plekken met query (object mapping)
	 * @param query
	 * @return array
	 */
	private ArrayList<Parkeerplek> getPlekken(String query) {
		ArrayList<Parkeerplek> plekken = new ArrayList<Parkeerplek>();
		
		try{
			PreparedStatement st = this.getPreparedStatement(query);
			plekken = this.getPlekken(st);
			
			st.closeOnCompletion();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return plekken;
	}
	
	/**
	 * Get alle parkeerplekken
	 * @return plekken in array
	 */
	public ArrayList<Parkeerplek> getAllePlekken() {
		return this.getPlekken("SELECT * FROM parkeerplek");
	}
}
