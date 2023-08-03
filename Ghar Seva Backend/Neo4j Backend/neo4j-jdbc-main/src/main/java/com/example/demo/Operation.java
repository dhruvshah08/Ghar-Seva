package com.example.demo;



import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
public class Operation {

	
	@GetMapping("/recommendation")
	public List<String> getRecommendation(@RequestParam(defaultValue = "") String category) throws SQLException {
		// Connect
		Driver driver = new org.neo4j.jdbc.Driver();
		DriverManager.registerDriver(driver);
		
		
        Connection con = null;
        List<String> ans = new ArrayList<String>();
		try {
			con = DriverManager.getConnection("jdbc:neo4j:bolt://localhost","neo4j","123456");
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			System.out.println(e1);
			//e1.printStackTrace();
		}
		
		//switch
		switch(category) {
			case "acrepair" : category = "AC_Repair";break;
			case "homepainting" : category = "Home_Painting";break;
			case "electronicgoods" : category = "Electronics";break;
			case "carpenters" : category = "Carpenters";break;
			case "electricians" : category = "Electricians";break;
			case "sanitization" : category = "Sanitizing";break;
			case "spa" : category = "Spa";break;
			case "salon" : category = "Salon";break;
			case "pestcontrol" : category = "Pest_Control";break;
		}

        // Querying
        String query = "MATCH (node : "+category+") return node";
        try {
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            String a = null;
            while (rs.next()) {
            	a = rs.getString("node");
            	System.out.println(a);
            }
            HashMap<String,Object> map = new Gson().fromJson(a, new TypeToken<HashMap<String, Object>>(){}.getType());
            if(map==null) {
            	ans.add("Electricians");
            	ans.add("Carpenters");
            	ans.add("Salon");
            	return ans;
            }
            map.remove("_id");
            map.remove("_labels");
            map.remove("name");
        	 List<Map.Entry<String, Object> > list =
                     new LinkedList<Map.Entry<String, Object> >(map.entrySet());
       
              // Sort the list
              Collections.sort(list, new Comparator<Map.Entry<String, Object> >() {
                  public int compare(Map.Entry<String, Object> o1,
                                     Map.Entry<String, Object> o2)
                  {
                      return ( (Double)o2.getValue()).compareTo((Double) o1.getValue());
                  }
              });
               
              // put data from sorted list to hashmap
              HashMap<String, Object> temp = new LinkedHashMap<String, Object>();
              int count = 0;
              for (Map.Entry<String, Object> aa : list) {
             	 if(count < 3) {
             		 switch(aa.getKey()) {
             		 	
 	                  	case "after_sanitizing" : ans.add("Sanitizing");break;
 	                  	case "after_spa" : ans.add("Spa");break;
 	                  	case "after_electronics" : ans.add("Electronics");break;
 	                  	case "after_home_painting" : ans.add("Home Painting");break;
 	                  	case "after_carpenter" : ans.add("Carpenters");break;
 	                  	case "after_ac_repair" : ans.add("AC Repair");break;
 	                  	case "after_pest_control" : ans.add("Pest Control");break;
 	                  	case "after_electrician" : ans.add("Electricians");break;
 	                  	case "after_salon" : ans.add("Salon");break;
 	 	     			
 	                  }
             		 count ++;
             	 }
             	 else {
             		 break;
             	 }
                     
            }
        } catch (Exception e) { System.out.print(e); }
        try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.print(e);
		}
		return ans;
		
	}
	
	@GetMapping("/add")
	public int add(@RequestParam(defaultValue = "") String category
			,@RequestParam(defaultValue = "") String currCategory){
		
		
		Driver driver = null;
		try {
			driver = new org.neo4j.jdbc.Driver();
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			DriverManager.registerDriver(driver);
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		
        Connection con = null;
        List<String> ans = new ArrayList<String>();
		try {
			con = DriverManager.getConnection("jdbc:neo4j:bolt://localhost","neo4j","123456");
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			System.out.println(e1);
		}
		
		//switch
		switch(category) {
			case "acrepair" : category = "AC_Repair";break;
			case "homepainting" : category = "Home_Painting";break;
			case "electronicgoods" : category = "Electronics";break;
			case "carpenters" : category = "Carpenters";break;
			case "electricians" : category = "Electricians";break;
			case "sanitization" : category = "Sanitizing";break;
			case "spa" : category = "Spa";break;
			case "salon" : category = "Salon";break;
			case "pestcontrol" : category = "Pest_Control";break;
		}

        // Querying
        String query = "MATCH (node : "+category+") set node."+currCategory+" = node."+currCategory+" + 1";
        try {
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            String a = null;
            while (rs.next()) {
            	a = rs.getString("node");
            	System.out.println(a);
            }
        } catch (Exception e) { System.out.print(e);return 404; }
        try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.print(e);
		}
		return 200;
		
	}
	
	@GetMapping("/add/user")
	public int addUser(@RequestParam(defaultValue = "") String email) {
		Driver driver = null;
		try {
			driver = new org.neo4j.jdbc.Driver();
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			DriverManager.registerDriver(driver);
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        Connection con = null;
		try {
			con = DriverManager.getConnection("jdbc:neo4j:bolt://localhost","neo4j","123456");
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			System.out.println(e1);
		}
		String query = "MERGE (u:user{email:\""+email+"\"})";
		System.out.println("Added : "+email);
		try {
            PreparedStatement stmt = con.prepareStatement(query);
            System.out.println("done executing this..");
            stmt.execute();
		} catch (Exception e) { System.out.print(e);return 404; }
        try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.print(e);
		}
		return 200;
	}
	
	@GetMapping("/add/serviceprovider")
	public int addServiceProvider(@RequestParam(defaultValue = "") String servicetype
			,@RequestParam(defaultValue = "") String name,@RequestParam(defaultValue = "") String address,@RequestParam(defaultValue = "false") Boolean isverified
			,@RequestParam(defaultValue = "0.0") Double rating,@RequestParam(defaultValue = "0.0") Double latitude,@RequestParam(defaultValue = "0.0") Double longitude,@RequestParam(defaultValue = "0") Integer popularity) {
		Driver driver = null;
		try {
			driver = new org.neo4j.jdbc.Driver();
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			DriverManager.registerDriver(driver);
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        Connection con = null;
		try {
			con = DriverManager.getConnection("jdbc:neo4j:bolt://localhost","neo4j","123456");
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			System.out.println(e1);
			//e1.printStackTrace();
		}
		String query = "MERGE(s:serviceprovider{servicetype:\""+servicetype+"\",name:\""+name+"\",address:\""+address+"\",isverified:"+isverified+",rating:"+rating+",latitude:"+latitude+",longitude:"+longitude+",popularity:"+popularity+"})";
		System.out.println("Added : "+name);
		try {
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.execute();
        } catch (Exception e) { System.out.print(e);return 404; }
        try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.print(e);
		}
		return 200;
	}
	
	@GetMapping("/add/booking")
	public int addBooking(@RequestParam(defaultValue = "") String email
			,@RequestParam(defaultValue = "") String name,
			@RequestParam(defaultValue = "") String servicetype,
			@RequestParam(defaultValue = "") String phonenumber,
			@RequestParam(defaultValue = "") String address) {
		Driver driver = null;
		try {
			driver = new org.neo4j.jdbc.Driver();
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			DriverManager.registerDriver(driver);
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        Connection con = null;
		try {
			con = DriverManager.getConnection("jdbc:neo4j:bolt://localhost","neo4j","123456");
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			System.out.println(e1);
		}
		
		java.util.Date date=new java.util.Date();  
		
		String query = "MATCH (u:user),(s:serviceprovider) WHERE u.email=\""+email+"\" AND s.name=\""+name+"\" AND s.servicetype=\""+servicetype+"\" CREATE (u)-[b:booked{orderdate:\""+date+"\",phonenumber:\""+phonenumber+"\",address:\""+address+"\"}]->(s)";
	
		try {
            PreparedStatement stmt = con.prepareStatement(query);
            System.out.println(query);
            stmt.execute();
        } catch (Exception e) { System.out.print(e);return 404; }
        try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.print(e);
		}
		return 200;
	}
	
	@GetMapping("/mybookings")
	public List<BookingModel> getMyBookings(@RequestParam(defaultValue="") String email) {
		Driver driver = null;
		try {
			driver = new org.neo4j.jdbc.Driver();
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			DriverManager.registerDriver(driver);
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        Connection con = null;
		try {
			con = DriverManager.getConnection("jdbc:neo4j:bolt://localhost","neo4j","123456");
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			System.out.println(e1);
		}
		
		
		
		String query = "MATCH (:user {email: '"+email+"'})-[r]->(serviceprovider) RETURN r";
		String query1 = "MATCH (:user {email: '"+email+"'})-->(serviceprovider) RETURN serviceprovider";
		
		System.out.println(email);
		List<BookingModel> list = new ArrayList<BookingModel>();
		List<String> date = new ArrayList<String>();
		List<String> cphno = new ArrayList<String>();
		List<String> caddress = new ArrayList<String>();
		int k = 0;
		try {
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            String a = null;
            while (rs.next()) {
            	a = rs.getString("r");
            	HashMap<String,String> map = new Gson().fromJson(a, new TypeToken<HashMap<String, String>>(){}.getType());
            	date.add(map.get("orderdate"));
            	cphno.add(map.get("phonenumber"));
            	caddress.add(map.get("address"));
            }
            
            PreparedStatement stmt1 = con.prepareStatement(query1);
            ResultSet rs1 = stmt1.executeQuery();
            String a1 = null;
            while (rs1.next()) {
            	a1 = rs1.getString("serviceprovider");
            	HashMap<String,Object> map = new Gson().fromJson(a1, new TypeToken<HashMap<String, Object>>(){}.getType());
            	list.add(new BookingModel(
            			map.get("name").toString(),
            			map.get("address").toString(),
            			map.get("servicetype").toString(),
            			cphno.get(k),
            			caddress.get(k),
            			date.get(k)
            			));
            	k++;
            }
            
            System.out.println("DONE EXECUTING ADD BOOKING SUCESSFULLY!");
            
            
        } catch (Exception e) { System.out.print(e);return null; }
        try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.print(e);
		}
		return list;
	}
	
}
