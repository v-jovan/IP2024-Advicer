package net.etfbl.ip.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.etfbl.ip.beans.MessageBean;
import net.etfbl.ip.utils.ConnectionPool;
import net.etfbl.ip.utils.SQLQueries;

public class MessageManager {
	public List<MessageBean> getMessagesForUser(int userId) {
		List<MessageBean> messages = new ArrayList<>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = ConnectionPool.getInstance().checkOut();
			ps = conn.prepareStatement(SQLQueries.GET_MESSAGES_FOR_USER);
			ps.setInt(1, userId);
			rs = ps.executeQuery();
			while (rs.next()) {
				MessageBean message = new MessageBean();
				message.setId(rs.getInt("id"));
				message.setSenderId(rs.getInt("sender_id"));
				message.setRecipientId(rs.getInt("recipient_id"));
				message.setSubject(rs.getString("subject"));
				message.setContent(rs.getString("content"));
				message.setSentAt(rs.getTimestamp("sent_at"));
				message.setReadAt(rs.getTimestamp("read_at"));
				message.setSenderName(rs.getString("sender_name"));
				messages.add(message);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					ConnectionPool.getInstance().checkIn(conn);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return messages;
	}

	public MessageBean getMessageById(int messageId) {
		Connection conn = null;
		MessageBean message = null;

		try {
			conn = ConnectionPool.getInstance().checkOut();
			conn.setAutoCommit(false); // Beginning transaction

			PreparedStatement psMessage = conn.prepareStatement(SQLQueries.GET_MESSAGE_BY_ID);
			psMessage.setInt(1, messageId);
			ResultSet rsMessage = psMessage.executeQuery();

			if (rsMessage.next()) {
				message = new MessageBean();
				message.setId(rsMessage.getInt("id"));
				message.setSenderId(rsMessage.getInt("sender_id"));
				message.setRecipientId(rsMessage.getInt("recipient_id"));
				message.setSubject(rsMessage.getString("subject"));
				message.setContent(rsMessage.getString("content"));
				message.setSentAt(rsMessage.getTimestamp("sent_at"));
				message.setReadAt(rsMessage.getTimestamp("read_at"));

				PreparedStatement psUser = conn.prepareStatement(SQLQueries.GET_USER_BY_ID);
				psUser.setInt(1, message.getSenderId());
				ResultSet rsUser = psUser.executeQuery();

				if (rsUser.next()) {
					String senderName = rsUser.getString("first_name") + " " + rsUser.getString("last_name");
					if (senderName.trim().isEmpty()) {
						senderName = rsUser.getString("username");
					}
					message.setSenderName(senderName);
					message.setSenderEmail(rsUser.getString("email"));
				}

				rsUser.close();
				psUser.close();
			}

			rsMessage.close();
			psMessage.close();
			conn.commit(); // Committing transaction

		} catch (SQLException e) {
			// Rolling back the transaction in case of failure
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException rollbackEx) {
					rollbackEx.printStackTrace();
				}
			}
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.setAutoCommit(true);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				ConnectionPool.getInstance().checkIn(conn);
			}
		}

		return message;
	}

	public void markMessageAsRead(int messageId) {
		Connection conn = null;

		try {
			conn = ConnectionPool.getInstance().checkOut();
			PreparedStatement ps = conn.prepareStatement(SQLQueries.MARK_MESSAGE_AS_READ);
			ps.setInt(1, messageId);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				ConnectionPool.getInstance().checkIn(conn);
			}
		}
	}

	public void deleteMessageById(int messageId) {
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = ConnectionPool.getInstance().checkOut();
			ps = conn.prepareStatement(SQLQueries.DELETE_MESSAGE_BY_ID);
			ps.setInt(1, messageId);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					ConnectionPool.getInstance().checkIn(conn);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
