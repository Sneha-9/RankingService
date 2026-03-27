import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;

import com.sneha.Main;

import com.sneha.pointservice.GetUserPointResponse;
import com.sneha.pointservice.Point;
import com.sneha.pointservice.UserPointData;
import com.sneha.rankingservice.GetUserRankRequest;
import com.sneha.rankingservice.GetUserRankResponse;
import com.sneha.rankingservice.UserPoint;
import com.sneha.userservice.GetUsersResponse;
import com.sneha.userservice.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@SpringBootTest(
        classes = Main.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Testcontainers
@AutoConfigureRestTestClient
public class E2E {
    @Autowired
    private ObjectMapper objectMapper;

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @Autowired
    private RestTestClient restTestClient;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("pointservice.host", () -> wireMockServer.baseUrl().replace("http://",""));
        registry.add("userservice.host", () -> wireMockServer.baseUrl().replace("http://",""));
    }

    @Test
    void shouldGetUsersWithPoint() throws JsonProcessingException {
        List<User> users = new ArrayList<>();
        users.add(User.newBuilder().setEmail("sneha@abc.com").setId("123").setName("sneha").build());
        GetUsersResponse getUsersResponse = GetUsersResponse.newBuilder().addAllUsers(users).build();
// calling user service
        wireMockServer.stubFor(
                get("/users").willReturn(
                        ok(
                                objectMapper.writeValueAsString(getUsersResponse)
                        ))
        );

       List<UserPointData> userPointData = new ArrayList<>();
       userPointData.add(UserPointData.newBuilder().setPoint(Point.newBuilder().setValue(10).build()).setId("123").build());
       GetUserPointResponse getUserPointResponse = GetUserPointResponse.newBuilder().addAllPoints(userPointData).build();
// calling point service
        wireMockServer.stubFor(
                post("/point/users").willReturn(
                        ok(
                                objectMapper.writeValueAsString(getUserPointResponse)
                        ))
        );

        GetUserRankRequest getUserRankRequest = GetUserRankRequest.newBuilder().setMinPoint(10).build();


        RestTestClient.ResponseSpec responseSpec = restTestClient.post()
                .uri("/rank/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(getUserRankRequest)
                .exchange();

        responseSpec.expectStatus().isOk();

        GetUserRankResponse getUserRankResponse = responseSpec.expectBody(GetUserRankResponse.class).returnResult().getResponseBody();

        List<UserPoint> userPointsExpected = new ArrayList<>();
        userPointsExpected.add(UserPoint.newBuilder().setName("sneha").setPoint(10).build());

        Assertions.assertEquals(userPointsExpected, getUserRankResponse.getUserPointListList());
    }
}

