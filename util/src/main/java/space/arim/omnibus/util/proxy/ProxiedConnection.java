/* 
 * Omnibus-util
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * Omnibus-util is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Omnibus-util is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Omnibus-util. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.omnibus.util.proxy;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * A parent class for connections which simply redirect, a.k.a. <i>proxy</i>, calls to another connection (the backing connection). <br>
 * Such "connections" do no work themselves, but merely refer calls to their backing connections. <br>
 * <br>
 * However, the proxy connection's additional call layer provides fine tuned control over reads and writes to the backing connection. <br>
 * Programmers may extend this class to utilise this enhanced control. <br>
 * <br>
 * Note that a reference is retained to the backing connection. Changes to the backing connection are reflected in proxied connections.
 * 
 * @author A248
 *
 */
public abstract class ProxiedConnection extends ProxiedObject<Connection> implements Connection {
	
	/**
	 * Creates a ProxiedConnection based on a backing connection
	 * 
	 * @param original the original, backing connection
	 */
	protected ProxiedConnection(Connection original) {
		super(original);
	}
	
	@Override
	public void close() throws SQLException {
		getOriginal().close();
	}
	
	@Override
	public boolean isClosed() throws SQLException {
		return getOriginal().isClosed();
	}
	
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return getOriginal().unwrap(iface);
	}
	
	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return getOriginal().isWrapperFor(iface);
	}
	
	@Override
	public Statement createStatement() throws SQLException {
		return getOriginal().createStatement();
	}
	
	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return getOriginal().prepareStatement(sql);
	}
	
	@Override
	public CallableStatement prepareCall(String sql) throws SQLException {
		return getOriginal().prepareCall(sql);
	}
	
	@Override
	public String nativeSQL(String sql) throws SQLException {
		return getOriginal().nativeSQL(sql);
	}
	
	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		getOriginal().setAutoCommit(autoCommit);
	}
	
	@Override
	public boolean getAutoCommit() throws SQLException {
		return getOriginal().getAutoCommit();
	}
	
	@Override
	public void commit() throws SQLException {
		getOriginal().commit();
	}
	
	@Override
	public void rollback() throws SQLException {
		getOriginal().rollback();
	}
	
	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		return getOriginal().getMetaData();
	}
	
	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
		getOriginal().setReadOnly(readOnly);
	}
	
	@Override
	public boolean isReadOnly() throws SQLException {
		return getOriginal().isReadOnly();
	}
	
	@Override
	public void setCatalog(String catalog) throws SQLException {
		getOriginal().setCatalog(catalog);
	}
	
	@Override
	public String getCatalog() throws SQLException {
		return getOriginal().getCatalog();
	}
	
	@Override
	public void setTransactionIsolation(int level) throws SQLException {
		getOriginal().setTransactionIsolation(level);
	}
	
	@Override
	public int getTransactionIsolation() throws SQLException {
		return getOriginal().getTransactionIsolation();
	}
	
	@Override
	public SQLWarning getWarnings() throws SQLException {
		return getOriginal().getWarnings();
	}
	
	@Override
	public void clearWarnings() throws SQLException {
		getOriginal().clearWarnings();
	}
	
	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		return getOriginal().createStatement(resultSetType, resultSetConcurrency);
	}
	
	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return getOriginal().prepareStatement(sql, resultSetType, resultSetConcurrency);
	}
	
	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return getOriginal().prepareCall(sql, resultSetType, resultSetConcurrency);
	}
	
	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return getOriginal().getTypeMap();
	}
	
	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		getOriginal().setTypeMap(map);
	}
	
	@Override
	public void setHoldability(int holdability) throws SQLException {
		getOriginal().setHoldability(holdability);
	}
	
	@Override
	public int getHoldability() throws SQLException {
		return getOriginal().getHoldability();
	}
	
	@Override
	public Savepoint setSavepoint() throws SQLException {
		return getOriginal().setSavepoint();
	}
	
	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		return getOriginal().setSavepoint(name);
	}
	
	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		getOriginal().rollback(savepoint);
	}
	
	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		getOriginal().releaseSavepoint(savepoint);
	}
	
	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return getOriginal().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
	}
	
	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return getOriginal().prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}
	
	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return getOriginal().prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}
	
	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		return getOriginal().prepareStatement(sql, autoGeneratedKeys);
	}
	
	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		return getOriginal().prepareStatement(sql, columnIndexes);
	}
	
	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		return getOriginal().prepareStatement(sql, columnNames);
	}
	
	@Override
	public Clob createClob() throws SQLException {
		return getOriginal().createClob();
	}
	
	@Override
	public Blob createBlob() throws SQLException {
		return getOriginal().createBlob();
	}
	
	@Override
	public NClob createNClob() throws SQLException {
		return getOriginal().createNClob();
	}
	
	@Override
	public SQLXML createSQLXML() throws SQLException {
		return getOriginal().createSQLXML();
	}
	
	@Override
	public boolean isValid(int timeout) throws SQLException {
		return getOriginal().isValid(timeout);
	}
	
	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		getOriginal().setClientInfo(name, value);
	}
	
	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		getOriginal().setClientInfo(properties);
	}
	
	@Override
	public String getClientInfo(String name) throws SQLException {
		return getOriginal().getClientInfo(name);
	}
	
	@Override
	public Properties getClientInfo() throws SQLException {
		return getOriginal().getClientInfo();
	}
	
	@Override
	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		return getOriginal().createArrayOf(typeName, elements);
	}
	
	@Override
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		return getOriginal().createStruct(typeName, attributes);
	}
	
	@Override
	public void setSchema(String schema) throws SQLException {
		getOriginal().setSchema(schema);
	}
	
	@Override
	public String getSchema() throws SQLException {
		return getOriginal().getSchema();
	}
	
	@Override
	public void abort(Executor executor) throws SQLException {
		getOriginal().abort(executor);
	}
	
	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		getOriginal().setNetworkTimeout(executor, milliseconds);
	}
	
	@Override
	public int getNetworkTimeout() throws SQLException {
		return getOriginal().getNetworkTimeout();
	}
	
}
