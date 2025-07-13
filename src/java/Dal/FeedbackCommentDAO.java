/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dal;

import Model.FeedbackComment;
import Model.UserAccount;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hungk
 */
public class FeedbackCommentDAO extends DBcontext.DBContext {

    public List<FeedbackComment> getCommentsByFeedbackId(int feedbackId) {
        List<FeedbackComment> comments = new ArrayList<>();
        String sql = "SELECT fc.*, ua.username, ua.fullname, ua.avatar_url, ua.role, ua.status, ua.email "
                + "FROM FeedbackComment fc "
                + "JOIN UserAccount ua ON fc.user_id = ua.id "
                + "WHERE fc.feedback_id = ? AND fc.is_deleted = 0 "
                + "ORDER BY fc.created_at";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, feedbackId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String createdAtStr = rs.getTimestamp("created_at") != null
                        ? rs.getTimestamp("created_at").toString()
                        : null;
                UserAccount user = new UserAccount(
                        rs.getString("user_id"),
                        rs.getString("username"),
                        rs.getString("fullname"),
                        rs.getString("email"),
                        rs.getString("avatar_url"),
                        rs.getString("role"),
                        rs.getString("status"),
                        createdAtStr,
                        null, null, null, false, null
                );

                FeedbackComment comment = new FeedbackComment(
                        rs.getInt("id"),
                        rs.getInt("feedback_id"),
                        rs.getInt("parent_comment_id"),
                        rs.getString("user_id"),
                        rs.getString("content"),
                        rs.getString("image_url"),
                        rs.getTimestamp("created_at"),
                        rs.getBoolean("is_edited"),
                        rs.getBoolean("is_deleted")
                );
                comment.setAccount(user);
                comments.add(comment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return comments;
    }

    public boolean addComment(FeedbackComment comment) {
        String sql = "INSERT INTO FeedbackComment (feedback_id, parent_comment_id, user_id, content, image_url, created_at, is_edited, is_deleted) "
                + "VALUES (?, ?, ?, ?, ?, GETDATE(), ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, comment.getFeedback_id());
            if (comment.getParent_comment_id() == 0) {
                stmt.setNull(2, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(2, comment.getParent_comment_id());
            }
            stmt.setString(3, comment.getUser_id());
            stmt.setString(4, comment.getContent());
            stmt.setString(5, comment.getImage_url());
            stmt.setBoolean(6, comment.isIs_edited());
            stmt.setBoolean(7, comment.isIs_deleted());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateCommentContent(int commentId, String newContent) {
        String sql = "UPDATE FeedbackComment SET content = ?, is_edited = 1 WHERE id = ? AND is_deleted = 0";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newContent);
            stmt.setInt(2, commentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean softDeleteComment(int commentId) {
        String sql = "UPDATE FeedbackComment SET is_deleted = 1 WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, commentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void main(String[] args) {
        FeedbackCommentDAO commentDAO = new FeedbackCommentDAO();
        List<FeedbackComment> comments = commentDAO.getCommentsByFeedbackId(1);
        for (FeedbackComment comment : comments) {
            System.out.println(comment);
        }
    }
}
