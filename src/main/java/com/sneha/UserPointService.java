package com.sneha;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sneha.exceptions.SystemException;
import com.sneha.pointservice.GetUserPointRequest;
import com.sneha.pointservice.GetUserPointResponse;
import com.sneha.pointservice.UserPointData;
import com.sneha.rankingservice.UserPoint;
import com.sneha.userservice.GetUsersResponse;
import com.sneha.userservice.User;
import lombok.AllArgsConstructor;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class UserPointService {

    private final OkHttpClient client;
    private ObjectMapper objectMapper;
//    private UserServiceConfiguration userServiceConfiguration;
//    private PointServiceConfigurations pointServiceConfigurations;

    private ConfigurationProperties configurationProperties;

    public List<UserPoint> aggregateUserData(int minPoint) throws Exception {
        List<UserPoint> result = new ArrayList<>();

        List<User> users = getUserData();

        Map<String, String> usersMap = new HashMap<>();
        for (User u : users) {
            usersMap.put(u.getId(), u.getName());
        }

        List<UserPointData> userPointData = getUserPoints(minPoint);
        Map<String, Integer> userPointMap = new HashMap<>();

        for (UserPointData u : userPointData) {
            userPointMap.put(u.getId(), u.getPoint().getValue());
        }

        Set<String> keys = userPointMap.keySet();
        for (String i : keys) {
            result.add(UserPoint.newBuilder().setName(usersMap.get(i)).setPoint(userPointMap.get(i)).build());
        }

        return result;

    }

    private List<User> getUserData() throws Exception {
        String url = "http://"+ configurationProperties.getUserServiceConfig().getHost() + configurationProperties.getUserServiceConfig().getPath();//localhost:8099/users";
    //    System.out.printf("dcveveve %s" ,url);

        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();


            String rawResponse = responseBody.string();


            GetUsersResponse usersResponse = objectMapper.readValue(rawResponse, GetUsersResponse.class);


            return usersResponse.getUsersList();
        } catch (Exception e) {
            throw new SystemException("Something went wrong. in user service, please try again later");
        }
    }

    private List<UserPointData> getUserPoints(int minPoint) throws Exception {
        String url = "http://" + configurationProperties.getPointServiceConfig().getHost()+configurationProperties.getPointServiceConfig().getPath(); //localhost:8092/point/users";
     //   System.out.printf("dcveveve %s" ,url);
        GetUserPointRequest getUserPointRequest = GetUserPointRequest.newBuilder().setMinPoint(minPoint).build();


        String rawRequest = objectMapper.writeValueAsString(getUserPointRequest);


        RequestBody requestBody = RequestBody.create(rawRequest, MediaType.parse("application/json"));


        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try {

            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();


            String rawResponse = responseBody.string();

            GetUserPointResponse userPointResponse = objectMapper.readValue(
                    rawResponse, GetUserPointResponse.class);

            return userPointResponse.getPointsList();

        } catch (Exception e) {
            throw new SystemException("Something went wrong, please try again later");
        }
    }
}
