package com.proyecti.blog.cognito.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.auth.WebIdentityFederationSessionCredentialsProvider;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentityClient;
import com.amazonaws.services.cognitoidentity.model.GetIdRequest;
import com.amazonaws.services.cognitoidentity.model.GetIdResult;
import com.amazonaws.services.cognitoidentity.model.GetOpenIdTokenRequest;
import com.amazonaws.services.cognitoidentity.model.GetOpenIdTokenResult;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClient;
import com.amazonaws.services.securitytoken.model.AssumeRoleWithWebIdentityRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleWithWebIdentityResult;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.proyecti.blog.cognito.configuration.enumeration.IdentityProvider.IdentityProviderEnum;
import com.proyecti.blog.cognito.services.dto.WifResponseDTO;

@Service("v1.login.service")
public class LoginServiceImpl implements LoginService
{
	private static final String MY_BUCKET_NAME = "MY.BUCKET.NAME";
	private static final String ROLE_ARN = "THIS.IS.THE.ROLE.ARN";
	
	@Autowired
	private AmazonCognitoIdentityClient amazonCognitoIdentityClient;
	
	@Autowired
	private AWSSecurityTokenServiceClient awsSecurityTokenServiceClient;
	
	
	@Override
	public String getToken(String key, String value) 
	{
		/*
		 * 	To provide end-user credentials, first make an unsigned call to GetId. 
		 * 	If the end user is authenticated with one of the supported identity providers, 
	 	 * 	set the Logins map with the identity provider token.
	 	 */
		final GetIdRequest getIdRequest = new GetIdRequest();
		
		if(!(StringUtils.isEmpty(key) && StringUtils.isEmpty(value)))
		{
			getIdRequest.addLoginsEntry(key, value);
		}
		
		final GetIdResult getIdResult = amazonCognitoIdentityClient.getId(getIdRequest);
		
		// GetId returns a unique identifier for the user.
		final String identityId = getIdResult.getIdentityId();
		
		/*
		 *	Next, make an unsigned call to GetOpenIdToken, which returns the OpenID token 
		 * 	necessary to call STS and retrieve AWS credentials
		 *	This call expects the same Logins map as the GetId call, as well as the IdentityID 
		 *	originally returned by GetId. 
		 */
		
		final GetOpenIdTokenRequest getOpenIdTokenRequest = new GetOpenIdTokenRequest();
		getOpenIdTokenRequest.addLoginsEntry("key", "value");
		getOpenIdTokenRequest.setIdentityId(identityId);
		
		final GetOpenIdTokenResult getOpenIdTokenResult = amazonCognitoIdentityClient.getOpenIdToken(getOpenIdTokenRequest);

		final String token = getOpenIdTokenResult.getToken();
		
		System.out.print("Token retrieved: " + token);
		
		return token;
	}    

	
	@Override
	public WifResponseDTO loginWithAssumeRoleWithWebIdentity(String token, IdentityProviderEnum identityProvider) 
	{
		/*
		 *	The token returned by GetOpenIdToken can be passed to the STS operation 
		 *	AssumeRoleWithWebIdentity to retrieve AWS credentials.
		 *
		 *	The ProviderId parameter for an STS call with a Cognito OpenID token is 
		 *	cognito-identity.amazonaws.com.
		 */ 
		final AssumeRoleWithWebIdentityRequest request = new AssumeRoleWithWebIdentityRequest()
	        .withWebIdentityToken(token)
	        .withProviderId(identityProvider.getValueAsString())
	        .withRoleArn(ROLE_ARN)
	        .withRoleSessionName("wifSession")
	        .withDurationSeconds(300);
		
		final AssumeRoleWithWebIdentityResult result = awsSecurityTokenServiceClient.assumeRoleWithWebIdentity(request);
		final Credentials stsCredentials = result.getCredentials();
		final BasicSessionCredentials credentials = new BasicSessionCredentials(stsCredentials.getAccessKeyId(),
		                                                                  stsCredentials.getSecretAccessKey(),
		                                                                  stsCredentials.getSessionToken());
		
		return new WifResponseDTO(result.getSubjectFromWebIdentityToken(), new AmazonS3Client(credentials));
	}
	
	@Override
	public WifResponseDTO loginWithCredentialsProvider(String token, IdentityProviderEnum identityProvider) 
	{
		/*
		 * If you call AWS STS as above, you still need to extract the identity provider's credentials 
		 * and user identifier from the response object and pass them to your individual service clients. 
		 * 
		 * The application also needs to call AssumeRoleWithWebIndentity each time the AWS credentials 
		 * expire. 
		 * 
		 * To help with managing WIF credentials, Java SDK offer a credentials provider that wraps the
		 * calls to AssumeRoleWithWebIdentity. 
		 */
		final WebIdentityFederationSessionCredentialsProvider wif =
		         new WebIdentityFederationSessionCredentialsProvider(token, 
		        		 											 identityProvider.getValueAsString(),
		                                                             ROLE_ARN);

		return new WifResponseDTO(wif.getSubjectFromWIF(), new AmazonS3Client(wif));
	}

	@Override
	public ObjectListing requestToS3(WifResponseDTO myDTO) 
	{
		final ListObjectsRequest req = new ListObjectsRequest();
		
		req.setBucketName(MY_BUCKET_NAME);
		req.setPrefix( myDTO.getSubjectFromWIF() + "/" );
				
		return myDTO.getAmazonS3Client().listObjects(req);
	}
}
