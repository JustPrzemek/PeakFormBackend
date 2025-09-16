package com.peakform.followers.service;

import com.peakform.followers.dto.FollowDTO;
import com.peakform.pages.PagedResponse;
import org.springframework.data.domain.Pageable;


public interface FollowersService {

    PagedResponse<FollowDTO> getMyFollowers(Pageable pageable, String username);

    PagedResponse<FollowDTO> getMyFollowing(Pageable pageable);

}