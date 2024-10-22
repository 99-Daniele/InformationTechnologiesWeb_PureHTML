package it.polimi.tiw.commerce.dao;

import java.sql.*;

import it.polimi.tiw.commerce.beans.Supplier;

public class SupplierDAO {

	private Connection connection;

	public SupplierDAO(Connection connection) {
		this.connection = connection;
	}
	
	public boolean doesSupplierExist(int supplierID)  throws SQLException {
		String query = "SELECT * FROM supplier WHERE supplierID = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, supplierID);
			result = pstatement.executeQuery();
			if(!result.next())
				return false;
			else
				return true;
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				if (result != null) {
					result.close();
				}
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
			try {
				if (pstatement != null) {
					pstatement.close();
				}
			} catch (Exception e2) {
				throw new SQLException(e2);
			}
		}
	}
	
	public Supplier findSupplierBySupplierID(int supplierID) throws SQLException {
		Supplier supplier = new Supplier();
		String query = "SELECT * FROM supplier WHERE supplierID = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, supplierID);
			result = pstatement.executeQuery();
			if(result.next()) {
				supplier.setSupplierID(supplierID);
				supplier.setName(result.getString("name"));
				supplier.setScore(result.getInt("score"));
				supplier.setThreshold(result.getInt("threshold"));
			}
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				if (result != null) {
					result.close();
				}
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
			try {
				if (pstatement != null) {
					pstatement.close();
				}
			} catch (Exception e2) {
				throw new SQLException(e2);
			}
		}
		return supplier;
	}
	
	public int calcShipmentCost(int supplierID, int amount, int totalCost) throws SQLException {
		String query = "SELECT threshold FROM supplier WHERE supplierID = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, supplierID);
			result = pstatement.executeQuery();
			if(result.next()) {
				if(result.getInt("threshold") == 0 || result.getInt("threshold") > totalCost) {
					SpendingRangeDAO srDAO = new SpendingRangeDAO(connection);
					return srDAO.getShipmentCost(supplierID, amount);
				}
			}
			return 0;
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				if (result != null) {
					result.close();
				}
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
			try {
				if (pstatement != null) {
					pstatement.close();
				}
			} catch (Exception e2) {
				throw new SQLException(e2);
			}
		}
	}
}
