package com.study.messages.dao;

import com.study.messages.dto.MySubscription;
import com.study.messages.dto.Subscription;
import com.study.messages.dto.SubscriptionStats;

import java.util.List;

public interface SubscriptionsRepository {

    Subscription createSubscription(int subscriberId, int producerId);

    boolean deleteSubscription(int subscriberId, int producerId);

    List<MySubscription> findMySubscriptions(int subscriberId);

    boolean exists(int subscriberId, int producerId);

    List<Integer> findFollowers(int producerId, int afterId, int limit);

    SubscriptionStats getStats(int userId);
}
