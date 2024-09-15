package net.etfbl.ip.utils;

public class SQLQueries {

	public static final String LOGIN_USER = "SELECT * FROM user WHERE username = ?";
	public static final String GET_USER_NAME = "SELECT first_name, last_name, username, email FROM user WHERE id = ?";
	public static final String GET_USER_BY_ID = "SELECT * FROM user WHERE id = ?";
	public static final String SEND_MESSAGE = "INSERT INTO message (sender_id, recipient_id, subject, content, sent_at) VALUES (?, ?, ?, ?, ?)";
	public static final String GET_MESSAGES_FOR_USER = "SELECT m.id, m.sender_id, m.recipient_id, m.subject, m.content, m.sent_at, m.read_at, u.username AS sender_name FROM message m JOIN user u ON m.sender_id = u.id  WHERE m.recipient_id = ? ORDER BY m.read_at IS NULL DESC, m.sent_at DESC";
	public static final String GET_MESSAGE_BY_ID = "SELECT * from message WHERE id = ?";
	public static final String MARK_MESSAGE_AS_READ = "UPDATE message SET read_at = NOW() WHERE id = ? AND read_at IS NULL;";
	public static final String DELETE_MESSAGE_BY_ID = "DELETE FROM messages WHERE id = ?";

}