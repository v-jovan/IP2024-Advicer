package net.etfbl.ip.utils;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;

public class ConnectionPool {

	private static ConnectionPool instance;
	private final String jdbcURL;
	private final String username;
	private final String password;
	private final int maxIdleConnections;
	private final int maxConnections;
	private int connectCount;
	private final List<Connection> freeConnections;
	private final List<Connection> usedConnections;
	private final Lock lock = new ReentrantLock();
	private final Condition connectionAvailable = lock.newCondition();

	public static ConnectionPool getInstance() {
		if (instance == null) {
			synchronized (ConnectionPool.class) {
				if (instance == null) {
					instance = new ConnectionPool();
				}
			}
		}
		return instance;
	}

	private ConnectionPool() {
		Properties properties = new Properties();
		try {
			try (InputStream input = getClass().getClassLoader()
					.getResourceAsStream("net/etfbl/ip/utils/ConnectionPool.properties")) {
				if (input == null) {
					throw new RuntimeException("Podešavanja nisu pronađena.");
				}
				properties.load(input);
			}

			jdbcURL = properties.getProperty("jdbc.url");
			username = properties.getProperty("jdbc.username");
			password = properties.getProperty("jdbc.password");
			String driver = properties.getProperty("jdbc.driver");
			int preconnectCount = Integer.parseInt(properties.getProperty("pool.preconnect.count"));
			maxIdleConnections = Integer.parseInt(properties.getProperty("pool.max.idle.connections"));
			maxConnections = Integer.parseInt(properties.getProperty("pool.max.connections"));

			freeConnections = new ArrayList<>();
			usedConnections = new ArrayList<>();

			Class.forName(driver);
			for (int i = 0; i < preconnectCount; i++) {
				Connection conn = DriverManager.getConnection(jdbcURL, username, password);
				freeConnections.add(conn);
			}
			connectCount = preconnectCount;

		} catch (Exception ex) {
			throw new RuntimeException("Error initializing connection pool", ex);
		}
	}

	public Connection checkOut() throws SQLException {
		lock.lock();
		try {
			while (freeConnections.isEmpty() && connectCount >= maxConnections) {
				try {
					if (connectionAvailable.awaitNanos(10000000000L) <= 0) {
						throw new SQLException("Timeout waiting for a connection.");
					}
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
					throw new SQLException("Interrupted while waiting for a connection.", ex);
				}
			}

			Connection conn;
			if (!freeConnections.isEmpty()) {
				conn = freeConnections.remove(freeConnections.size() - 1);
				if (!isValid(conn)) {
					conn = DriverManager.getConnection(jdbcURL, username, password);
				}
			} else {
				conn = DriverManager.getConnection(jdbcURL, username, password);
				connectCount++;
			}

			usedConnections.add(conn);
			return conn;

		} finally {
			lock.unlock();
		}
	}

	public void checkIn(Connection conn) {
		if (conn == null) {
			return;
		}

		lock.lock();
		try {
			if (usedConnections.remove(conn)) {
				if (isValid(conn)) {
					if (freeConnections.size() < maxIdleConnections) {
						freeConnections.add(conn);
					} else {
						conn.close();
						connectCount--;
					}
				} else {
					conn.close();
					connectCount--;
				}
				connectionAvailable.signal();
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	private boolean isValid(Connection conn) {
		try {
			return !conn.isClosed() && conn.isValid(1);
		} catch (SQLException ex) {
			return false;
		}
	}

	public void shutdown() {
		lock.lock();
		try {
			for (Connection conn : freeConnections) {
				try {
					conn.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			freeConnections.clear();
			connectCount = 0;

			try {
				Enumeration<Driver> drivers = DriverManager.getDrivers();
				while (drivers.hasMoreElements()) {
					Driver driver = drivers.nextElement();
					DriverManager.deregisterDriver(driver);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			AbandonedConnectionCleanupThread.checkedShutdown();

		} finally {
			lock.unlock();
		}
	}

}
