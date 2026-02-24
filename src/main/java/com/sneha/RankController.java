package com.sneha;

import com.google.gson.Gson;
import com.sneha.rankingservice.GetUserRankRequest;
import com.sneha.rankingservice.GetUserRankResponse;
import com.sneha.rankingservice.UserPoint;
import lombok.AllArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class RankController {

    private RankingService rankingService;

    @PostMapping(value = "/rank/users", produces = "application/json")
    GetUserRankResponse getUserRank(@RequestBody GetUserRankRequest getUserRankRequest) throws Exception {
       List<UserPoint> response = rankingService.getUserRank(getUserRankRequest.getMinPoint());
       return GetUserRankResponse.newBuilder().addAllUserPointList(response).build();
    }

}
