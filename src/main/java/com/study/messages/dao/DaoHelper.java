package com.study.messages.dao;

import com.study.messages.dto.Message;
import com.study.messages.dto.MySubscription;
import com.study.messages.dto.Subscription;
import com.study.messages.dto.SubscriptionStats;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

@Component
public class DaoHelper {

    public Message mapMessage(ResultSet rs) throws SQLException {
        Message message = new Message();
        message.setId(rs.getInt("id"));
        message.setText(rs.getString("text"));
        message.setAuthorId(rs.getInt("author_id"));
        message.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
        message.setUpdatedAt(rs.getObject("updated_at", OffsetDateTime.class));
        return message;
    }

    public Subscription mapSubscription(ResultSet rs) throws SQLException {
        return new Subscription(
                rs.getInt("subscriber_id"),
                rs.getInt("producer_id"),
                rs.getObject("created_at", OffsetDateTime.class)
        );
    }

    public MySubscription mapMySubscription(ResultSet rs) throws SQLException {
        return new MySubscription(
                rs.getInt("producer_id"),
                rs.getObject("subscribed_at", OffsetDateTime.class)
        );
    }

    public SubscriptionStats mapStats(ResultSet rs) throws SQLException {
        return new SubscriptionStats(
                rs.getLong("followers_count"),
                rs.getLong("following_count")
        );
    }
}
