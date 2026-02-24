package com.sneha;

import com.sneha.rankingservice.UserPoint;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RankingService {

    private UserPointService userPointService;

    List<UserPoint> getUserRank(int minPoint) throws Exception {

        return  userPointService.aggregateUserData(minPoint);
    }

}
