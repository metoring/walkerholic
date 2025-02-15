package com.yunhalee.walkerholic.useractivity.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yunhalee.walkerholic.ServiceTest;
import com.yunhalee.walkerholic.activity.domain.Activity;
import com.yunhalee.walkerholic.user.domain.Level;
import com.yunhalee.walkerholic.user.domain.Role;
import com.yunhalee.walkerholic.user.domain.User;
import com.yunhalee.walkerholic.useractivity.domain.ActivityStatus;
import com.yunhalee.walkerholic.useractivity.domain.UserActivity;
import com.yunhalee.walkerholic.useractivity.dto.UserActivityRequest;
import com.yunhalee.walkerholic.useractivity.dto.UserActivityResponse;
import com.yunhalee.walkerholic.useractivity.dto.UserActivityResponses;
import com.yunhalee.walkerholic.util.CommonMethod;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

class UserActivityServiceTests extends ServiceTest {

    @InjectMocks
    private UserActivityService userActivityService;

    private User user;
    private Activity activity;
    private UserActivity userActivity;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .firstname("testFirstName")
            .lastname("TestLastName")
            .email("test@example.com")
            .password("12345678")
            .role(Role.USER).build();
        activity = Activity.builder()
            .name("testActivity")
            .score(500)
            .description("test-activity").build();
        userActivity = UserActivity.builder()
            .user(user)
            .activity(activity)
            .status(ActivityStatus.ONGOING).build();
    }

    @ParameterizedTest
    @CsvSource({"false", "false", "true"})
    @DisplayName("주어진 상태에 따른 새로운 사용자 액티비티를 생성하고 사용자 레벨을 조정한다.")
    void create_user_activity(boolean finished) {
        //given
        UserActivityRequest userActivityRequest = userActivityRequest(1, 1, finished);
        //when
        when(userService.findUserById(anyInt())).thenReturn(user);
        when(activityService.findActivityById(anyInt())).thenReturn(activity);
        UserActivityResponse response = userActivityService.create(userActivityRequest);
        //then
        assertEquals(response.getActivityId(), activity.getId());
        assertEquals(response.isFinished(), finished);
        assertEquals(user.getLevel(), finished ? Level.Silver : Level.Starter);
    }

    @Test
    @DisplayName("사용자 액티비티 상태가 완료되면 상태를 업데이트 하고 점수에 따라 레벨도 업데이트 한다.")
    void update_user_activity() {
        //given
        UserActivityRequest userActivityRequest = userActivityRequest(1, 1, true);
        //when
        when(userActivityRepository.findById(anyInt()))
            .thenReturn(java.util.Optional.of(userActivity));
        when(userService.findUserById(anyInt())).thenReturn(user);
        when(activityService.findActivityById(anyInt())).thenReturn(activity);
        UserActivityResponse response = userActivityService.update(userActivityRequest, 1);
        //then
        assertEquals(response.isFinished(), true);
        assertEquals(user.getLevel(), Level.Silver);
    }

    @Test
    @DisplayName("사용자의 사용자액티비티 목록을 조회한다.")
    public void getUserActivitiesByUserId() {
        //given
        UserActivity firstUserActivity = CommonMethod
            .userActivity(user, activity, ActivityStatus.ONGOING);
        UserActivity secondUserActivity = CommonMethod
            .userActivity(user, activity, ActivityStatus.FINISHED);

        //when
        Page<UserActivity> userActivityPage = new PageImpl<>(
            Arrays.asList(firstUserActivity, secondUserActivity));
        when(userActivityRepository.findByUserId(any(), anyInt())).thenReturn(userActivityPage);
        UserActivityResponses responses = userActivityService.userActivities(1, 1);
        //then
        assertThat(responses.getActivities().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("사용자 액티비트를 삭제하고 삭제된 액티비티 점수에 따라 사용자 레벨을 수정한다.")
    public void deleteUserActivity() {
        //given
        userActivity = CommonMethod.userActivity(user, activity, ActivityStatus.FINISHED);
        user.updateLevel(userActivity);

        //when
        when(userService.findUserById(anyInt())).thenReturn(user);
        when(userActivityRepository.findById(anyInt()))
            .thenReturn(java.util.Optional.of(userActivity));
        userActivityService.deleteUserActivity(1, 1);
        //then
        verify(userActivityRepository).delete(any());
        assertEquals(user.getLevel(), Level.Starter);
    }

    private UserActivityRequest userActivityRequest(Integer userId, Integer activityId,
        boolean finished) {
        return UserActivityRequest.builder()
            .userId(userId)
            .activityId(activityId)
            .finished(finished).build();
    }
}

