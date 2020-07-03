package com.infotechnano.vidtogo.vidtogo.services;

import com.infotechnano.vidtogo.vidtogo.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository("postgres")
public class UserService {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserService(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<User> mapUser() {
        return (resultSet, i) -> {
            String studentIdStr = resultSet.getString("student_id");
            UUID studentId = UUID.fromString(studentIdStr);

            String name = resultSet.getString("name");
            String email = resultSet.getString("email");

            return new User(
                    studentId,
                    name,
                    email
            );
        };
    }

    public User registerUser(User user){
        UUID userId = UUID.randomUUID();

        //hashed form of the password
        String password = new BCryptPasswordEncoder(10).encode(user.getPassword());

        String sql = "INSERT INTO Users (id,name,email,password) VALUES (?,?,?,?)";

        if(checkEmailExists(user.getEmail())){
            return null;
        }

        int rowsAffected = jdbcTemplate.update(sql, userId,user.getName(),user.getEmail(),password);

        if(rowsAffected > 0){
            String querySql = "SELECT id,name,email FROM Users WHERE id=?";
            return jdbcTemplate.queryForObject(querySql,new Object[]{userId},mapUser());
        }else{
            return null;
        }
    }

    public User loginUser(UUID userId){
        String sql = "SELECT id,name,email FROM Users WHERE id=?";
        return jdbcTemplate.queryForObject(sql,new Object[]{userId},mapUser());
    }

    @SuppressWarnings("ConstantConditions")
    private boolean checkEmailExists(String email) {
        String sql = "SELECT EXISTS (SELECT 1 FROM Users WHERE email = ?)";
        return jdbcTemplate.queryForObject(
                sql,
                new Object[]{email},
                (resultSet, i) -> resultSet.getBoolean(1)
        );
    }

    public Integer updateUser(UUID userId,User user){
        String sql = "UPDATE Users Set name=?,email=? WHERE id=?";
        return jdbcTemplate.update(sql,user.getName(),user.getEmail(),userId);
    }

    public Integer deleteUser(UUID userId) {
        String sql = "DELETE FROM Users WHERE id = ?";
        return jdbcTemplate.update(sql, userId);
    }

    public boolean checkPassword(String password){
        String newPassword = new BCryptPasswordEncoder(10).encode(password);

        String sql = "SELECT password FROM Users WHERE password=?";
        String currentPassword = jdbcTemplate.queryForObject(sql,new Object[]{newPassword},(resultSet, i) -> resultSet.getString("password"));
        if(currentPassword != null){
            return currentPassword.equals(newPassword);
        }else{
            return false;
        }

    }
}
