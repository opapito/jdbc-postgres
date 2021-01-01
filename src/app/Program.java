package app;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import db.DB;
import entities.Order;
import entities.OrderStatus;
import entities.Product;

public class Program {

	//Java's application entry point signature ("main" keyword)
	public static void main(String[] args) throws SQLException {
		
		/*
		Product p = new Product();
		p.setName("Pizza");
		System.out.println("Nome do produto = " + p.getName());
		*/
		Connection conn = DB.getConnection();
	
		//Instantiating an object for sending SQL commands 
		Statement st = conn.createStatement();
			
		//Storing the query resulting table object
		//ResultSet rs = st.executeQuery("select * from tb_product");
		//ResultSet rs = st.executeQuery("select * from tb_order");
		ResultSet rs = st.executeQuery("SELECT * FROM tb_order " + 
				"INNER JOIN tb_order_product ON tb_order.id = tb_order_product.order_id " + 
				"INNER JOIN tb_product ON tb_product.id = tb_order_product.product_id");
			
		//map collection key->value
		Map<Long, Order> map = new HashMap<>();
		Map<Long, Product> prods = new HashMap<>();
		
		//Reading result object table line by line 
		while (rs.next()) {
			Long orderId = rs.getLong("order_id");
			//preventing duplicates values
			if (map.get(orderId) == null) {
				//Product p = instantiateProduct(rs);
				Order order = instantiateOrder(rs);
				map.put(orderId, order);
			}
			
			Long productId = rs.getLong("product_id");
			//preventing duplicates values
			if (prods.get(productId) == null) {
				Product p = instantiateProduct(rs);
				prods.put(productId, p);
			}
			
			map.get(orderId).getProducts().add(prods.get(productId));

			//System.out.println(rs.getLong("Id") + ", " + rs.getString("Name"));
			//Before print the object, it is necessary to create a toString() method in Product Class first 
			//System.out.println(p);
			//System.out.println(order);
		}
		for(Long orderId : map.keySet()) {
			System.out.println(map.get(orderId));
			for(Product p : map.get(orderId).getProducts()) {
				System.out.println(p);
			}
			System.out.println();
		}
	}
	
	//Auxiliary method
	private static Order instantiateOrder(ResultSet rs) throws SQLException {
		Order order = new Order();
		order.setId(rs.getLong("order_id"));
		order.setLatitude(rs.getDouble("latitude"));
		order.setLongitude(rs.getDouble("longitude"));
		order.setMoment(rs.getTimestamp("moment").toInstant());//toInstant() converts the data field to the Instant type
		//Converting integer field (0 or 1) to order status enum type (0 or 1)
		order.setStatus(OrderStatus.values()[rs.getInt("status")]);
		return order;
	}
	
	//Auxiliary method
	private static Product instantiateProduct(ResultSet rs) throws SQLException {
		Product p = new Product();
		p.setId(rs.getLong("product_id"));
		p.setDescription(rs.getString("description"));
		p.setName(rs.getString("name"));
		p.setImageUri(rs.getString("image_uri"));
		p.setPrice(rs.getDouble("price"));
		return p;
	}
}
