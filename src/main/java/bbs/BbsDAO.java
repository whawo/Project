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
			// 실제로 실행 했을때의 결과를 가져올 수 있게 해줌
			if (rs.next()) {
				// 결과가 있는 경우
				return rs.getString(1);
				// 현재의 날짜 반환하게 됨
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
// 데이터베이스 오류
	}

	public int getNext() {
		String SQL = "SELECT bbsID FROM BBS ORDER BY bbsID DESC;";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			// 실제로 실행 했을때의 결과를 가져올 수 있게 해줌
			if (rs.next()) {
				// 결과가 있는 경우
				return rs.getInt(1) + 1;
				// 현재의 날짜 반환하게 됨
			}
			return 1;
			// 첫번째 게시물 작성시 매겨지는 번호
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
		// 데이터베이스 오류
	}

	public int write(String bbsTitle, String userID, String bbsContent) {
		// 실제로 데이터 베이스에 넣어주기
		String SQL = "INSERT INTO BBS VALUES (?, ?, ?, ?, ?, ?);";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, getNext());
			// 하나씩 값을 넣어주기 다음번에 쓰여야 할 게시물 번호가 됨
			pstmt.setString(2, bbsTitle);
			pstmt.setString(3, userID);
			pstmt.setString(4, getDate());
			pstmt.setString(5, bbsContent);
			pstmt.setInt(6, 1);
//첫번째 작성시 삭제 상태가 아니여야 하기 때문에 1로 표기
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1; // 데이터베이스 오류
	}

	public ArrayList<Bbs> getList(int pageNumber) {
		String SQL = "SELECT * FROM BBS WHERE bbsID < ? AND bbsAvaliable = 1 ORDER BY bbsID DESC LIMIT 10;";
		ArrayList<Bbs> list = new ArrayList<Bbs>();
		// bbs에서 나오는 인스턴스를 보관 할 수 있는 리스트를 만듬
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, getNext() - (pageNumber - 1) * 10);
//getnext = 다음글에 작성될 게시글 번호 
			rs = pstmt.executeQuery();
			// 실제로 실행 했을때의 결과를 가져올 수 있게 해줌
			while(rs.next()) { // 결과가 있는 경우
				Bbs bbs = new Bbs();
				bbs.setBbsID(rs.getInt(1));
				bbs.setBbsTitle(rs.getString(2));
				bbs.setUserID(rs.getString(3));
				bbs.setBbsDate(rs.getString(4));
				bbs.setBbsContent(rs.getString(5));
				bbs.setBbsAvaliable(rs.getInt(6));
				list.add(bbs); // 현재의 날짜 반환하게 됨
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list; // 데이터베이스 오류
	}

	public boolean nextPage(int pageNumber) { // 게시글이 10개 일 경우 10단위로 끊기는 경우 다음페이지 안나오게
		String SQL = "SELECT * FROM BBS WHERE bbsID < ? AND bbsAvaliable = 1;";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, getNext() - (pageNumber - 1) * 10); // getnext = 다음글에 작성될 게시글 번호
			rs = pstmt.executeQuery(); // 실제로 실행 했을때의 결과를 가져올 수 있게 해줌
			if (rs.next()) { // 결과가 있는 경우
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false; // 데이터베이스 오류
	}
	public Bbs getBbs(int bbsID) {
		 String SQL = "SELECT * FROM BBS WHERE bbsID = ? ;";
		 try { 
		PreparedStatement pstmt = conn.prepareStatement(SQL);
		 pstmt.setInt(1, bbsID); //getnext = 다음글에 작성될 게시글 번호
		 rs = pstmt.executeQuery(); // 실제로 실행 했을때의 결과를 가져올 수 있게 해줌
		 if(rs.next()){ //결과가 있는 경우
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
		 
		return null; // 데이터베이스 오류
		 }
	//게시글 수정 메소드
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
			return -1; //데이터베이스 오류
		}
		
		//게시글 삭제 메소드
		public int delete(int bbsID) {
			//실제 데이터를 삭제하는 것이 아니라 게시글 유효숫자를 '0'으로 수정한다
			String sql = "update bbs set bbsAvaliable = 0 where bbsID = ?;";
			try {
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, bbsID);
				return pstmt.executeUpdate();
			}catch (Exception e) {
				e.printStackTrace();
			}
			return -1; //데이터베이스 오류 
		}
		
	}
