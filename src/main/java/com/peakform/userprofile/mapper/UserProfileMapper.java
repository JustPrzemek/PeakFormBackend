package com.peakform.userprofile.mapper;

import com.peakform.security.user.model.User;
import com.peakform.userprofile.dto.EditUserDataDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    EditUserDataDTO userToEditUserDataDTO(User user);

}
