package com.study.messages.services;

import com.study.messages.clients.UsersClient;
import com.study.messages.dao.SubscriptionsRepository;
import com.study.messages.dto.FollowersPage;
import com.study.messages.dto.MySubscription;
import com.study.messages.dto.RemoteUser;
import com.study.messages.dto.Subscription;
import com.study.messages.dto.SubscriptionCheck;
import com.study.messages.dto.SubscriptionStats;
import com.study.messages.exceptions.ApiException;
import com.study.messages.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionsRepository subscriptionsRepository;
    private final UsersClient usersClient;

    public Subscription subscribe(int subscriberId, int producerId, String bearerToken) {
        RemoteUser target = usersClient.getUser(producerId, bearerToken);
        List<String> roles = target.getRoles();
        boolean canProduce = roles != null && (roles.contains("producer") || roles.contains("administrator"));
        if (!canProduce) {
            throw new ApiException(ErrorCode.NOT_PRODUCER);
        }
        try {
            return subscriptionsRepository.createSubscription(subscriberId, producerId);
        } catch (DuplicateKeyException e) {
            throw new ApiException(ErrorCode.ALREADY_SUBSCRIBED);
        } catch (DataIntegrityViolationException e) {
            throw new ApiException(ErrorCode.SELF_SUBSCRIPTION);
        }
    }

    public void unsubscribe(int subscriberId, int producerId) {
        if (!subscriptionsRepository.deleteSubscription(subscriberId, producerId)) {
            throw new ApiException(ErrorCode.SUBSCRIPTION_NOT_FOUND);
        }
    }

    public List<MySubscription> getMySubscriptions(int subscriberId) {
        return subscriptionsRepository.findMySubscriptions(subscriberId);
    }

    public SubscriptionCheck checkSubscription(int subscriberId, int producerId) {
        return new SubscriptionCheck(subscriptionsRepository.exists(subscriberId, producerId));
    }

    public FollowersPage getFollowers(int producerId, int afterId, int limit) {
        List<Integer> rows = subscriptionsRepository.findFollowers(producerId, afterId, limit + 1);
        boolean hasMore = rows.size() > limit;
        List<Integer> items = hasMore ? rows.subList(0, limit) : rows;
        Integer nextAfterId = hasMore && !items.isEmpty() ? items.getLast() : null;
        return new FollowersPage(items, nextAfterId);
    }

    public SubscriptionStats getStats(int userId) {
        return subscriptionsRepository.getStats(userId);
    }
}
