package com.study.messages.dao;

import com.study.messages.dto.MySubscription;
import com.study.messages.dto.Subscription;
import com.study.messages.dto.SubscriptionStats;
import com.study.messages.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcSubscriptionsRepository implements SubscriptionsRepository {

    private final JdbcTemplate jdbcTemplate;
    private final DaoHelper daoHelper;

    @Override
    public Subscription createSubscription(int subscriberId, int producerId) {
        return jdbcTemplate.queryForObject(Constants.CREATE_SUBSCRIPTION,
                (rs, rowNum) -> daoHelper.mapSubscription(rs), subscriberId, producerId);
    }

    @Override
    public boolean deleteSubscription(int subscriberId, int producerId) {
        return jdbcTemplate.update(Constants.DELETE_SUBSCRIPTION, subscriberId, producerId) > 0;
    }

    @Override
    public List<MySubscription> findMySubscriptions(int subscriberId) {
        return jdbcTemplate.query(Constants.GET_MY_SUBSCRIPTIONS,
                (rs, rowNum) -> daoHelper.mapMySubscription(rs), subscriberId);
    }

    @Override
    public boolean exists(int subscriberId, int producerId) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(
                Constants.CHECK_SUBSCRIPTION, Boolean.class, subscriberId, producerId));
    }

    @Override
    public List<Integer> findFollowers(int producerId, int afterId, int limit) {
        return jdbcTemplate.queryForList(Constants.GET_FOLLOWERS, Integer.class, producerId, afterId, limit);
    }

    @Override
    public SubscriptionStats getStats(int userId) {
        return jdbcTemplate.queryForObject(Constants.GET_STATS,
                (rs, rowNum) -> daoHelper.mapStats(rs), userId, userId);
    }
}
