package com.yunhalee.walkerholic;

import com.yunhalee.walkerholic.activity.domain.ActivityRepository;
import com.yunhalee.walkerholic.common.service.S3ImageUploader;
import com.yunhalee.walkerholic.follow.domain.FollowRepository;
import com.yunhalee.walkerholic.product.domain.ProductRepository;
import com.yunhalee.walkerholic.review.domain.ReviewRepository;
import com.yunhalee.walkerholic.user.domain.UserRepository;
import com.yunhalee.walkerholic.useractivity.domain.UserActivityRepository;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MockBeans {

    @MockBean
    protected UserActivityRepository userActivityRepository;

    @MockBean
    protected ActivityRepository activityRepository;

    @MockBean
    protected UserRepository userRepository;

    @MockBean
    protected ProductRepository productRepository;

    @MockBean
    protected ReviewRepository reviewRepository;

    @MockBean
    protected S3ImageUploader s3ImageUploader;

    @MockBean
    protected FollowRepository followRepository;
}
