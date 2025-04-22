package ru.yandex.practicum.filmorate.storage.dbStorage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.userModel.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Component("userRowMapper")
public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        user.setBirthday(birthday);
        return user;
    }
}
