package com.proyecti.blog.cognito.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.amazonaws.services.s3.model.ObjectListing;
import com.proyecti.blog.cognito.configuration.enumeration.IdentityProvider.IdentityProviderEnum;
import com.proyecti.blog.cognito.services.LoginService;
import com.proyecti.blog.cognito.services.dto.WifResponseDTO;

/**
 * @author Antonio Acevedo (Proyecti)

 * @see <a href="http://docs.aws.amazon.com/cognitoidentity/latest/APIReference/Welcome.html">http://docs.aws.amazon.com/cognitoidentity/latest/APIReference/Welcome.html</a>
 * @see <a href="http://docs.aws.amazon.com/STS/latest/APIReference/API_AssumeRoleWithWebIdentity.html">http://docs.aws.amazon.com/STS/latest/APIReference/API_AssumeRoleWithWebIdentity.html</a>
 * @see <a href="http://aws.amazon.com/articles/4617974389850313">http://aws.amazon.com/articles/4617974389850313</a>
 */
@RestController("v1.login.controller")
public class LoginController 
{    
	@Autowired
	private LoginService loginService;
	
	
	@RequestMapping("/getOpenIdToken")
    public String getOpenIdToken() 
	{
		return loginService.getToken(null, null);
    }
	
	@RequestMapping(value = "/login", params = "type")
    public String testLoginWithCredentialsProvider(
    		@RequestParam(value = "openIdToken", required=true) String openIdToken,
    		@RequestParam(value = "type", required=true) String type)
	{
		final WifResponseDTO wifResponseDTO;
		
		if(type.equalsIgnoreCase("credentials"))
		{
			wifResponseDTO = loginService.loginWithCredentialsProvider(openIdToken, IdentityProviderEnum.AWS_COGNITO);
		}
		else
		{
			wifResponseDTO = loginService.loginWithAssumeRoleWithWebIdentity(openIdToken, IdentityProviderEnum.AWS_COGNITO);			
		}

		final ObjectListing objectListing = loginService.requestToS3(wifResponseDTO);

		return objectListing.getBucketName();
	}
}
