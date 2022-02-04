package bbs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class BbsDAO {
	private Connection conn;
	private ResultSet rs;

	public BbsDAO() {
		try {
			String dbURL = "jdbc:mysql://localhost:3306/BBS?serverTimezone=Asia/Seoul";
			String dbID = "root";
			String dbPassword = "root";
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(dbURL, dbID, dbPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getDate() {
		String SQL = "SELECT NOW();";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			// ������ ���� �������� ����� ������ �� �ְ� ����
			if (rs.next()) {
				// ����� �ִ� ���
				return rs.getString(1);
				// ������ ��¥ ��ȯ�ϰ� ��
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
// �����ͺ��̽� ����
	}

	public int getNext() {
		String SQL = "SELECT bbsID FROM BBS ORDER BY bbsID DESC;";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			// ������ ���� �������� ����� ������ �� �ְ� ����
			if (rs.next()) {
				// ����� �ִ� ���
				return rs.getInt(1) + 1;
				// ������ ��¥ ��ȯ�ϰ� ��
			}
			return 1;
			// ù��° �Խù� �ۼ��� �Ű����� ��ȣ
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
		// �����ͺ��̽� ����
	}

	public int write(String bbsTitle, String userID, String bbsContent) {
		// ������ ������ ���̽��� �־��ֱ�
		String SQL = "INSERT INTO BBS VALUES (?, ?, ?, ?, ?, ?);";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, getNext());
			// �ϳ��� ���� �־��ֱ� �������� ������ �� �Խù� ��ȣ�� ��
			pstmt.setString(2, bbsTitle);
			pstmt.setString(3, userID);
			pstmt.setString(4, getDate());
			pstmt.setString(5, bbsContent);
			pstmt.setInt(6, 1);
//ù��° �ۼ��� ���� ���°� �ƴϿ��� �ϱ� ������ 1�� ǥ��
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1; // �����ͺ��̽� ����
	}

	public ArrayList<Bbs> getList(int pageNumber) {
		String SQL = "SELECT * FROM BBS WHERE bbsID < ? AND bbsAvaliable = 1 ORDER BY bbsID DESC LIMIT 10;";
		ArrayList<Bbs> list = new ArrayList<Bbs>();
		// bbs���� ������ �ν��Ͻ��� ���� �� �� �ִ� ����Ʈ�� ����
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, getNext() - (pageNumber - 1) * 10);
//getnext = �����ۿ� �ۼ��� �Խñ� ��ȣ 
			rs = pstmt.executeQuery();
			// ������ ���� �������� ����� ������ �� �ְ� ����
			while(rs.next()) { // ����� �ִ� ���
				Bbs bbs = new Bbs();
				bbs.setBbsID(rs.getInt(1));
				bbs.setBbsTitle(rs.getString(2));
				bbs.setUserID(rs.getString(3));
				bbs.setBbsDate(rs.getString(4));
				bbs.setBbsContent(rs.getString(5));
				bbs.setBbsAvaliable(rs.getInt(6));
				list.add(bbs); // ������ ��¥ ��ȯ�ϰ� ��
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list; // �����ͺ��̽� ����
	}

	public boolean nextPage(int pageNumber) { // �Խñ��� 10�� �� ��� 10������ ����� ��� ���������� �ȳ�����
		String SQL = "SELECT * FROM BBS WHERE bbsID < ? AND bbsAvaliable = 1;";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, getNext() - (pageNumber - 1) * 10); // getnext = �����ۿ� �ۼ��� �Խñ� ��ȣ
			rs = pstmt.executeQuery(); // ������ ���� �������� ����� ������ �� �ְ� ����
			if (rs.next()) { // ����� �ִ� ���
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false; // �����ͺ��̽� ����
	}
	public Bbs getBbs(int bbsID) {
		 String SQL = "SELECT * FROM BBS WHERE bbsID = ? ;";
		 try { 
		PreparedStatement pstmt = conn.prepareStatement(SQL);
		 pstmt.setInt(1, bbsID); //getnext = �����ۿ� �ۼ��� �Խñ� ��ȣ
		 rs = pstmt.executeQuery(); // ������ ���� �������� ����� ������ �� �ְ� ����
		 if(rs.next()){ //����� �ִ� ���
		 Bbs bbs = new Bbs();
		 bbs.setBbsID(rs.getInt(1));
		 bbs.setBbsTitle(rs.getString(2));
		 bbs.setUserID(rs.getString(3));
		 bbs.setBbsDate(rs.getString(4));
		 bbs.setBbsContent(rs.getString(5));
		 bbs.setBbsAvaliable(rs.getInt(6)); 
		return bbs;
		 }
		 }catch (Exception e) {
		 e.printStackTrace();
		 } 
		 
		return null; // �����ͺ��̽� ����
		 }
	//�Խñ� ���� �޼ҵ�
		public int update(int bbsID, String bbsTitle, String bbsContent) {
			String sql = "update bbs set bbsTitle = ?, bbsContent = ? where bbsID = ?;";
			try {
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, bbsTitle);
				pstmt.setString(2, bbsContent);
				pstmt.setInt(3, bbsID);
				return pstmt.executeUpdate();
			}catch (Exception e) {
				e.printStackTrace();
			}
			return -1; //�����ͺ��̽� ����
		}
		
		//�Խñ� ���� �޼ҵ�
		public int delete(int bbsID) {
			//���� �����͸� �����ϴ� ���� �ƴ϶� �Խñ� ��ȿ���ڸ� '0'���� �����Ѵ�
			String sql = "update bbs set bbsAvaliable = 0 where bbsID = ?;";
			try {
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, bbsID);
				return pstmt.executeUpdate();
			}catch (Exception e) {
				e.printStackTrace();
			}
			return -1; //�����ͺ��̽� ���� 
		}
		
	}
