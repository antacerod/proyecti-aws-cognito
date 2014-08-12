package com.proyecti.blog.cognito.services;

import com.amazonaws.services.s3.model.ObjectListing;
import com.proyecti.blog.cognito.configuration.enumeration.IdentityProvider.IdentityProviderEnum;
import com.proyecti.blog.cognito.services.dto.WifResponseDTO;

public interface LoginService 
{
	String getToken(String key, String value);

	WifResponseDTO loginWithCredentialsProvider(String token, IdentityProviderEnum identityProvider);

	WifResponseDTO loginWithAssumeRoleWithWebIdentity(String token, IdentityProviderEnum identityProvider);

	ObjectListing requestToS3(WifResponseDTO myDTO);
}
